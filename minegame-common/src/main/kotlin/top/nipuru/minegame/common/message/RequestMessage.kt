package top.nipuru.minegame.common.message

import com.alipay.remoting.config.ConfigManager
import com.alipay.remoting.exception.CodecException
import com.alipay.remoting.serialization.SerializerManager
import java.io.Serializable

fun createRequest(request: Any): RequestMessage {
    val serializer = ConfigManager.serializer()
    val serializer1 = SerializerManager.getSerializer(serializer.toInt())
    val bytes = serializer1.serialize(request)
    return RequestMessage(request.javaClass.name, serializer.toInt(), bytes)
}

fun createResponse(response: Any): ResponseMessage {
    val serializer = ConfigManager.serializer()
    val serializer1 = SerializerManager.getSerializer(serializer.toInt())
    val bytes = serializer1.serialize(response)
    return ResponseMessage(response.javaClass.name, serializer.toInt(), bytes)
}

interface RequestMessageContainer {
    val request: RequestMessage
}

open class BaseMessage(val className: String, private val serializer: Int, private val data: ByteArray) : Serializable {
    fun <T> getData(): T = SerializerManager.getSerializer(serializer).deserialize(data, className)
}

class RequestMessage(className: String, serializer: Int, data: ByteArray) :
    BaseMessage(className, serializer, data)

class ResponseMessage(className: String, serializer: Int, data: ByteArray) :
    BaseMessage(className, serializer, data)
