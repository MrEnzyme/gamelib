package gamelib.network

import gamelib.core.GameInstance

class MockReplicatedGameObject extends ReplicatedGameObject
{
    @replicate("a") var a = 5

    def update(gameInstance: GameInstance, deltaTime: Double) = Nil
}
