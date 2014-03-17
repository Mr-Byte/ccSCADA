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

trait RedNetCablePeripheral extends RedstoneControllerPeripheral
{
    self: TileEntity =>

    private val defaultValues = Array.fill(16)(0)
    private var outputValues: Map[ForgeDirection, Array[Int]] = Map()
    private var inputValues: Map[ForgeDirection, Array[Int]] = Map()

    registerMethod("getBundledInput", getBundledInput)
    registerMethod("getBundledOutput", getBundledOutput)
    registerMethod("setBundledOutput", setBundledOutput)

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

    def setInputValues(side: ForgeDirection, values: Array[Int]) =
        this.synchronized
        {
            inputValues += (side -> values)
        }

    def setOutputValues(side: ForgeDirection, values: Array[Int]) =
    {
        this.synchronized
        {
            outputValues += (side -> values)
        }

        addUpdate(() => onOutputValuesUpdate(side))
    }

    private def getBundledInput(computer: IComputerAccess, arguments: Array[AnyRef]): Array[AnyRef] =
    {
        Array(arguments match
              {
                  case Array(side: String, _*) =>
                      convertToBundleState(getInputValues(Conversions.stringToDirection(side))).asInstanceOf[AnyRef]
                  case _ =>
                      throw new Exception("Invalid argument (side).")
              })
    }

    private def getBundledOutput(computer: IComputerAccess, arguments: Array[AnyRef]) =
    {
        Array(arguments match
              {
                  case Array(side: String, _*) =>
                      convertToBundleState(getOutputValues(Conversions.stringToDirection(side))).asInstanceOf[AnyRef]
                  case _ =>
                      throw new Exception("Invalid argument (side).")
              })
    }

    private def convertToBundleState(array: Array[Int]) =
    {
        array.view
        .zipWithIndex
        .foldLeft(0)
        {
            case (accumulator, (value, index)) =>
                accumulator + (if (value > 0) Math.pow(2, index).toInt else 0)
        }
    }

    private def setBundledOutput(computer: IComputerAccess, arguments: Array[AnyRef]): Array[AnyRef] =
    {
        arguments match
        {
            case Array(side: String, colors: java.lang.Double, _*) =>
                val value = colors.toInt
                val result = defaultValues.clone()

                for (index <- 0 to 15)
                {
                    if (((value >> index) & 0x1) == 0x1)
                    {
                        result(index) = 15
                    }
                }

                setOutputValues(Conversions.stringToDirection(side), result)

                null
            case _ =>
                throw new Exception("Invalid arguments (side, colors).")
        }
    }

    private def onOutputValuesUpdate(outputSide: ForgeDirection) =
    {
        def updateNeighborCable(x: Int, y: Int, z: Int) =
        {
            //For porting forward to 1.7.2
            val worldObj = getWorldObj
            val blockId = worldObj.getBlockId(x, y, z)
            val block = Block.blocksList(blockId)

            block match
            {
                case cable: IRedNetNetworkContainer =>
                    cable.updateNetwork(worldObj, x, y, z)

                case _ =>
            }
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
}
