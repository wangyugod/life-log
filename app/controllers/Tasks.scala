package controllers

import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.{Inject, Singleton}

import models.{Event, Task}
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.Cursor
import reactivemongo.bson.BSONObjectID
import services.{EventService, TaskService}
import utils.TimeUtil
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

/**
 * Created by Simon Wang on 2015/6/15.
 */
@Singleton
class Tasks @Inject()(taskService: TaskService)(eventService: EventService) extends Controller with MongoController {

  def taskCollection: JSONCollection = db.collection[JSONCollection]("tasks")

  def eventCollection: JSONCollection = db.collection[JSONCollection]("events")

  def createTask() = Action.async(parse.json) {
    request =>
      import models.Task._
      request.body.validate[models.Task].map {
        task =>
          Logger.debug(s"task is :$task")
          if (task.userId != "") {
            // `user` is an instance of the case class `models.User`
            taskService.createTask(taskCollection, task).map {
              lastError =>
                Logger.debug(s"Successfully inserted with LastError: $lastError")
                Created(s"Task Created")
            }
          } else {
            Future.successful(BadRequest("User not login"))
          }
      }.getOrElse(Future.successful(BadRequest("invalid json")))
  }


  def listTasks(userId: String) = Action.async {
    Logger.debug(s"userId is $userId")
    val time = TimeUtil.todayDateZeroHour.getTime
    val futureEventList: Future[List[Task]] = taskService.findTaskByUser(taskCollection, userId, time)
    Logger.debug("prepare to get")
    val futureTaskArray =
      futureEventList.map {
        tasks => {
          Logger.debug(s"task: $tasks size: ${tasks.size}")
          Json.arr(tasks)
        }
      }

    futureTaskArray.map {
      taskArray =>
        Logger.debug(s"result $taskArray")
        Ok(taskArray(0))
    }
  }

  def finishDailyTask(taskId: String) = Action.async {
    val futureTask = taskService.findTaskById(taskCollection, taskId)
    futureTask.flatMap {
      taskOption =>
        taskOption match {
          case Some(task) => {
            val today = TimeUtil.todayDateZeroHour
            val event = Event(BSONObjectID.generate, task.title, today, TimeUtil.datePlusHour(today, 2), task.userId, Some(taskId))
            eventService.insertEvent(eventCollection, event).map {
              lastError =>
                Logger.debug(s"Successfully insert event from task with LastError: $lastError")
                Created(s"Event Created")
            }
          }
          case _ => Future.successful(BadRequest(s"No Task Found with id $taskId"))
        }
    }
  }


}