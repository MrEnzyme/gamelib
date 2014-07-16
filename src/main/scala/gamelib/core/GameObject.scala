package gamelib.core

abstract class GameObject
{
    private var dead = false
    final def setDead() = dead = true
    final def isDead = dead

    def update(gameInstance: GameInstance, deltaTime: Double): Traversable[GameEvent]
}
