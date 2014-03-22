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

import dan200.computer.api.{ILuaContext, IComputerAccess, IPeripheral}
import java.util.concurrent.ConcurrentLinkedQueue
import scala.concurrent.{Await, Promise, Future}
import scala.concurrent.duration.Duration
import net.minecraft.world.World

trait Peripheral extends IPeripheral
{
    private val updateQueue: ConcurrentLinkedQueue[() => Unit] = new ConcurrentLinkedQueue[() => Unit]()

    def getWorld: World

    def execute[T](body: => T): Future[T] =
    {
        val promise: Promise[T] = Promise()

        updateQueue.add(() =>
                        {
                            val result = body
                            promise.success(result)
                        })

        promise.future
    }

    def await[T](body: => T): T =
    {
        Await.result(execute(body), Duration.Inf)
    }
    
    def update() =
    {
        while(!updateQueue.isEmpty)
        {
            updateQueue.remove()()
        }
    }

    class MethodCallback(val methodName: String, callback: (IComputerAccess, Array[AnyRef]) => Array[AnyRef])
    {
        def apply(computer: IComputerAccess, arguments: Array[AnyRef]): Array[AnyRef] = callback(computer, arguments)
    }

    private var methods: Array[MethodCallback] = Array()

    def registerMethod(name: String, method: (IComputerAccess, Array[AnyRef]) => Array[AnyRef]) =
    {
        methods :+= new MethodCallback(name, method)
    }

    def getMethodNames: Array[String] =
        methods.map(method => method.methodName).toArray

    def callMethod(computer: IComputerAccess, context: ILuaContext, method: Int, arguments: Array[AnyRef]): Array[AnyRef] =
    {
        methods(method)(computer, arguments)
    }

    def canAttachToSide(side: Int): Boolean =
        true

    def attach(computer: IComputerAccess) =
        ()

    def detach(computer: IComputerAccess) =
        ()
}
