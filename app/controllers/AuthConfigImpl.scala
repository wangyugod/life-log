package controllers

import play.api.mvc.RequestHeader
import play.api.mvc.Results._

import scala.concurrent.{ExecutionContext, Future}

trait AuthConfigImpl extends BaseAuthConfig {

  def loginSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext) = Future.successful(Redirect(routes.Application.index()))

  def logoutSucceeded(request: RequestHeader)(implicit ctx: ExecutionContext) = Future.successful(Redirect(routes.Users.login()))

  def authenticationFailed(request: RequestHeader)(implicit ctx: ExecutionContext) = Future.successful(Redirect(routes.Users.login()))

}