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

import net.minecraft.tileentity.TileEntity
import net.minecraftforge.common.ForgeDirection
import dan200.computer.api.IComputerAccess
import powercrystals.minefactoryreloaded.api.rednet.IRedNetNetworkContainer
import net.minecraft.block.Block
import com.theenginerd.ccSCADA.util.AsType

trait RedNetCableSupport extends Peripheral
{
    self: TileEntity =>

    private val defaultValues = Vector.fill(16)(0)
    private var outputValues: Map[ForgeDirection, Vector[Int]] = Map()
    private var inputValues: Map[ForgeDirection, Vector[Int]] = Map()

    //Bundled Input method handlers
    registerMethod("getBundledInput", getBundledInput)
    registerMethod("getBundledOutput", getBundledOutput)
    registerMethod("setBundledOutput", setBundledOutput)
    registerMethod("testBundledInput", testBundledInput)

    //Individual subnet handlers
    registerMethod("setSubnetOutput", setSubnetOutput)
    registerMethod("getSubnetOutput", getSubnetOutput)
    registerMethod("getSubnetInput", getSubnetInput)


    def getInputValues(side: ForgeDirection) =
        this.synchronized
        {
            inputValues.get(side).getOrElse(defaultValues)
        }

    def getOutputValues(side: ForgeDirection) =
        this.synchronized
        {
            outputValues.get(side).getOrElse(defaultValues)
        }

    def setInputValues(side: ForgeDirection, values: Vector[Int]) =
        this.synchronized
        {
            inputValues += (side -> values)
        }

    def setOutputValues(side: ForgeDirection, values: Vector[Int]) =
    {
        this.synchronized
        {
            outputValues += (side -> values)
        }

        addUpdate(() => notifyNeighborOnSideOfUpdate(side))
    }

    private def notifyNeighborOnSideOfUpdate(outputSide: ForgeDirection) =
    {
        def updateNeighborCable(x: Int, y: Int, z: Int) =
        {
            //For porting forward to 1.7.2
            val worldObj = getWorldObj
            val blockId = worldObj.getBlockId(x, y, z)
            val block = Option(Block.blocksList(blockId))

            for(AsType(cable: IRedNetNetworkContainer) <- block)
                cable.updateNetwork(worldObj, x, y, z)
        }

        outputSide match
        {
            case ForgeDirection.EAST =>
                updateNeighborCable(xCoord + 1, yCoord, zCoord)

            case ForgeDirection.WEST =>
                updateNeighborCable(xCoord - 1, yCoord, zCoord)

            case ForgeDirection.UP =>
                updateNeighborCable(xCoord, yCoord + 1, zCoord)

            case ForgeDirection.DOWN =>
                updateNeighborCable(xCoord, yCoord - 1, zCoord)

            case ForgeDirection.NORTH =>
                updateNeighborCable(xCoord, yCoord, zCoord - 1)

            case ForgeDirection.SOUTH =>
                updateNeighborCable(xCoord, yCoord, zCoord + 1)

            case _ =>
        }
    }

    private def getBundledInput(computer: IComputerAccess, arguments: Array[AnyRef]): Array[AnyRef] =
    {
        arguments match
        {
            case Array(side: String, _*) =>
                Array(convertToBundleState(getInputValues(Conversions.stringToDirection(side))).asInstanceOf[AnyRef])
            case _ =>
                throw new Exception("Invalid argument (side).")
        }
    }

    private def getBundledOutput(computer: IComputerAccess, arguments: Array[AnyRef]) =
    {
        arguments match
        {
            case Array(side: String, _*) =>
                Array(convertToBundleState(getOutputValues(Conversions.stringToDirection(side))).asInstanceOf[AnyRef])
            case _ =>
                throw new Exception("Invalid argument (side).")
        }
    }

    private def convertToBundleState(array: Vector[Int]) =
    {
        array.view
             .zipWithIndex
             .foldLeft(0)
             {
                 case (accumulator, (value, index)) =>
                     accumulator | (if (value > 0) 0x1 << index else 0)
             }
    }

    private def setBundledOutput(computer: IComputerAccess, arguments: Array[AnyRef]): Array[AnyRef] =
    {
        arguments match
        {
            case Array(sideName: String, colors: java.lang.Double, _*) =>
                val value = colors.toInt
                var result = defaultValues

                for (index <- 0 to 15)
                {
                    if (((value >> index) & 0x1) == 0x1)
                    {
                        result = result.updated(index, value)
                    }
                }

                val side = Conversions.stringToDirection(sideName)
                setOutputValues(side, result)

                null
            case _ =>
                throw new Exception("Invalid arguments (side, colors).")
        }
    }

    private def testBundledInput(computer: IComputerAccess, arguments: Array[AnyRef]): Array[AnyRef] =
    {
        arguments match
        {
            case Array(sideName: String, color: java.lang.Double, _*) =>
                val index = Math.getExponent(color.toDouble)
                val inputValues = getInputValues(Conversions.stringToDirection(sideName))

                if(index < 0 || index > inputValues.length)
                    throw new Exception("Invalid argument (color).")

                Array((inputValues(index) > 0).asInstanceOf[AnyRef])

            case _ =>
                throw new Exception("Invalid arguments (side, color).")
        }
    }

    private def setSubnetOutput(computer: IComputerAccess, arguments: Array[AnyRef]): Array[AnyRef] =
    {
        arguments match
        {
            case Array(sideName: String, subnet: java.lang.Double, value: java.lang.Double, _*) =>
                val side = Conversions.stringToDirection(sideName)
                val outputs = getOutputValues(side)

                setOutputValues(side, outputs.updated(subnet.toInt, value.toInt))

                null

            case _ =>
                throw new Exception("Invalid arguments (side, subnet)")
        }
    }

    private def getSubnetOutput(computer: IComputerAccess, arguments: Array[AnyRef]): Array[AnyRef] =
    {
        arguments match
        {
            case Array(sideName: String, subnet: java.lang.Double, _*) =>
                val outputs = getOutputValues(Conversions.stringToDirection(sideName))

                Array(outputs(subnet.toInt).asInstanceOf[AnyRef])

            case _ =>
                throw new Exception("Invalid arguments (side, subnet)")
        }
    }

    private def getSubnetInput(computer: IComputerAccess, arguments: Array[AnyRef]): Array[AnyRef] =
    {
        arguments match
        {
            case Array(sideName: String, subnet: java.lang.Double, _*) =>
                val outputs = getInputValues(Conversions.stringToDirection(sideName))

                Array(outputs(subnet.toInt).asInstanceOf[AnyRef])

            case _ =>
                throw new Exception("Invalid arguments (side, subnet)")
        }
    }

    private def performSubnetUpdateForSide(outputSide: ForgeDirection, subnet: Int) = ???
}
