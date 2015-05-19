package controllers

import play.api._
import play.api.i18n.Messages
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index(Messages("test.hello")))
  }

}