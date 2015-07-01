package models

import java.text.SimpleDateFormat
import java.util.{TimeZone, Calendar, Date}

import play.api.Logger
import play.api.libs.json._
import reactivemongo.bson.BSONObjectID
import play.modules.reactivemongo.json.BSONFormats._
import utils.TimeUtil

/**
 * Created by Simon Wang on 2015/6/1.
 */
case class Event(_id:BSONObjectID, title: String, startTime: Date, endTime: Date, userId: String, taskId: Option[String])

object Event {

  implicit val eventReads = new Reads[Event] {
    override def reads(json: JsValue): JsResult[Event] = {
      val sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.000Z'")
      sdf.setTimeZone(TimeZone.getTimeZone("GMT"))
      Logger.debug(s"json value is $json")
      val _id = (json \ "_id").asOpt[BSONObjectID] match {
        case Some(oid) => oid
        case _ => BSONObjectID.generate
      }
      JsSuccess(Event(
        _id,
        (json \ "title").as[String],
        sdf.parse((json \ "startTime").as[String]),
        sdf.parse((json \ "endTime").as[String]),
        (json \ "userId").as[String],
        (json \ "taskId").asOpt[String]
      ))
    }
  }

  implicit val eventWrites = Json.writes[Event]
}

object MongoEvent {
  implicit val eventMongoReads = new Reads[Event] {
    override def reads(json: JsValue): JsResult[Event] = {
      Logger.debug(s"result: $json")
      JsSuccess(Event(
        (json \ "_id").as[BSONObjectID],
        (json \ "title").as[String],
        TimeUtil.convertToGMTTime((json \ "startTime").as[Long]),
        TimeUtil.convertToGMTTime((json \ "endTime").as[Long]),
        (json \ "userId").as[String],
        (json \ "taskId").asOpt[String]
      ))
    }
  }
  implicit val eventWrites = new Writes[Event]{
    override def writes(o: Event): JsValue = {
      val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
      Logger.debug(s"title ${o.title}")
      Json.obj(
        "_id" -> o._id,
        "title" -> o.title,
        "startTime" -> sdf.format(o.startTime),
        "endTime" -> sdf.format(o.endTime),
        "duration" ->  BigDecimal((o.endTime.getTime - o.startTime.getTime).toDouble / (1000 * 3600)).setScale(2, BigDecimal.RoundingMode.HALF_UP),
        "percent" ->  (BigDecimal((o.endTime.getTime - o.startTime.getTime).toDouble / (10 * 3600 * 22)).setScale(2, BigDecimal.RoundingMode.HALF_UP).toString() + "%"),
        "taskId" ->  o.taskId
      )
    }
  }
}


