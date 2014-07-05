name := "play-dok-demo"

version := "1.0-play2.3"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

resolvers += "Applicius Releases" at "https://raw.github.com/applicius/mvn-repo/master/releases/"

libraryDependencies ++= Seq(
  "fr.applicius" %% "play-dok" % "1.0-play2.3")
