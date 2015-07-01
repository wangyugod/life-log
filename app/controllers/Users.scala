package controllers

import javax.inject.{Inject, Singleton}

import jp.t2v.lab.play2.auth.OptionalAuthElement
import models.User
import org.apache.commons.lang3.RandomStringUtils
import play.api.Logger
import play.api.data.Form
import play.api.data.Forms._
import views.html
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.libs.json.Json
import reactivemongo.api.Cursor
import services.PasswordEncoder
import play.api.mvc._
import scala.concurrent.duration._


import scala.concurrent.{Await, ExecutionContext, Future}

/**
 * Created by Administrator on 2015/5/19.
 */
@Singleton
class Users @Inject()(passwordEncoder: PasswordEncoder) extends Controller with AuthenticationController with OptionalAuthElement {

  import models.JsonFormats._

  def createUser = Action.async(parse.json) {
    request =>
      /*
       * request.body is a JsValue.
       * There is an implicit Writes that turns this JsValue as a JsObject,
       * so you can call insert() with this JsValue.
       * (insert() takes a JsObject as parameter, or anything that can be
       * turned into a JsObject using a Writes.)
       */
      request.body.validate[models.User].map {
        user =>
          val salt = RandomStringUtils.randomAlphanumeric(3)
          val encodedPassword = passwordEncoder.encode(user.password, salt)
          if (user.password == user.confirmPassword) {
            val finalUser = User(user.email, user.name, encodedPassword, encodedPassword, Some(salt))
            val userOption = findUserByLogin(user.email)
            userOption.flatMap {
              uo =>
                uo match {
                  case Some(u) => Future.successful(BadRequest("User with this login already exists"))
                  case _ => {
                    // `user` is an instance of the case class `models.User`
                    collection.insert(finalUser).map {
                      lastError =>
                        Logger.debug(s"Successfully inserted with LastError: $lastError")
                        Created(s"User Created")
                    }
                  }
                }
            }
          } else {
            Future.successful(BadRequest("Password not identical"))
          }
      }.getOrElse(Future.successful(BadRequest("invalid json")))
  }

  /*def authenticate = Action.async(parse.json) {
    implicit request =>
      request.body.validate[Login].map {
        login =>
          val futureUser = findUserByCredential(login.email, login.password)
          futureUser.flatMap {
            userOption =>
              userOption match {
                case Some(u) =>
                  logger.debug(s"found user ${u.email}")
                  gotoLoginSucceeded(u.email)
                case _ => Future.successful(BadRequest("email or password not correct"))
              }
          }
      }.getOrElse(Future.successful(BadRequest("Invalid json")))
  }*/

  val loginForm = Form {
    tuple(
      "email" -> email,
      "password" -> text
    ) verifying("Invalid user name or password", fields => fields match {
      case (e, p) => {
        Logger.debug(s"here verifying email $e pwd $p")
        checkUser(e, p)
      }
    })
  }

  def login = Action {
    implicit request =>
      Logger.debug("goto login page")
      Ok(views.html.login(loginForm))
  }

  def signUp = StackAction {
    implicit request =>
      val maybeUser: Option[User] = loggedIn
      Ok(views.html.usermgt(maybeUser))
  }

  def authenticate = Action.async { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => {
        Logger.debug("authenticate failed")
        Logger.debug(s"has errors  ${formWithErrors.hasErrors} global errors ${formWithErrors.hasGlobalErrors}")
        Future.successful(BadRequest(html.login(formWithErrors)))
      },
      user => {
        Logger.debug("succeeded")
        gotoLoginSucceeded(user._1)
      }
    )
  }

  def checkUser(email: String, password: String): Boolean = {
    Logger.debug("get into checkuser method")
    val result = findUserByCredential(email, password).map(_.isDefined)
    Logger.debug("checking user")
    Await.result(result, 5 seconds)
  }

  def findUserByLogin(login: String) = {
    val cursor: Cursor[models.User] = collection.find(Json.obj("email" -> login)).cursor[models.User]
    cursor.headOption
  }

  def findUserByCredential(email: String, password: String): Future[Option[models.User]] = {
    val cursor: Cursor[models.User] = collection.find(Json.obj("email" -> email)).cursor[models.User]
    val futureUser: Future[Option[models.User]] = cursor.headOption
    futureUser.map {
      uOption =>
        uOption match {
          case Some(u) if (u.password == passwordEncoder.encode(password, u.salt.get)) => uOption
          case _ => None
        }
    }
  }

}
