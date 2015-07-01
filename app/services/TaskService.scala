package services

import models.Task
import play.api.libs.json.Json
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.Cursor
import reactivemongo.core.commands.LastError

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by Simon Wang on 2015/6/25.
 */
abstract class TaskService {
  def findTaskById(taskCollection: JSONCollection, taskId: String): Future[Option[Task]]
  def findTaskByUser(taskCollection: JSONCollection, userId: String, time: Long): Future[List[Task]]
  def createTask(taskCollection: JSONCollection, task: Task): Future[LastError]
}


class TaskServiceImpl extends TaskService{
  override def findTaskById(taskCollection: JSONCollection, taskId: String): Future[Option[Task]] = {
    import models.MongoTask._
    val cursor: Cursor[Task] = taskCollection.find(Json.obj(("_id" -> Json.obj("$oid" -> taskId)))).
      sort(Json.obj("startTime" -> -1)).cursor[Task]
    cursor.headOption
  }

  override def createTask(taskCollection: JSONCollection, task: Task): Future[LastError] = {
    taskCollection.insert(task)
  }

  override def findTaskByUser(taskCollection: JSONCollection, userId: String, time: Long): Future[List[Task]] = {
    import models.MongoTask._
    val cursor: Cursor[Task] = taskCollection.find(Json.obj("userId" -> userId, "endTime" -> Json.obj {
      "$gte" -> time
    })).sort(Json.obj("startTime" -> -1)).cursor[Task]
    cursor.collect[List]()
  }
}
