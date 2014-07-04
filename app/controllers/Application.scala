package controllers

import scala.concurrent.Future

import play.api._
import play.api.mvc._

import fr.applicius.PlayDokAction

object Application extends Controller {

  def index = PlayDokAction()
  def merge1 = PlayDokAction(token = Some("token"))

  def merge2 = PlayDokAction(token = Some("token"), templateId = Some("id"))

  def merge3 = PlayDokAction( // with token and pre-filled field
    token = Some("token"), fields = Map("field" -> "value"))

  def composed = Action async { req =>
    req.session.get("authBadge") match {
      case Some(badge) => index(req)
      case _ => Future.successful(Forbidden("No authenticated"))
    }
  }

}
