package controllers

import jp.t2v.lab.play2.auth.{LoginLogout, OptionalAuthElement}

import play.api.libs.json.Json
import play.api.mvc.Controller
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.Cursor
import play.api.libs.json._

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by Simon Wang on 2015/5/25.
 */
trait AuthenticationController extends OptionalAuthElement with AuthConfigImpl with LoginLogout with MongoController {
  self: Controller =>

  import models.JsonFormats._
  import models._

  def collection: JSONCollection = db.collection[JSONCollection]("users")

  override def resolveUser(email: Id)(implicit ctx: ExecutionContext) = {
    val cursor: Cursor[models.User] = collection.find(Json.obj("email" -> email)).cursor[models.User]
    val futureUser: Future[Option[models.User]] = cursor.headOption
    futureUser
  }

}
