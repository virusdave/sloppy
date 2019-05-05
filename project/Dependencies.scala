import sbt._

object Dependencies {
  object LibraryVersions {
    val Enumeratum = "1.5.13"
    val Slick = "3.2.3"
    val SlickPG = "0.16.3"
  }
  
  val LibraryDependencies: Seq[ModuleID] = Seq(
    "com.beachape" %% "enumeratum" % LibraryVersions.Enumeratum,
    "com.beachape" %% "enumeratum-play-json" % LibraryVersions.Enumeratum,
    "com.chuusai" %% "shapeless" % "2.3.3",
    "com.github.tminglei" %% "slick-pg" % LibraryVersions.SlickPG,
    "com.github.tminglei" %% "slick-pg_play-json" % LibraryVersions.SlickPG,
    "com.typesafe.slick" %% "slick" % LibraryVersions.Slick,
    "com.typesafe.slick" %% "slick-codegen" % LibraryVersions.Slick,
    "com.typesafe.slick" %% "slick-hikaricp" % LibraryVersions.Slick,
    "io.underscore" %% "slickless" % "0.3.3",
    "org.postgresql" % "postgresql" % "42.2.5",
    "org.scalatest" %% "scalatest" % "3.0.1" % Test,
    "org.slf4j" % "slf4j-nop" % "1.6.4",
  )
}
