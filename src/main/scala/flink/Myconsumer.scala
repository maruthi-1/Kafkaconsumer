package flink

import java.util.Properties

import org.apache.flink.api.common.functions.RuntimeContext
import org.apache.flink.runtime.state.filesystem.FsStateBackend
import org.apache.flink.streaming.api.environment.CheckpointConfig.ExternalizedCheckpointCleanup
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.{CheckpointingMode, TimeCharacteristic}
import org.apache.flink.streaming.connectors.elasticsearch.{ElasticsearchSinkFunction, RequestIndexer}
import org.apache.flink.streaming.connectors.elasticsearch7.ElasticsearchSink
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer
import org.apache.http.HttpHost
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.client.Requests
import org.elasticsearch.common.xcontent.XContentType
object Myconsumer {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime)
    env.getConfig.setAutoWatermarkInterval(1000L)
    env.enableCheckpointing(1000)
    val config = env.getCheckpointConfig
    config.enableExternalizedCheckpoints(ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION)
    // set mode to exactly-once (this is the default)
    env.getCheckpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE)

    // make sure 500 ms of progress happen between checkpoints
    env.getCheckpointConfig.setMinPauseBetweenCheckpoints(500)

    // checkpoints have to complete within one minute, or are discarded
    env.getCheckpointConfig.setCheckpointTimeout(60000)
    env.setStateBackend(new FsStateBackend("file:///home/maruthi/Checkpoint"))

    // allow only one checkpoint to be in progress at the same time
    env.getCheckpointConfig.setMaxConcurrentCheckpoints(1)
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", "localhost:9092")
    properties.setProperty("zookeeper.connect", "localhost:2181")
    properties.setProperty("group.id", "test")

val co = new FlinkKafkaConsumer("mytopic", new Myserialization, properties)
    val stream = env.addSource(co)
      .assignTimestampsAndWatermarks(new TimeStampAssiger)
    val values = stream.map(x => {
      val key =  x.key
      val value = x.value
      val obj = Utils.fromJson[Empdata](value)
      val timeStamp = obj.timeStamp
      val t=TimeStampConverter.convert(timeStamp)
      val check =TimeStampConverter.time(t)
      (key,check, 1)
    })

val keyValue = values.keyBy(0)

    val tumblingWindow = keyValue.timeWindow(Time.minutes(15))
      .allowedLateness(Time.minutes(120))
                  //.trigger(CustTrigger of (10))
      .sum(2).name("tumblingwindow")

      val httpHosts = new java.util.ArrayList[HttpHost]
      httpHosts.add(new HttpHost("127.0.0.1", 9200, "http"))
    case class Obj(event: String,
                   timestamp: String,
                   count: Int)

      val esSinkBuilder = new ElasticsearchSink.Builder[(String, String, Int)](httpHosts, new ElasticsearchSinkFunction[(String, String,Int)] {

        override def process(element: (String, String,Int), ctx: RuntimeContext, indexer: RequestIndexer): Unit = {

          val obj = Obj(element._1, element._2,element._3)

          val str = Utils.toJson[Obj](obj)

          val rqst: IndexRequest = Requests.indexRequest
            .index("widas")
            .`type`("hyderabad")
            .id(element._1+""+element._2)
            .source(str, XContentType.JSON)

          indexer.add(rqst)
        }
      }
      )
    esSinkBuilder.setBulkFlushMaxActions(1)

    tumblingWindow.addSink(esSinkBuilder.build())
    env.execute("abc")
}
}
