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
import powercrystals.minefactoryreloaded.api.rednet.IRedNetNetworkContainer
import net.minecraft.block.Block
import com.theenginerd.ccscada.util.AsType
import com.theenginerd.ccscada.util.Conversions._

trait RedNetCableSupport extends Peripheral
{
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
            inputValues.getOrElse(side, defaultValues)
        }

    def getOutputValues(side: ForgeDirection) =
        this.synchronized
        {
            outputValues.getOrElse(side, defaultValues)
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

        execute
        {
            notifyNeighborOnSideOfUpdate(side)
        }
    }

    private def notifyNeighborOnSideOfUpdate(outputSide: ForgeDirection) =
    {
        def updateNeighborCable(x: Int, y: Int, z: Int) =
        {
            val blockId = getWorld.getBlockId(x, y, z)
            val block = Option(Block.blocksList(blockId))

            for(AsType(cable: IRedNetNetworkContainer) <- block)
                cable.updateNetwork(getWorld, x, y, z)
        }


        updateNeighborCable(xCoordinate + outputSide.offsetX, yCoordinate + outputSide.offsetY, zCoordinate + outputSide.offsetZ)
    }

    private def getBundledInput(computer: IComputerAccess, arguments: Array[AnyRef]): Array[AnyRef] =
    {
        arguments match
        {
            case Array(sideName: String, _*) =>
                Array(convertToBundleState(getInputValues(sideName)).asInstanceOf[AnyRef])
            case _ =>
                throw new Exception("Invalid argument (side).")
        }
    }

    private def getBundledOutput(computer: IComputerAccess, arguments: Array[AnyRef]) =
    {
        arguments match
        {
            case Array(sideName: String, _*) =>
                Array(convertToBundleState(getOutputValues(sideName)).asInstanceOf[AnyRef])
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
                        result = result.updated(index, 15)
                    }
                }

                setOutputValues(sideName, result)

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
                val inputValues = getInputValues(sideName)

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
                val outputs = getOutputValues(sideName)

                setOutputValues(sideName, outputs.updated(subnet.toInt, value.toInt))

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
                val outputs = getOutputValues(sideName)

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
                val outputs = getInputValues(sideName)

                Array(outputs(subnet.toInt).asInstanceOf[AnyRef])

            case _ =>
                throw new Exception("Invalid arguments (side, subnet)")
        }
    }
}
