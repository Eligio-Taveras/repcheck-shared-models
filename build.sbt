import org.typelevel.scalacoptions.ScalacOption
import sbt.Keys.libraryDependencies
import sbt.Def
import Dependencies.*
import com.repcheck.sbt.ExceptionUniquenessPlugin.autoImport.exceptionUniquenessRootPackages

val isScala212: Def.Initialize[Boolean] = Def.setting {
  VersionNumber(scalaVersion.value).matchesSemVer(SemanticSelector("2.12.x"))
}

ThisBuild / dynverSonatypeSnapshots := true

lazy val commonSettings = Seq(
  organization := "com.repcheck",
  scalaVersion := "3.7.3",
  publishTo := Some(
    "GitHub Packages" at s"https://maven.pkg.github.com/Eligio-Taveras/repcheck-shared-models"
  ),
  publishMavenStyle := true,
  credentials ++= {
    val envCreds = for {
      user  <- sys.env.get("GITHUB_ACTOR")
      token <- sys.env.get("GITHUB_TOKEN")
    } yield Credentials("GitHub Package Registry", "maven.pkg.github.com", user, token)

    val fileCreds = {
      val f = Path.userHome / ".sbt" / ".github-packages-credentials"
      if (f.exists) Some(Credentials(f)) else None
    }

    envCreds.orElse(fileCreds).toSeq
  },
  resolvers += "GitHub Packages - repcheck-utils" at "https://maven.pkg.github.com/Eligio-Taveras/repcheck-utils",
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "3.2.18" % Test
  ),
  semanticdbEnabled := true,
  // Suppress Scala 3 ScalaTest-matcher warnings in TEST sources only (mirrors data-ingestion / bill-decomposition):
  //   - "unused value of type Assertion" from chained assertions (tpolecat -Wnonunit-statement)
  //   - "is not declared infix" from the matcher DSL
  Test / scalacOptions += "-Wconf:msg=unused value of type:s",
  Test / scalacOptions += "-Wconf:msg=is not declared infix:s",
  tpolecatScalacOptions ++= ScalaCConfig.scalaCOptions,
  tpolecatScalacOptions ++= {
    if (isScala212.value) ScalaCConfig.scalaCOption2_12
    else Set.empty[ScalacOption]
  },

  // WartRemover — enforces FP discipline at compile time
  wartremoverErrors ++= Seq(
    Wart.AsInstanceOf,          // No unsafe casts
    Wart.EitherProjectionPartial, // No .get on Either projections
    Wart.IsInstanceOf,          // No runtime type checks — use pattern matching
    Wart.MutableDataStructures, // No mutable collections
    Wart.Null,                  // No null — use Option
    Wart.OptionPartial,         // No Option.get — use fold/map/getOrElse
    Wart.Return,                // No return statements
    Wart.StringPlusAny,         // No string + any — use interpolation
    Wart.IterableOps,           // No .head/.tail on collections — use headOption
    Wart.TryPartial,            // No Try.get — use fold/recover
    Wart.Var                    // No mutable vars
  ),
  wartremoverWarnings ++= Seq(
    Wart.Throw                  // Warn on bare throw — prefer F.raiseError
  )
)

lazy val root = (project in file("."))
  .aggregate(repchecksharedmodels, docGenerator)
  .settings(
    commonSettings,
    name := "repcheck-shared-models"
  )

lazy val repchecksharedmodels = (project in file("repcheck-shared-models"))
  .enablePlugins(com.repcheck.sbt.ExceptionUniquenessPlugin)
  .settings(
    commonSettings,
    libraryDependencies += "com.repcheck" %% "repcheck-utils"        % "0.1.3", // base behaviors (codecs/DateTimeCodecs)
    libraryDependencies += "com.repcheck" %% "repcheck-utils-doobie" % "0.1.3", // pgvector + Postgres array codecs
    libraryDependencies ++= circe ++ doobie ++ Seq(
      // ScalaCheck is compile-scope: StructuredCodec.sampleGen (Gen[A]) is part of the contract — the single source
      // for both property tests and varied samples shown to the LLM (master §5b / 01-F1 diagram).
      "org.scalacheck" %% "scalacheck" % "1.17.0",
      // StructuredCodec.jsonSchema is DERIVED from the type (no hand-authored schema): tapir Schema.derived →
      // TapirSchemaToJsonSchema → circe Json. tapir-apispec-docs transitively pulls tapir-core + jsonschema-circe.
      "com.softwaremill.sttp.tapir"   %% "tapir-apispec-docs" % "1.13.19",
      "com.softwaremill.sttp.apispec" %% "jsonschema-circe"   % "0.11.10",
      "org.scalatestplus"             %% "scalacheck-1-17"    % "3.2.18.0" % Test,
      "net.reactivecore"  %% "circe-json-schema"  % "0.4.1"    % Test, // §10c #5b schema-validity law (circe-native, 2020-12)
      "com.h2database"                 % "h2"                 % "2.2.224"  % Test,
    ),
    // BillDO has 29 fields; Circe semi-auto derivation exceeds the default 32 inline limit
    scalacOptions += "-Xmax-inlines:64",
    // Coverage gate: every file must be >= 95% statement coverage or CI (`sbt coverage test coverageReport`) fails.
    coverageMinimumStmtPerFile := 95,
    coverageFailOnMinimum      := true,
    // Only exclusion: TapirSchemas isolates the tapir `Schema.derived` macros, whose expansion can't be exercised by
    // tests. The case classes/codecs/generators stay coverable; the §10c #5b law still validates these schemas.
    coverageExcludedFiles := ".*TapirSchemas.*",
    exceptionUniquenessRootPackages := Seq("com.repcheck", "repcheck")
  )

lazy val docGenerator = (project in file("doc-generator"))
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(
      "com.anthropic" % "anthropic-java" % "2.18.0",
      "org.typelevel" %% "cats-effect" % "3.5.4",
      "ch.qos.logback" % "logback-classic" % "1.5.6"
    ),
    // Exclude WartRemover for this utility project — uses Java SDK patterns
    wartremoverErrors := Seq.empty,
    wartremoverWarnings := Seq.empty,
    // Exclude from coverage — utility project with no unit tests
    coverageEnabled := false
  )
