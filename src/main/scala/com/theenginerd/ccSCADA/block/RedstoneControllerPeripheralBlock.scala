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

import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.creativetab.CreativeTabs
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.world.{World, IBlockAccess}
import net.minecraftforge.common.ForgeDirection
import com.theenginerd.ccSCADA.tileentity.RedstoneControllerPeripheralTileEntity

abstract class RedstoneControllerPeripheralBlock(blockId: Int)
    extends BlockContainer(blockId, Material.rock)
{
    setHardness(0.5F)
    setCreativeTab(CreativeTabs.tabMisc)
    setUnlocalizedName("ccSCADA.redstoneControllerPeripheral")

    GameRegistry.registerBlock(this, "redstoneControllerPeripheral")

    //TODO: Load texture icon.

    override def getFlammability(world: IBlockAccess, x: Int, y: Int, z: Int, metadata: Int, facing: ForgeDirection) = 0

    override def isFlammable(world: IBlockAccess, x: Int, y: Int, z: Int, metadata: Int, facing: ForgeDirection) = false

    override def isOpaqueCube = true

    override def isBlockSolidOnSide(world: World, x: Int, y: Int, z: Int, side: ForgeDirection) = true

    override def canProvidePower = true

    override def canConnectRedstone(world: IBlockAccess, x: Int, y: Int, z: Int, side: Int) = true

    override def isProvidingWeakPower(world: IBlockAccess, x: Int, y: Int, z: Int, side: Int): Int =
    {
        world.getBlockTileEntity(x, y, z).asInstanceOf[RedstoneControllerPeripheralTileEntity].getPowerOutputForSide(getNormalizedDirection(side))
    }

    override def isProvidingStrongPower(world: IBlockAccess, x: Int, y: Int, z: Int, side: Int): Int =
    {
        world.getBlockTileEntity(x, y, z).asInstanceOf[RedstoneControllerPeripheralTileEntity].getPowerOutputForSide(getNormalizedDirection(side))
    }

    private def getNormalizedDirection(side: Int) =
        ForgeDirection.getOrientation(side) match
        {
            case direction @ (ForgeDirection.UP | ForgeDirection.DOWN) => direction.getOpposite
            case direction => direction.getOpposite
        }
}
