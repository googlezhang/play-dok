package fr.applicius

import java.util.{ Iterator ⇒ JIterator, List ⇒ JList, Map ⇒ JMap }

import scala.concurrent.Future

import dispatch.{ as, Http, url }

import play.api.{ Application, Logger }
import play.api.mvc.{
  AnyContentAsFormUrlEncoded,
  AnyContentAsMultipartFormData,
  Action,
  Request,
  ResponseHeader,
  Results,
  SimpleResult
}

import play.api.libs.iteratee.Enumerator
import play.api.libs.concurrent.Execution.Implicits.defaultContext

/** PlayDok action */
object PlayDokAction {
  lazy val logger = Logger("playdok")

  /**
   * Creates an action that merges using Applidok.
   * This action can be used directly as route (where to POST from form page),
   * or can be composed with your own action.
   *
   * Application token and template ID are loaded either from
   * function parameters configuration (if Some), or from request parameters
   * or headers ([[play.api.mvc.Request]]), or from application configuration
   * ([[play.api.Application.configuration]]).
   *
   * Values for fields to be merged are get either from given `fields`,
   * or request parameters.
   *
   * {{{
   * import fr.applicius.PlayDokAction
   *
   * // In your controller
   * def myAction = PlayDokAction()
   * }}}
   *
   * @param token Applidok (application) token
   * @param templateId Template ID
   * @param fields Values of fields to be merged
   * @param app Play application
   */
  def apply(token: Option[String] = None, templateId: Option[String] = None, fields: Map[String, String] = Map.empty, app: Application = play.api.Play.current) = Action async { req ⇒
    logger info "Will call Applidok merge..."

    val params = parameters(req)

    credentials(app, req, params, token, templateId).fold[Future[SimpleResult]](
      Future.successful(Results.BadRequest(
        "Missing Applidok credentials: token and template ID"))) { creds ⇒

        logger debug s"Applidok credentials: $creds"

        val applidok = url("https://go.applidok.com/api/merge")
        val ps = params ++ fields + ("applidok_token" -> creds._1,
          "applidok_template" -> creds._2)

        logger trace s"Applidok parameters: $ps"

        Http((applidok << ps).POST) map {
          case suc if (suc.getStatusCode == 200) ⇒ {
            logger info "PDF successfully merged by Applidok"

            val hs = headers(suc.getHeaders.entrySet.iterator) filterNot { h ⇒
              h._1 == "Transfer-Encoding" || h._1 == "Server" ||
                h._1 == "Connection"
            }

            SimpleResult(
              header = ResponseHeader(200, hs),
              body = Enumerator.fromStream(suc.getResponseBodyAsStream))
          }
          case err ⇒
            logger error s"Error calling Applidok merge: ${err.getStatusCode} - ${err.getStatusText}"
            Results.InternalServerError(
              s"Fails to merge: ${err.getStatusCode} - ${err.getStatusText}")
        }
      }
  }

  /**
   * Looks up for merge credentials (application token, template Id),
   * first among request parameters (named "applidok_token" and
   * "applidok_template"), then as request headers (same names),
   * finally in application configuration (as fallback).
   */
  private[applicius] def credentials(app: Application, req: Request[_], params: Map[String, String], tok: Option[String], tid: Option[String]): Option[(String, String)] = {

    val (cfg, headers) = (app.configuration, req.headers)
    val (cfgAppToken, cfgTemplateId) = (cfg.getString("applidok.token"),
      cfg.getString("applidok.template"))

    val (headerAppToken, headerTemplateId) =
      (headers.get("applidok_token"), headers.get("applidok_template"))

    val (reqAppToken, reqTemplateId) =
      (params.get("applidok_token"), params.get("applidok_template"))

    logger debug s"""Credentials from,
- configuration: token = ${cfgAppToken}, template ID = $cfgTemplateId
- request headers: token = ${headerAppToken}, template ID = $headerTemplateId
- request parameters: token = ${reqAppToken}, template ID = $reqTemplateId
- function parameters: token = ${tok}, template ID = $tid"""

    for {
      appToken ← tok orElse (
        reqAppToken orElse headerAppToken orElse cfgAppToken)

      templateId ← tid orElse (
        reqTemplateId orElse headerTemplateId orElse cfgTemplateId)

    } yield (appToken -> templateId)
  }

  /** Returns POST/GET request parameters. */
  private def parameters(req: Request[_]): Map[String, String] = {
    val queryString = req.queryString.foldLeft(
      Map[String, String]()) { (m, e) ⇒
        val (k, v) = e
        v.headOption.fold(m) { s ⇒ m + (k -> s) }
      }

    val post: Map[String, String] = (req.body match {
      case AnyContentAsFormUrlEncoded(params) ⇒ params
      case mp @ AnyContentAsMultipartFormData(_) ⇒
        mp.asFormUrlEncoded getOrElse Map[String, Seq[String]]()
      case _ ⇒ Map[String, Seq[String]]()
    }).foldLeft(Map[String, String]()) { (m, e) ⇒
      val (k, v) = e
      v.headOption.fold(m) { s ⇒ m + (k -> s) }
    }

    post ++ queryString
  }

  @annotation.tailrec
  private def headers(hs: JIterator[JMap.Entry[String, JList[String]]], out: Map[String, String] = Map.empty): Map[String, String] = if (!hs.hasNext) out else {
    val e = hs.next
    val vs = e.getValue

    if (vs.isEmpty) headers(hs, out)
    else headers(hs, out + (e.getKey -> vs.get(0)))
  }
}
