ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.3.7"
name := "WebCrawler-Kh"
coverageFailOnMinimum := false
coverageExcludedPackages:= de/htwg/se/view

fork := true
run / connectInput := true
run / javaOptions ++= Seq(
  "-Dprism.order=sw",
  "-Dprism.verbose=false"
)

lazy val root = (project in file(".")).settings(
    libraryDependencies ++= Seq( 
      "org.scalactic" %% "scalactic" % "3.2.14" ,
      "org.scalatest" %% "scalatest" % "3.2.14" % Test,
      "org.jsoup" % "jsoup" % "1.17.2",
      "org.scalafx" %% "scalafx" % "24.0.0-R35",
      "org.scala-lang.modules" %% "scala-xml" % "2.4.0",
      "net.codingwell" %% "scala-guice" % "7.0.0",
      "com.google.inject" % "guice" % "5.1.0",
      "com.typesafe.play" %% "play-json" % "2.10.0",
    ), 
    coverageEnabled := true,
    libraryDependencies ++= {
      val os = System.getProperty("os.name").toLowerCase match {
        case mac if mac.contains("mac") => "mac"
        case win if win.contains("win") => "win"
        case _                          => "linux"
      }
      Seq("base", "controls", "fxml", "graphics", "media", "web")
      .map(m => "org.openjfx" % s"javafx-$m" % "16" classifier os)
    }
)
