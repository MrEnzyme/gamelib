package gamelib.network

abstract class MessageType

case object CreateMessage extends MessageType
case object UpdateMessage extends MessageType
case object DestroyMessage extends MessageType

case class ReplicationMessage(messageType: MessageType, data: Array[Byte])