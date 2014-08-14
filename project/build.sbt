// SBT eclipse plugin for generating Eclipse project. Simply run 'eclipse' in SBT cmd.
// FROM: https://github.com/typesafehub/sbteclipse
// TASKS: eclipse
addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.5.0")


// SBT revolver plugin for running spray-can web service and reloading it in a forked JVM.
// FROM: https://github.com/spray/sbt-revolver
// TASKS: ;re-start; re-stop; re-status
addSbtPlugin("io.spray" % "sbt-revolver" % "0.7.2")


// SBT assembly plugin to create fat jars that contain all dependencies.
// FROM: https://github.com/sbt/sbt-assembly
// TASKS: assembly
addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.11.2")

