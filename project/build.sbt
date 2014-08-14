//resolvers += Classpaths.typesafeResolver

//resolvers ++= Seq(
//  "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots",
//  "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",
//  "Scala-Tools Snapshots" at "http://scala-tools.org/repo-snapshots",
//  "Scala Tools Releases" at "http://scala-tools.org/repo-releases",
//  "snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
//  "releases" at "http://oss.sonatype.org/content/repositories/releases",
//  "spray repo" at "http://repo.spray.io",
//  "spray nightlies repo" at "http://nightlies.spray.io"
//)

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

