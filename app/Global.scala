import java.io.File
import com.typesafe.config.ConfigValueFactory
import play.api.{ Configuration, GlobalSettings, Mode }

object Global extends GlobalSettings {
  override def onLoadConfig(c: Configuration, p: File, cl: ClassLoader, m: Mode.Mode): Configuration = c.copy(underlying = c.underlying.withValue(
    "applidok.token", ConfigValueFactory.fromAnyRef(
      System getenv "APPLIDOK_TOKEN")))

}
