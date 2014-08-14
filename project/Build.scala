package com.example

import sbt._
import sbt.Keys._
import sbt.Tests
import com.typesafe.sbteclipse.plugin.EclipsePlugin.EclipseKeys
import spray.revolver.RevolverPlugin._
import sbtassembly.Plugin._
import AssemblyKeys._

object MyBuild extends Build {

  // ===========  PROJECTS  ============ //

  lazy val root = Project(id = "root",
    base = file("."))
    .aggregate(rest, engine)

  lazy val rest = Project(id = "rest",
    base = file("rest"))
    .settings(restSettings: _*)
    .dependsOn(engine)

  lazy val engine = Project(id = "engine",
    base = file("engine"))
    .settings(engineSettings: _*)


  // ========= PROJECT SETTINGS =========//

  lazy val engineSettings =
    settings ++
      Seq(libraryDependencies ++=
        Dependencies.lift ++
          //Dependencies.spray ++
          //Dependencies.mongeezAll ++
          Dependencies.rogue ++
          Dependencies.akka)

  lazy val restSettings =
    settings ++
      assemblySettings ++
      Seq(mergeStrategy in assembly <<= (mergeStrategy in assembly) {
        (old) => {
          case PathList("overview.html") => MergeStrategy.rename
          case x => old(x)
        }
      }) ++
      Revolver.settings ++
      Seq(libraryDependencies ++=
        Dependencies.spray ++
          Dependencies.akka ++
          Dependencies.testKit) // ++
  //Dependencies.lift ++
  //Dependencies.rogue)

  override lazy val settings =
    super.settings ++
      buildSettings ++
      defaultSettings

  lazy val buildSettings = Seq(
    organization := "com.example",
    version := "0.1",
    scalaVersion := "2.10.4",
    EclipseKeys.withSource := true
  )

  lazy val defaultSettings = Seq(
    // Compile options
    scalacOptions in Compile ++=
      Seq("-encoding",
        "UTF-8",
        "-target:jvm-1.7",
        "-deprecation",
        "-feature",
        "-unchecked",
        "-language:_"),
    /* Uncomment to see reflection uses: "-Xlog-reflective-calls",
     This generates lots of noise in the build: "-Ywarn-adapted-args",
    */
    scalacOptions in Compile in doc ++=
      Seq("-diagrams",
        "-implicits")
  )


  //=========== DEPENDENCIES =============//

  object Dependencies {

    object Compile {

      // LIFT
      val liftVersion = "2.5.1"
      val liftJson = "net.liftweb" %% "lift-json" % liftVersion
      val liftCommon = "net.liftweb" %% "lift-common" % liftVersion
      val liftRecord = "net.liftweb" %% "lift-mongodb-record" % liftVersion

      // ROGUE
      val rogueField = "com.foursquare" %% "rogue-field" % "2.2.1" intransitive()
      val rogueCore = "com.foursquare" %% "rogue-core" % "2.3.0" intransitive()
      val rogueLift = "com.foursquare" %% "rogue-lift" % "2.3.0" intransitive()
      val rogueIndex = "com.foursquare" %% "rogue-index" % "2.3.0" intransitive()

      // SPRAY
      val sprayV = "1.3.1"
      val sprayCan = "io.spray" % "spray-can" % sprayV
      val sprayRouting = "io.spray" % "spray-routing" % sprayV
      val sprayTestkit = "io.spray" % "spray-testkit" % sprayV % "test"
      //    "io.spray"  	  %   "spray-http"    % "1.2.0",
      //    "io.spray"            %   "spray-httpx"   % "1.2.0",
      //    "io.spray"            %%   "spray-json"    % "1.2.5"

      // AKKA
      val akkaV = "2.3.0"
      val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaV
      val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % akkaV % "test"

      //val casbah = "org.mongodb" %% "casbah" % "2.7.1"
      //val mongeez = "org.mongeez" % "mongeez" % "0.9.4"

      // LOG
      //    val logback      = "ch.qos.logback" % "logback-classic"              % "1.0.13"
      //    val logbackJavaCompiler = "org.codehaus.janino" % "janino" % "2.6.1"

      // DATE
      //    val dateScala = "org.scalaj" % "scalaj-time_2.10.0-M7" % "0.6"

      object Test {
        val specs2 = "org.specs2" %% "specs2" % "2.3.12" % "test"
        val testDb = "com.github.fakemongo" % "fongo" % "1.5.1" % "test"
      }
    }

    import Compile._

    val rogue = Seq(rogueField, rogueCore, rogueLift, rogueIndex)
    val lift = Seq(liftCommon, liftRecord, liftJson)
    //val log = Seq(Compile.logback, Compile.logbackJavaCompiler)
    val akka = Seq(akkaActor, akkaTestkit)
    val spray = Seq(sprayCan, sprayRouting, sprayTestkit)
    //val   mongeezAll = Seq(Compile.mongeez)
    //val testKit = Seq(Test.junit, Test.scalatest, Test.specs2, Test.testDb)
    val testKit = Seq(Test.specs2, Test.testDb)

  }

}

