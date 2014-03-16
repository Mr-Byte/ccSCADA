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

package com.theenginerd.ccSCADA.tileentity

import net.minecraft.tileentity.TileEntity
import com.theenginerd.ccSCADA.peripheral.RedstoneControllerPeripheral
import net.minecraftforge.common.ForgeDirection
import net.minecraft.block.Block
import powercrystals.minefactoryreloaded.api.rednet.IRedNetNetworkContainer

class RedstoneControllerPeripheralTileEntity
    extends TileEntity
    with RedstoneControllerPeripheral
{
    override def updateEntity()
    {
        super.updateEntity()
        update()
    }

    override def onOutputValuesUpdate(outputSide: ForgeDirection, values: Array[Int]) =
    {
        def updateNeighborCable(x: Int, y: Int, z: Int) =
        {
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
