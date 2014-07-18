package gamelib.network

import com.esotericsoftware.kryonet.Server

class GameServer extends Runnable
{
    val networkingServer = new Server

    def start() = new Thread(this, "GameServer").start()
    def run()
    {
        val timePerFrame = 20L
        while(true)
        {
            //get network input

            //update game instance

            //get network output

        }
    }
}