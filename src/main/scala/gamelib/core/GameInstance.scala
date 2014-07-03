package gamelib.core

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.SynchronizedQueue

class GameInstance
{
    private val gameObjects = new ListBuffer[GameObject]
    private val newObjects = new SynchronizedQueue[GameObject]
    private val deadObjects = new ListBuffer[GameObject]

    def addObject(newObject: GameObject) = newObjects.enqueue(newObject)

    def update(deltaTime: Double)
    {
        gameObjects ++= newObjects
        newObjects.clear()
        deadObjects.clear()

        for(gameObject <- gameObjects)
        {
            updateGameObject(gameObject, deltaTime)
            if(gameObject.isDead) deadObjects += gameObject
        }

        gameObjects --= deadObjects
    }

    protected def updateGameObject(gameObject: GameObject, deltaTime: Double) = gameObject.update(deltaTime)
}
