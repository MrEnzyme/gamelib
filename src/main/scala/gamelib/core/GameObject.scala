package gamelib.core

abstract class GameObject
{
    private var dead = false
    final def setDead() = dead = true
    final def isDead = dead

    def update(deltaTime: Double)
}
