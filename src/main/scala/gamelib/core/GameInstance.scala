package gamelib.core

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.SynchronizedQueue
import gamelib.network.InputEvent

class GameInstance
{
    protected val gameObjects = new ListBuffer[GameObject]
    protected val newObjects = new SynchronizedQueue[GameObject]
    protected val deadObjects = new ListBuffer[GameObject]

    protected val gameEvents = new ListBuffer[GameEvent]

    private val inputState = new InputState

    def addObject(newObject: GameObject) = newObjects.enqueue(newObject)
    protected def updateObject(gameObject: GameObject, deltaTime: Double): Traversable[GameEvent] = gameObject.update(this, deltaTime)
    protected def removeObject(gameObject: GameObject): Unit = gameObjects -= gameObject

    final def getObjects = gameObjects.toList

    final def getKeyState(key: Char) = inputState.getKey(key)
    final def getMouseButton(button: Int) = inputState.getMouseButton(button)
    final def getMousePos = inputState.getMousePos
    final def updateInputState(evt: InputEvent) = inputState.processInputEvent(evt)

    def update(deltaTime: Double)
    {
        gameObjects ++= newObjects
        newObjects.clear()
        deadObjects.clear()
        gameEvents.clear()

        for(gameObject <- gameObjects)
        {
            gameEvents ++= updateObject(gameObject, deltaTime)
            if(gameObject.isDead) deadObjects += gameObject
        }

        for(g <- gameEvents) g.execute(this)

        for(deadObj <- deadObjects) removeObject(deadObj)
    }
}
