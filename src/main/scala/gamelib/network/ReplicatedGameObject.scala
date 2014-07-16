package gamelib.network

import gamelib.core.{GameInstance, GameEvent, GameObject}

abstract class ReplicatedGameObject extends GameObject with Replicated
{
    def updateLocal(gameInstance: GameInstance, deltaTime: Double): Traversable[GameEvent]
}
