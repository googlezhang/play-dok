# Play Dok

This is a library to use [Applidok](http://www.applidok.com) to generated PDF in [Play Framework](http://playframework.org) applications.

A demonstration Play app is available [online](http://play-demo.applidok.com/). You can see sources of this demo on [GitHub](https://github.com/cchantep/play-dok/tree/play22-demo/).

## Setup

Add following library dependency in the `build.sbt` (or `project/Build.scala`) file of your application:

```scala
libraryDependencies ++= Seq("fr.applidok" %% "play-dok" % "1.2-play2.3")
```

> This library version is for Play 2.3.x, with up to Scala 2.11.5.
> For compatibility with Play 2.2.x (and Scala 2.10), dependency version must be set to `1.0-play2.2`.

You can create your Applidok account by [registering online](https://go.applidok.com). Template editor (Dhek) is freely available for [download](https://go.applidok.com/en/download.gz.html).

## Usage

Play Dok provides actions to call Applidok features.

You can configure it directly in `conf/routes`:

```
POST /merge fr.applidok.PlayAction()
```

Then Applidok merge can be called in your application at URL `/merge`.

It's also possible to call Play Dok action from your own controllers, to [compose it with you own actions](http://www.playframework.com/documentation/latest/ScalaActionsComposition).

```scala
package controllers

import scala.concurrent.Future

import play.api._
import play.api.mvc._

import fr.applidok.PlayAction

object MyController extends Controller {
  def simple = PlayAction()

  def merge1 = PlayAction(token = Some("token"))

  def merge2 = PlayAction(token = Some("token"), templateId = Some("id"))

  def merge3 = PlayAction( // with token and pre-filled field
    token = Some("token"), fields = Map("field" -> "value"))

  def composed = Action async { req =>
    req.session.get("authBadge") match {
      case Some(badge) => simple(req)
      case _ => Future.successful(Forbidden("No authenticated"))
    }
  }
}
```

> **Note**: As `PlayAction` is asynchronous (returns a `Future[SimpleResult]` instead of `SimpleResult`), when composing with it `Action.async` must be used.

## Authentication

Information required to call Applidok features can be passed as function parameter, as request parameters or headers, or set in application configuration (file `conf/application.conf`).

So it's possible add application token and template ID in application configuration with following format:

```
applidok.token=Application token for Applidok
applidok.template=ID of Applidok template
```

> These credentials are visible on each template in [Applidok manager](https://go.applidok.com), in Integration tab.

Application token and template ID can also be provided as request parameters or headers using names `applidok_token` and `applidok_template`.

Finally, you can directly pass it as function parameter when you call `PlayAction(...)` from your controllers:

```scala
package controllers

import play.api._
import play.api.mvc._

import fr.applidok.PlayAction

object MyController extends Controller {
  def merge1 = PlayAction(token = Some("token"))

  def merge2 = PlayAction(token = Some("token"), templateId = Some("id"))

}
```

Credentials lookup can be summed up as: function parameters > request parameters > request headers > application configuration.

## Merge

Field values to be merged on Applidok template are get from request parameters (query string or POST) and function parameters.

```scala
package controllers

import play.api._
import play.api.mvc._

import fr.applidok.PlayAction

object MyController extends Controller {
  def merge = PlayAction( // with token and pre-filled field
    fields = Map("field1" -> "A", "field2" -> "B"))

}
```

For an area name `A` in selected Applidok template, a value must be provided in request or function with same name.

## Troubleshooting

- Internal server error with status text `Fails to merge: ...`: An error has occured while calling Applidok merge.

Play Dok logger is named `playdok`, so debug can be configured as following:

```
logger.playdok=DEBUG

# or:
#logger.playdok=TRACE
```
