package controllers

import jp.t2v.lab.play2.auth.OptionalAuthElement
import play.api._
import play.api.i18n.Messages
import play.api.mvc._

object Application extends Controller with AuthenticationController with OptionalAuthElement {

  def index = StackAction {
    implicit request =>
      val maybeUser: Option[User] = loggedIn
      Ok(views.html.index(maybeUser))
  }

}