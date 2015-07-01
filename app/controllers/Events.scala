package controllers

import javax.inject.{Inject, Singleton}

import models.Event
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.Cursor
import services.EventService
import utils.TimeUtil

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by Simon Wang on 2015/6/2.
 */
@Singleton
class Events @Inject()(eventService: EventService) extends Controller with MongoController {
  def eventCollection: JSONCollection = db.collection[JSONCollection]("events")

  def createEvent() = Action.async(parse.json) {
    request =>
      request.body.validate[models.Event].map {
        event =>
          if (event.userId != "") {
            // `user` is an instance of the case class `models.User`
            eventService.insertEvent(eventCollection, event).map {
              lastError =>
                Logger.debug(s"Successfully inserted with LastError: $lastError")
                Created(s"Event Created")
            }
          } else {
            Future.successful(BadRequest("User not login"))
          }
      }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  def listEvents(userId: String) = Action.async {
    Logger.debug(s"userId is $userId")
    val futureEventList: Future[List[Event]] = eventService.findEventsByUser(eventCollection, userId)
    val futureEventArray =
      futureEventList.map {
        events => {
          Logger.debug(s"events: $events size: ${events.size}")
          Json.arr(events)
        }
      }

    futureEventArray.map {
      eventArray =>
        Logger.debug(s"result $eventArray")
        Ok(eventArray(0))
    }
  }

  def listTodayEvents(userId: String) = Action.async {
    val today = TimeUtil.todayDateZeroHour
    val time = today.getTime
    val futureEventList: Future[List[Event]] = eventService.findEventsByDate(eventCollection, userId, time)
    val futureEventArray =
      futureEventList.map {
        events => {
          Logger.debug(s"events: $events size: ${events.size}")
          Json.arr(events)
        }
      }
    futureEventArray.map {
      eventArray =>
        Logger.debug(s"result $eventArray")
        Ok(eventArray(0))
    }
  }

  def deleteEvent(id: String) = Action.async {
    Logger.debug(s"delete event by id $id")
    val futureResult = eventService.deleteEvent(eventCollection, id)
    Logger.debug(s"delete done")
    futureResult.map {
      lastError =>
        Logger.debug(s"remove event successfully with error $lastError")
        Ok
    }
  }
}
