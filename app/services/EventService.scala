package services

import models.Event
import play.api.libs.json.Json
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.Cursor
import reactivemongo.core.commands.LastError

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by Simon Wang on 2015/6/26.
 */
abstract class EventService {
  def insertEvent(eventCollection: JSONCollection, event: Event): Future[LastError]

  def findEventsByUser(eventCollection: JSONCollection, userId: String): Future[List[Event]]

  def findEventsByDate(eventCollection: JSONCollection, userId: String, time: Long): Future[List[Event]]

  def deleteEvent(eventCollection: JSONCollection, id: String): Future[LastError]
}

class EventServiceImpl extends EventService {
  override def insertEvent(eventCollection: JSONCollection, event: Event): Future[LastError] = {
    import models.Event._
    eventCollection.insert(event)
  }

  override def findEventsByDate(eventCollection: JSONCollection, userId: String, time: Long): Future[List[Event]] = {
    import models.MongoEvent._
    val cursor: Cursor[Event] = eventCollection.find(Json.obj("userId" -> userId, "startTime" -> Json.obj {
      "$gt" -> time
    })).
      sort(Json.obj("startTime" -> -1)).cursor[Event]
    cursor.collect[List]()
  }

  override def findEventsByUser(eventCollection: JSONCollection, userId: String): Future[List[Event]] = {
    import models.MongoEvent._
    val cursor: Cursor[Event] = eventCollection.find(Json.obj("userId" -> userId)).
      sort(Json.obj("startTime" -> -1)).cursor[Event]
    cursor.collect[List]()
  }

  override def deleteEvent(eventCollection: JSONCollection, id: String): Future[LastError] = {
    eventCollection.remove(Json.obj(("_id" -> Json.obj("$oid" -> id))))
  }
}



