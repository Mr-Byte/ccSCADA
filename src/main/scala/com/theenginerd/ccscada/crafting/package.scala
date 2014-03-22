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

package com.theenginerd.ccscada

import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.item.{Item, ItemStack}
import net.minecraft.block.Block

import com.theenginerd.ccscada.block.blocks
import com.theenginerd.ccscada.block.redstoneControllerPeripheralName

package object crafting
{
    def registerRecipes() =
    {
        GameRegistry.addRecipe(new ItemStack(blocks(redstoneControllerPeripheralName), 1),
                               "SRS",
                               "RIR",
                               "SRS",
                               'S': Character, new ItemStack(Block.stone),
                               'R': Character, new ItemStack(Item.redstone),
                               'I': Character, new ItemStack(Item.ingotIron)
                               )
    }
}
