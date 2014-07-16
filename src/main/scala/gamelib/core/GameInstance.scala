package gamelib.core

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.SynchronizedQueue

class GameInstance
{
    protected val gameObjects = new ListBuffer[GameObject]
    protected val newObjects = new SynchronizedQueue[GameObject]
    protected val deadObjects = new ListBuffer[GameObject]

    protected val gameEvents = new ListBuffer[GameEvent]

    def addObject(newObject: GameObject) = newObjects.enqueue(newObject)
    protected def updateObject(gameObject: GameObject, deltaTime: Double): Traversable[GameEvent] = gameObject.update(this, deltaTime)
    protected def removeObject(gameObject: GameObject): Unit = gameObjects -= gameObject

    final def getObjects = gameObjects.toList

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
