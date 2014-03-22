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

import cpw.mods.fml.common.{Loader, FMLLog}
import net.minecraft.block.Block

trait BlockFactory
{
    def createBlock(blockId: Int): Block
}

object BlockFactory
{
    def createRedstoneControllerPeripheralBlock(blockId: Int): Block =
    {
        FMLLog.info("Creating RedstoneControllerPeripheralBlock type.")

        val factory = Loader.isModLoaded("MineFactoryReloaded") match
        {
            case true => RedNetCableControllerPeripheralFactory
            case false => RedstoneControllerPeripheralBlockFactory
        }

        factory.createBlock(blockId)
    }
}