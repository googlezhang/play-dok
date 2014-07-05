package controllers

import scala.concurrent.Future

import play.api._
import play.api.mvc._

import fr.applicius.PlayDokAction

object Application extends Controller {
  def index = Assets.at(path = "/public", "index.html")
  def form = Assets.at(path = "/public", "form.html")

  def merge2 = PlayDokAction(
    templateId = Some("ca62e08d-2082-4cd4-837c-d46a362091e3"))

  def merge3 = PlayDokAction(
    fields = Map("Nom" -> "My Name", "Adresse" -> "An adresse"))

  /* Other call examples:

  def withTokenArg = PlayDokAction(token = Some("token"))

  def withCredArgs = PlayDokAction(
   token = Some("token"), templateId = Some("id"))

  def merge3 = PlayDokAction( // with token and pre-filled field
    token = Some("token"), fields = Map("field" -> "value"))

  def composed = Action async { req =>
    req.session.get("authBadge") match {
      case Some(badge) => index(req)
      case _ => Future.successful(Forbidden("No authenticated"))
    }
  }
   */
}
