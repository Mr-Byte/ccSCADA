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

package com.theenginerd.ccscada.block.factory

import cpw.mods.fml.common.registry.GameRegistry
import com.theenginerd.ccscada.block.RedstoneControllerPeripheralBlock
import com.theenginerd.ccscada.tileentity.RedstoneControllerPeripheralTileEntity
import com.theenginerd.ccscada.block.redstoneBundleProvider.RedNetConnectable
import com.theenginerd.ccscada.peripheral.RedNetCableSupport
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

object RedNetCableControllerPeripheralFactory extends BlockFactory
{
    private class RedstoneControllerPeripheralTileEntityWithRedNetCableSupport
        extends RedstoneControllerPeripheralTileEntity
        with RedNetCableSupport

    def createBlock(blockId: Int) =
    {
        GameRegistry.registerTileEntity(classOf[RedstoneControllerPeripheralTileEntityWithRedNetCableSupport], "redstoneControllerPeripheral")

        new RedstoneControllerPeripheralBlock(blockId) with
            RedNetConnectable
        {
            override def createNewTileEntity(world: World): TileEntity =
                new RedstoneControllerPeripheralTileEntityWithRedNetCableSupport
        }
    }
}
