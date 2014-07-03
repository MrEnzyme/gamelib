package gamelib.network

import gamelib.core.GameInstance
import scala.collection.mutable.ListBuffer

class ReplicatedGameInstance extends GameInstance
{
    private val replicatedObjects = new ListBuffer[ReplicatedGameObject]

    def addObject(gameObject: ReplicatedGameObject)
    {
        super.addObject(gameObject)
        replicatedObjects += gameObject
    }
}