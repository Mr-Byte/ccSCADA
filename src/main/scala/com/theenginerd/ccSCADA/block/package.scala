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

package com.theenginerd.ccSCADA

import net.minecraftforge.common.Configuration
import net.minecraft.block.Block

package object block
{
    val redstoneControllerPeripheralName = "redstoneControllerPeripheral"

    private var redstoneControllerPeripheralId = 3410

    var blocks: Map[String, Block] = Map()

    def loadBlockIds(configuration: Configuration) =
    {
        def getBlockId(propertyName: String, defaultValue: Int) =
        {
            val property = configuration.getBlock(propertyName, defaultValue)
            property.getInt
        }

        redstoneControllerPeripheralId = getBlockId(redstoneControllerPeripheralName, redstoneControllerPeripheralId)
    }

    def registerBlocks() =
    {
        blocks += redstoneControllerPeripheralName -> RedstoneControllerPeripheralBlock(redstoneControllerPeripheralId)
    }
}
