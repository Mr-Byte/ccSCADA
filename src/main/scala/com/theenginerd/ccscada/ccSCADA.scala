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

import cpw.mods.fml.common.{FMLLog, Mod}
import cpw.mods.fml.common.network.NetworkMod
import cpw.mods.fml.common.Mod.EventHandler
import cpw.mods.fml.common.event.{FMLInitializationEvent, FMLPreInitializationEvent}
import net.minecraftforge.common.Configuration

@Mod(name = NAME, modid = ID, version=VERSION, dependencies=DEPENDENCIES, modLanguage="scala")
@NetworkMod(clientSideRequired = true, serverSideRequired = false)
object ccSCADA
{
    @EventHandler
    def preInitialize(event: FMLPreInitializationEvent) =
    {
        val configurationFile = event.getSuggestedConfigurationFile
        val configuration = new Configuration(configurationFile)

        try
        {
            configuration.load()
            block.loadBlockIds(configuration)
        }
        catch
        {
            case exception: Exception =>
                FMLLog.warning(s"$NAME failed to load its configuration file.")
        }
        finally
        {
            configuration.save()
        }
    }

    @EventHandler
    def initialize(event: FMLInitializationEvent) =
    {
        block.registerBlocks()
        crafting.registerRecipes()
    }
}
