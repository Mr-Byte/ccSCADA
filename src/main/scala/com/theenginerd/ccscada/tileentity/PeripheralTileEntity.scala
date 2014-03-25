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

package com.theenginerd.ccscada.tileentity

import net.minecraft.tileentity.TileEntity
import com.theenginerd.ccscada.peripheral.Peripheral
import net.minecraft.nbt.NBTTagCompound

abstract class PeripheralTileEntity
    extends TileEntity
    with Peripheral
{
    override def getWorld = worldObj
    override def xCoordinate = xCoord
    override def yCoordinate = yCoord
    override def zCoordinate = zCoord
    override def blockId = getBlockType.blockID

    override def updateEntity() =
    {
        super.updateEntity()
        update()
    }

    override def writeToNBT(nbt: NBTTagCompound) =
    {
        super.writeToNBT(nbt)
        nbt.setString("friendlyName", friendlyName)
    }

    override def readFromNBT(nbt: NBTTagCompound) =
    {
        super.readFromNBT(nbt)
        friendlyName = nbt.getString("friendlyName")

        execute
        {
            load()
        }
    }
}
