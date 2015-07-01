package models

import javax.inject.Inject

import play.api.libs.json.Json
import reactivemongo.api.Cursor
import services.MongoDB._
import scala.concurrent.ExecutionContext.Implicits.global
import reactivemongo.bson._
import play.modules.reactivemongo.json._

import scala.concurrent.Future

/**
 * Created by Administrator on 2015/5/20.
 */
case class User(email: String, name: String, password: String, confirmPassword: String, salt: Option[String])

case class Login(email: String, password: String)

object JsonFormats {

  import play.api.libs.json.Json

  // Generates Writes and Reads for Feed and User thanks to Json Macros
  implicit val userFormat = Json.format[User]
  implicit val loginFormat = Json.format[Login]
}
