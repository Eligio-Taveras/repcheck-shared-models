import sbt.*
import Versions.*

object Dependencies {
  private val emberClient =
    "org.http4s" %% "http4s-ember-client" % http4sVersion
  private val emberServer =
    "org.http4s" %% "http4s-ember-server" % http4sVersion
  private val http4sDsl =
    "org.http4s" %% "http4s-dsl" % http4sVersion
  private val http4sCirce =
    "org.http4s" %% "http4s-circe" % http4sVersion

  private val circeGeneric = "io.circe" %% "circe-generic" % circeVersion
  private val circeLiteral = "io.circe" %% "circe-literal" % circeVersion
  private val circeCore = "io.circe" %% "circe-core" % circeVersion
  private val circeParser = "io.circe" %% "circe-parser" % circeVersion

  private val pureConfigCore =
    "com.github.pureconfig" %% "pureconfig-core" % pureConfigVersion
  private val doobieCore =
    "org.tpolecat" %% "doobie-core" % doobieVersion
  private val doobieHikari =
    "org.tpolecat" %% "doobie-hikari" % doobieVersion
  private val doobiePostgres =
    "org.tpolecat" %% "doobie-postgres" % doobieVersion

  // available for 2.12, 2.13, 3.2
  private val fs2Core = "co.fs2" %% "fs2-core" % "3.10.2"

  // optional I/O library
  private val fs2IO = "co.fs2" %% "fs2-io" % "3.10.2"

  // optional reactive streams interop
  private val fs2ReactiveStreams = "co.fs2" %% "fs2-reactive-streams" % "3.10.2"

  // optional scodec interop
  private val fs2Scodec = "co.fs2" %% "fs2-scodec" % "3.10.2"

  val doobie: Seq[ModuleID] = Seq(doobieCore, doobieHikari, doobiePostgres)
  val pureConfig: Seq[ModuleID] = Seq(pureConfigCore)
  val circe: Seq[ModuleID] =
    Seq(circeCore, circeGeneric, circeLiteral, circeParser)
  val http4sEmber: Seq[ModuleID] =
    Seq(emberClient, emberServer, http4sDsl, http4sCirce)

  val fs2: Seq[ModuleID] = Seq(fs2Core, fs2IO, fs2ReactiveStreams, fs2Scodec)
}
