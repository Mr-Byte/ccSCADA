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

package com.theenginerd.ccSCADA.block.redstoneBundleProvider

import powercrystals.minefactoryreloaded.api.rednet.{RedNetConnectionType, IConnectableRedNet}
import net.minecraft.block.Block
import net.minecraft.world.World
import net.minecraftforge.common.ForgeDirection
import com.theenginerd.ccSCADA.peripheral.RedstoneControllerPeripheral

trait RedNetBundleProvider extends IConnectableRedNet
{
    self: Block =>

    def getConnectionType(world: World, x: Int, y: Int, z: Int, side: ForgeDirection): RedNetConnectionType =
    {
        RedNetConnectionType.PlateAll
    }

    def getOutputValues(world: World, x: Int, y: Int, z: Int, side: ForgeDirection): Array[Int] =
    {
        world.getBlockTileEntity(x, y, z).asInstanceOf[RedstoneControllerPeripheral].getOutputValues(side)
    }

    def getOutputValue(world: World, x: Int, y: Int, z: Int, side: ForgeDirection, subnet: Int): Int =
    {
        world.getBlockTileEntity(x, y, z).asInstanceOf[RedstoneControllerPeripheral].getOutputValues(side)(subnet)
    }

    def onInputsChanged(world: World, x: Int, y: Int, z: Int, side: ForgeDirection, inputValues: Array[Int]) =
    {
        world.getBlockTileEntity(x, y, z).asInstanceOf[RedstoneControllerPeripheral].setInputValues(side, inputValues)
    }

    def onInputChanged(world: World, x: Int, y: Int, z: Int, side: ForgeDirection, inputValue: Int)
    { }
}
