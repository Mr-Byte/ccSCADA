/*
 * Copyright 2014 Joshua R. Rodgers
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ========================================================================
 */

package com.theenginerd.ccscada.peripheral

import net.minecraftforge.common.ForgeDirection
import dan200.computer.api.IComputerAccess
import net.minecraft.tileentity.TileEntity
import net.minecraft.block.Block
import com.theenginerd.ccscada.util.BlockUtility

trait RedstoneControllerPeripheral extends Peripheral
{
    self: TileEntity =>

    private var powerOutputValues: Map[ForgeDirection, Int] = Map()

    registerMethod("getInput", getInput)
    registerMethod("setOutput", setOutput)
    registerMethod("getOutput", getOutput)
    registerMethod("getAnalogInput", getAnalogInput)
    registerMethod("setAnalogOutput", setAnalogOutput)
    registerMethod("getAnalogOutput", getAnalogOutput)
    registerMethod("getComparatorInput", getComparatorInput)

    def getPowerOutputForSide(direction: ForgeDirection): Int =
        this.synchronized
        {
            powerOutputValues.getOrElse(direction, 0)
        }

    def setPowerOutputForSide(direction: ForgeDirection, power: Int) =
    {
        this.synchronized
        {
            powerOutputValues += (direction -> clamp(power, 0, 15))
        }

        execute
        {
            BlockUtility.notifyNeighborsOnSide(getWorld, xCoord, yCoord, zCoord, getBlockType.blockID, direction)
        }
    }

    private def clamp(value: Int, min: Int, max: Int) =
        value match
        {
            case _ if value > max => max
            case _ if value < min => min
            case _ => value
        }

    private def getInput(computer: IComputerAccess, arguments: Array[AnyRef]): Array[AnyRef] =
    {
        arguments match
        {
            case Array(sideName: String, _*) =>
                Array(getInputForSide(Conversions.stringToDirection(sideName)).exists(result => if (result > 0) true else false): java.lang.Boolean)

            case _ =>
                throw new Exception("Invalid arguments (side).")
        }
    }

    private def getInputForSide(side: ForgeDirection) =
    {
        val inputStrength =
            await
            {
                getBlockOnSide(side).flatMap
                {
                    case (block, (x, y, z)) =>
                        Some(getWorld.getIndirectPowerLevelTo(x, y, z, side.getOpposite.ordinal()))
                }
            }

        inputStrength
    }

    private def getOutput(computer: IComputerAccess, arguments: Array[AnyRef]): Array[AnyRef] =
    {
        arguments match
        {
            case Array(sideName: String, _*) =>
                Array(getPowerOutputForSide(Conversions.stringToDirection(sideName)) > 0: java.lang.Boolean)

            case _ =>
                throw new Exception("Invalid arguments (side).")
        }
    }

    private def setOutput(computer: IComputerAccess, arguments: Array[AnyRef]): Array[AnyRef] =
    {
        arguments match
        {
            case Array(sideName: String, isPowered: java.lang.Boolean, _*) =>
                setPowerOutputForSide(Conversions.stringToDirection(sideName), if(isPowered) 15 else 0)

                null
            case _ =>
                throw new Exception("Invalid arguments (side, powerState).")
        }
    }

    private def getAnalogInput(computer: IComputerAccess, arguments: Array[AnyRef]): Array[AnyRef] =
    {
        arguments match
        {
            case Array(sideName: String, _*) =>
                Array(getInputForSide(Conversions.stringToDirection(sideName))
                          .getOrElse(0).asInstanceOf[AnyRef])

            case _ =>
                throw new Exception("Invalid arguments (side).")
        }
    }

    private def getAnalogOutput(computer: IComputerAccess, arguments: Array[AnyRef]): Array[AnyRef] =
    {
        arguments match
        {
            case Array(sideName: String, _*) =>
                Array(getPowerOutputForSide(Conversions.stringToDirection(sideName)): java.lang.Double)

            case _ =>
                throw new Exception("Invalid arguments (side).")
        }
    }

    private def setAnalogOutput(computer: IComputerAccess, arguments: Array[AnyRef]): Array[AnyRef] =
    {
        arguments match
        {
            case Array(sideName: String, power: java.lang.Double, _*) if power.toInt <= 15 && power.toInt >= 0 =>
                setPowerOutputForSide(Conversions.stringToDirection(sideName), power.toInt)

                null

            case Array(_, power: java.lang.Double, _*) if power.toInt > 15 || power.toInt < 0 =>
                throw new Exception("Power must be between 0 and 15.")

            case _ =>
                throw new Exception("Invalid arguments (side, power).")
        }
    }

    private def getComparatorInput(computer: IComputerAccess, arguments: Array[AnyRef]): Array[AnyRef] =
    {
        arguments match
        {
            case Array(sideName: String, _*) =>
                val comparatorStrength =
                    await
                    {
                        val side = Conversions.stringToDirection(sideName)
                        getBlockOnSide(side).flatMap
                        {
                            case (block, (x, y, z)) =>
                                if (block.hasComparatorInputOverride)
                                    Some(block.getComparatorInputOverride(getWorld, x, y, z, side.getOpposite.ordinal()))
                                else
                                    None
                        }
                    }

                comparatorStrength.map(result => Array(result.asInstanceOf[AnyRef])).getOrElse(null)

            case _ =>
                throw new Exception("Invalid arguments (side).")
        }
    }

    private def getBlockOnSide(direction: ForgeDirection) =
    {
        getCoordinatesForBlockOnSide(direction).flatMap
        {
            case (x, y, z) => Option(Block.blocksList(getWorld.getBlockId(x, y, z)), (x, y, z))
        }
    }

    private def getCoordinatesForBlockOnSide(direction: ForgeDirection) =
    {
        direction match
        {
            case ForgeDirection.UNKNOWN => None
            case _ => Some(xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ)
        }
    }

    override def detach(computer: IComputerAccess) =
    {
        execute
        {
            powerOutputValues = Map()
            BlockUtility.notifyAllNeighbors(getWorld, xCoord, yCoord, zCoord, getBlockType.blockID)
        }
    }
}
