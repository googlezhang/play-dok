name := "play-dok-demo"

version := "1.0-play2.2"

resolvers += "Applicius Releases" at "https://raw.github.com/applicius/mvn-repo/master/releases/"

libraryDependencies ++= Seq(
  "fr.applicius" %% "play-dok" % "1.0-play2.2")

play.Project.playScalaSettings
