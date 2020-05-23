package flink

import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks
import org.apache.flink.streaming.api.watermark.Watermark




class TimeStampAssiger extends AssignerWithPeriodicWatermarks[Messages] with Serializable {
  override def extractTimestamp(e: Messages, prevElementTimestamp: Long) = {
    Utils.fromJson[Empdata](e.value).timeStamp
  }
  override def getCurrentWatermark(): Watermark = {
    new Watermark(System.currentTimeMillis)
  }
}
