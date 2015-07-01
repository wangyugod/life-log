package utils

import java.text.SimpleDateFormat
import java.util.{TimeZone, Calendar, Date}

/**
 * Created by Simon Wang on 2015/6/15.
 */
object TimeUtil {
  def convertToGMTTime(time: Long): Date = {
    val calendar = Calendar.getInstance()
    calendar.setTimeZone(TimeZone.getTimeZone("UTC"))
    calendar.setTimeInMillis(time)
    calendar.getTime
  }

  def todayDateZeroHour = {
    val sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00")
    val d = sdf.format(new Date())
    sdf.parse(d)
  }

  def datePlusHour(date: Date, hour: Int) = {
    val cal = Calendar.getInstance
    cal.setTime(date)
    cal.add(Calendar.HOUR_OF_DAY, hour)
    cal.getTime
  }
}
