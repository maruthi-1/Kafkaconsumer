package flink

import org.apache.flink.api.common.typeinfo.TypeInformation
import org.apache.flink.api.java.typeutils.TypeExtractor.getForClass
import org.apache.flink.streaming.connectors.kafka.KafkaDeserializationSchema
import org.apache.kafka.clients.consumer.ConsumerRecord

class Myserialization extends KafkaDeserializationSchema[Messages] {

  override def isEndOfStream(nextElement: Messages): Boolean = false

  override def deserialize(record: ConsumerRecord[Array[Byte], Array[Byte]]): Messages = {

    val key = new String(record.key())
    val value = new String(record.value())

    //  println("key : " + key )
    //  println("value : " + value )
    Messages(key, value)

  }

  override def getProducedType: TypeInformation[Messages] = getForClass(classOf[Messages])

}

