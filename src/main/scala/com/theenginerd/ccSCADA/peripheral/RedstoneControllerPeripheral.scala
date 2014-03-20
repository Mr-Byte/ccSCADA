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

package com.theenginerd.ccSCADA.peripheral

import net.minecraftforge.common.ForgeDirection
import dan200.computer.api.IComputerAccess
import net.minecraft.tileentity.TileEntity

trait RedstoneControllerPeripheral extends Peripheral
{
    self: TileEntity =>

    private var powerOutputValues: Map[ForgeDirection, Int] = Map()
    private var powerInputValues: Map[ForgeDirection, Int] = Map()

    registerMethod("getInput", getInput)
    registerMethod("setOutput", setOutput)
    registerMethod("getOutput", getOutput)

    registerMethod("getAnalogInput", getAnalogInput)
    registerMethod("setAnalogOutput", setAnalogOutput)
    registerMethod("getAnalogOutput", getAnalogOutput)

    def getPowerInputForSide(direction: ForgeDirection): Int =
        this.synchronized
        {
            powerInputValues.getOrElse(direction, 0)
        }

    def setPowerInputForSide(direction: ForgeDirection, power: Int) =
        this.synchronized
        {
            powerInputValues += (direction -> clamp(power, 0, 15))
        }

    def getPowerOutputForSide(direction: ForgeDirection): Int =
        this.synchronized
        {
            powerOutputValues.getOrElse(direction, 0)
        }

    def setPowerOutputForSide(direction: ForgeDirection, power: Int) =
        this.synchronized
        {
            powerOutputValues += (direction -> clamp(power, 0, 15))
            addUpdate(() =>
                      {
                          notifyNeighbors(direction)
                      })
        }

    private def notifyNeighbors(direction: ForgeDirection)
    {
        getWorldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, getBlockType.blockID)

        direction match
        {
            case ForgeDirection.DOWN => getWorldObj.notifyBlocksOfNeighborChange(xCoord, yCoord + 1, zCoord, getBlockType.blockID)
            case ForgeDirection.UP => getWorldObj.notifyBlocksOfNeighborChange(xCoord, yCoord - 1, zCoord, getBlockType.blockID)
            case ForgeDirection.WEST => getWorldObj.notifyBlocksOfNeighborChange(xCoord + 1, yCoord, zCoord, getBlockType.blockID)
            case ForgeDirection.EAST => getWorldObj.notifyBlocksOfNeighborChange(xCoord - 1, yCoord, zCoord, getBlockType.blockID)
            case ForgeDirection.SOUTH => getWorldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord - 1, getBlockType.blockID)
            case ForgeDirection.NORTH => getWorldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord + 1, getBlockType.blockID)
            case ForgeDirection.UNKNOWN => ()
        }
    }

    private def clamp(value: Int, min: Int, max: Int) =
    {
        if(value > max)
            max
        else if(value < min)
            min
        else
            value
    }

    private def getInput(computer: IComputerAccess, arguments: Array[AnyRef]): Array[AnyRef] =
    {
        arguments match
        {
            case Array(sideName: String, _*) =>
                Array((getPowerInputForSide(Conversions.stringToDirection(sideName)) > 0).asInstanceOf[AnyRef])

            case _ =>
                throw new Exception("Invalid arguments (side).")
        }
    }

    private def getOutput(computer: IComputerAccess, arguments: Array[AnyRef]): Array[AnyRef] =
    {
        arguments match
        {
            case Array(sideName: String, _*) =>
                Array((getPowerOutputForSide(Conversions.stringToDirection(sideName)) > 0).asInstanceOf[AnyRef])

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
                throw new Exception("Invalid arguments (side).")
        }
    }

    private def getAnalogInput(computer: IComputerAccess, arguments: Array[AnyRef]): Array[AnyRef] =
    {
        arguments match
        {
            case Array(sideName: String, _*) =>
                Array(getPowerInputForSide(Conversions.stringToDirection(sideName)).asInstanceOf[AnyRef])

            case _ =>
                throw new Exception("Invalid arguments (side).")
        }
    }

    private def getAnalogOutput(computer: IComputerAccess, arguments: Array[AnyRef]): Array[AnyRef] =
    {
        arguments match
        {
            case Array(sideName: String, _*) =>
                Array(getPowerOutputForSide(Conversions.stringToDirection(sideName)).asInstanceOf[AnyRef])

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

    override def detach(computer: IComputerAccess) =
    {
        this.synchronized
        {
            powerOutputValues = Map()
            powerInputValues = Map()
        }

        addUpdate(() =>
                  {
                      notifyNeighbors(ForgeDirection.UP)
                      notifyNeighbors(ForgeDirection.DOWN)
                      notifyNeighbors(ForgeDirection.EAST)
                      notifyNeighbors(ForgeDirection.WEST)
                      notifyNeighbors(ForgeDirection.NORTH)
                      notifyNeighbors(ForgeDirection.SOUTH)
                  })
    }
}
