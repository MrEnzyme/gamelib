package gamelib.network

import gamelib.core.GameInstance
import org.scalatest.{OneInstancePerTest, FunSuite}

class MockReplicatedGameObject extends ReplicatedGameObject
{
    @replicate("a") var a = 5

    def update(gameInstance: GameInstance, deltaTime: Double) =
    {
        a += 1
        List()
    }
    def updateLocal(gameInstance: GameInstance, deltaTime: Double) =
    {
        a -= 1
        List()
    }
}

class ReplicatedGameInstanceTest extends FunSuite with OneInstancePerTest
{
    val gameInstance = new ReplicatedGameInstance("gamelib")
    val kryo = KryoRegistrar.makeNewKryo()

    test("tracks newly added ReplicatedGameObjects")
    {
        val repObject = new MockReplicatedGameObject
        gameInstance.addObject(repObject)

        val messages = gameInstance.writeCreateMessages(kryo)
        assert(messages.size == 1)
        assert(messages.head.messageType == CreateMessage)
    }

    test("replicates object creations")
    {
        val repObject = new MockReplicatedGameObject
        gameInstance.addObject(repObject)

        val clientGameInstance = new ReplicatedGameInstance("gamelib")
        val creationMessages = gameInstance.writeCreateMessages(kryo)
        clientGameInstance.applyMessages(creationMessages, kryo)
        clientGameInstance.update(0)
        assert(clientGameInstance.getObjects.length == 1)
        assert(clientGameInstance.getObjects.head.isInstanceOf[MockReplicatedGameObject])
        assert(clientGameInstance.getObjects.head.asInstanceOf[MockReplicatedGameObject].a == 4)
    }
}
