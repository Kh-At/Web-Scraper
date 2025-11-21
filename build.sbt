ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.3.7"
name := "WebCrawler-Kh"

coverageFailOnMinimum := false
coverageMinimum := 80


lazy val root = (project in file("."))
  .settings(
    libraryDependencies ++= Seq(   
    "org.scalactic" %% "scalactic" % "3.2.14" ,
    "org.scalatest" %% "scalatest" % "3.2.14" % Test
  ), coverageEnabled := true
)