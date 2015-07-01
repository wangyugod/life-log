package controllers

import jp.t2v.lab.play2.auth.AuthConfig
import play.Logger
import play.api.mvc.RequestHeader
import play.api.mvc.Results._

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect._

trait BaseAuthConfig  extends AuthConfig {

  type Id = String
  type User = models.User
//  type Authority = Role

  val idTag: ClassTag[Id] = classTag[Id]
  val sessionTimeoutInSeconds = 3600

  def authorizationFailed(request: RequestHeader)(implicit ctx: ExecutionContext) = throw new AssertionError("don't use")
  override def authorizationFailed(request: RequestHeader, user: User, authority: Option[Authority])(implicit ctx: ExecutionContext) = {
    Logger.info(s"authorizationFailed. userId: ${user.email}, userName: ${user.name}, authority: $authority")
    Future.successful(Forbidden("no permission"))
  }
  def authorize(user: User, authority: Authority)(implicit ctx: ExecutionContext) = throw new AssertionError("don't use")

}
