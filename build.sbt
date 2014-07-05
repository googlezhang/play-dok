name := "play-dok"

organization := "fr.applicius"

version := "1.0-play2.3"

javaOptions in ThisBuild ++= Seq("-source", "1.6", "-target", "1.6")

scalaVersion := "2.10.4"

crossScalaVersions := Seq("2.10.4", "2.11.1")

resolvers += "Typesafe" at "http://repo.typesafe.com/typesafe/releases/"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play" % "2.3.1" % "provided",
  "net.databinder.dispatch" %% "dispatch-core" % "0.11.1",
  "org.specs2" %% "specs2" % "2.3.11" % "test",
  "com.typesafe.play" %% "play-test" % "2.3.1" % "test")

pomExtra in ThisBuild := (
  <url>https://github.com/applicius/play-dok/</url>
  <licenses>
    <license>
      <name>GNU Lesser General Public License, Version 2.1</name>
      <url>https://raw.github.com/applicius/play-dok/master/LICENSE.txt</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <connection>scm:git:git@github.com:applicius/play-dok.git</connection>
    <developerConnection>
    scm:git:git@github.com:applicius/play-dok.git
    </developerConnection>
    <url>git@github.com:applicius/play-dok.git</url>
  </scm>
  <issueManagement>
    <system>GitHub</system>
    <url>https://github.com/applicius/play-dok/issues</url>
  </issueManagement>
  <ciManagement>
    <system>Travis CI</system>
    <url>https://travis-ci.org/applicius/play-dok</url>
  </ciManagement>
  <developers>
    <developer>
    <id>applicius</id>
    <name>Cedric Chantepie</name>
  </developer>
</developers>)

publishTo := Some(Resolver.file("file", new File(Path.userHome.absolutePath+"/.m2/repository")))
