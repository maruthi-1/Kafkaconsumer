package flink

import java.text.SimpleDateFormat
import java.util.{Date, TimeZone}


object TimeStampConverter {
  //  val t=System.currentTimeMillis()
  //  val c=convert(12)
  //  println(c)
  def convert(time: Long): String = {
    val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    format.setTimeZone(TimeZone.getTimeZone("UTC"))
    format.format(new Date(time))

  }

  def time(time: String) = {

    val mins = time.substring(14, 16).toInt

    val subs = mins match {

      case x if 0 <= x && x < 15 => 10
      case x if 15 <= x && x < 30 => 20
      case x if 30 <= x && x < 45 => 40
      case x if 45 <= x && x < 59 => 50
    }


    time.substring(0,14)+subs

  }
}
