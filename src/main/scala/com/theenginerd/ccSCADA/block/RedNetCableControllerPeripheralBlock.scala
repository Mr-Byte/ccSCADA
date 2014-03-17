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

package com.theenginerd.ccSCADA.block

import net.minecraft.world.World
import com.theenginerd.ccSCADA.tileentity.RedNetCableControllerPeripheralTileEntity
import net.minecraft.tileentity.TileEntity
import com.theenginerd.ccSCADA.block.redstoneBundleProvider.RedNetConnectable

class RedNetCableControllerPeripheralBlock(blockId: Int)
    extends RedstoneControllerPeripheralBlock(blockId)
    with RedNetConnectable
{
    override def createNewTileEntity(world: World): TileEntity =
    {
        new RedNetCableControllerPeripheralTileEntity()
    }
}
