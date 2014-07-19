package gamelib.core

import gamelib.ui.Renderable

abstract class GameObject extends Renderable
{
    private var dead = false
    final def setDead() = dead = true
    final def isDead = dead

    def update(gameInstance: GameInstance, deltaTime: Double): Traversable[GameEvent]
}
