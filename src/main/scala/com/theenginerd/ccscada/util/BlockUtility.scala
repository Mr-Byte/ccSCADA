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

package com.theenginerd.ccscada.util

import net.minecraftforge.common.ForgeDirection
import net.minecraft.world.World

object BlockUtility
{
    def notifyNeighborsOnSide(world: World, xCoord: Int, yCoord: Int, zCoord: Int, blockID: Int, direction: ForgeDirection)
    {
        world.notifyBlockOfNeighborChange(xCoord, yCoord, zCoord, blockID)

        direction match
        {
            case ForgeDirection.UNKNOWN => ()
            case _ => world.notifyBlockOfNeighborChange(xCoord + direction.offsetX, yCoord + direction.offsetY, zCoord + direction.offsetZ, blockID)
        }
    }

    def notifyAllNeighbors(world: World, xCoord: Int, yCoord: Int, zCoord: Int, blockID: Int) =
    {
        notifyNeighborsOnSide(world, xCoord, yCoord, zCoord, blockID, ForgeDirection.UP)
        notifyNeighborsOnSide(world, xCoord, yCoord, zCoord, blockID, ForgeDirection.DOWN)
        notifyNeighborsOnSide(world, xCoord, yCoord, zCoord, blockID, ForgeDirection.EAST)
        notifyNeighborsOnSide(world, xCoord, yCoord, zCoord, blockID, ForgeDirection.WEST)
        notifyNeighborsOnSide(world, xCoord, yCoord, zCoord, blockID, ForgeDirection.NORTH)
        notifyNeighborsOnSide(world, xCoord, yCoord, zCoord, blockID, ForgeDirection.SOUTH)
    }
}
