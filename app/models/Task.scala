package models

import java.text.SimpleDateFormat
import java.util.{TimeZone, Date}

import play.api.Logger
import play.api.libs.json._
import reactivemongo.bson.BSONObjectID
import play.modules.reactivemongo.json.BSONFormats._
import utils.TimeUtil

/**
 * Created by Simon Wang on 2015/6/15.
 */
case class Task(_id: BSONObjectID, title: String, duration: Int, startTime: Date, endTime: Date, userId: String)


object Task{
  implicit val taskReads = new Reads[Task] {
    override def reads(json: JsValue): JsResult[Task] = {
      val sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.000Z'")
      sdf.setTimeZone(TimeZone.getTimeZone("UTC"))
      Logger.debug(s"task json value is $json")
      val _id = (json \ "_id").asOpt[BSONObjectID] match {
        case Some(oid) => oid
        case _ => BSONObjectID.generate
      }
      JsSuccess(Task(
        _id,
        (json \ "title").as[String],
        (json \ "duration").as[Int],
        sdf.parse((json \ "startTime").as[String]),
        sdf.parse((json \ "endTime").as[String]),
        (json \ "userId").as[String]
      ))
    }
  }

  implicit val taskWrites = Json.writes[Task]
}


object MongoTask {
  implicit val taskMongoReads = new Reads[Task] {
    override def reads(json: JsValue): JsResult[Task] = {
      Logger.debug(s"result: $json")
      JsSuccess(Task(
        (json \ "_id").as[BSONObjectID],
        (json \ "title").as[String],
        (json \ "duration").as[Int],
        TimeUtil.convertToGMTTime((json \ "startTime").as[Long]),
        TimeUtil.convertToGMTTime((json \ "endTime").as[Long]),
        (json \ "userId").as[String]
      ))
    }
  }
  implicit val taskWrites = new Writes[Task]{
    override def writes(o: Task): JsValue = {
      val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
      Logger.debug(s"title ${o.title}")
      Json.obj(
        "_id" -> o._id,
        "title" -> o.title,
        "startTime" -> sdf.format(o.startTime),
        "endTime" -> sdf.format(o.endTime),
        "duration" ->  o.duration
      )
    }
  }
}
