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

package com.theenginerd.ccSCADA.peripheral

import net.minecraftforge.common.ForgeDirection

object Conversions
{
    def stringToDirection(side: String): ForgeDirection =
        side.toLowerCase match
        {
            case "top" => ForgeDirection.UP
            case "bottom" => ForgeDirection.DOWN
            case "left" => ForgeDirection.WEST
            case "right" => ForgeDirection.EAST
            case "front" => ForgeDirection.SOUTH
            case "back" => ForgeDirection.NORTH
        }
}
