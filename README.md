# repcheck-shared-models

Shared domain types for the RepCheck platform. This library defines the DTOs, domain objects, enums, codecs, and constants that all other RepCheck repositories depend on. It is published to GitHub Packages and consumed as a versioned Maven dependency.

## What It Contains

- **Congressional DTOs** -- API-shape case classes for bills, votes, members, amendments, and committees (mirrors Congress.gov JSON responses)
- **Domain Objects (DOs)** -- Storage-layer case classes with `toDO` conversions for AlloyDB persistence via Doobie
- **Enums** -- `Chamber`, `BillType`, `VoteResult`, `AmendmentType`, and other legislative enums with safe string parsing
- **Circe Codecs** -- Semi-auto derived JSON encoders/decoders for all DTOs and DOs
- **Constants** -- `CongressGovApiPaths`, `ChamberDataPaths`, `DateTimeFormats`, `PaginationDefaults`
- **Type Classes** -- `HasPlaceholder[A]` for placeholder entity generation in ingestion pipelines
- **LLM Output Schemas** -- Structured types for LLM analysis results
- **Prompt Chain Traits** -- Base traits for prompt assembly
- **User Domain** -- User profiles, stances, Q&A, and scoring types
- **Analysis Domain** -- Bill analysis, decomposition, and alignment types

## How It Fits in the RepCheck Ecosystem

```
repcheck-shared-models          <-- you are here
  |
  +-- repcheck-pipeline-models  (pipeline events, execution tracking, workflow schemas)
  +-- repcheck-ingestion-common (API clients, change detection, repositories)
  +-- repcheck-bills-pipeline   (bill ingestion)
  +-- repcheck-members-pipeline (member ingestion)
  +-- repcheck-votes-pipeline   (vote ingestion)
  +-- repcheck-llm-analysis     (LLM-powered bill analysis)
  +-- repcheck-scoring-engine   (alignment scoring)
```

Every repository in the platform depends on `repcheck-shared-models`. Changes here propagate downstream.

## Tech Stack

| Concern | Technology |
|---------|-----------|
| Language | Scala 3.4.1 |
| JSON | Circe 0.14.6 (semi-auto derivation) |
| Database | Doobie 1.0.0-RC4 (auto-derived `Read`/`Write`) |
| Build | SBT with sbt-dynver (git-based versioning) |
| Linting | WartRemover (11 error rules), Scalafix, tpolecat |
| Testing | ScalaTest (AnyFlatSpec + Matchers) |
| Publishing | GitHub Packages (Maven) |
| CI | GitHub Actions |

## Build Commands

```bash
sbt.bat compile              # Compile with WartRemover + tpolecat
sbt.bat test                 # Run all tests
sbt.bat scalafmtCheckAll     # Check formatting (fails if unformatted)
sbt.bat scalafmtAll          # Auto-format all source files
sbt.bat scalafixAll --check  # Check import ordering and lint rules
sbt.bat scalafixAll          # Auto-fix import ordering
```

## Project Structure

```
repcheck-shared-models/
  src/main/scala/repcheck/shared/models/
    analysis/        # Bill analysis and decomposition types
    codecs/          # Shared Circe codec instances
    congress/
      amendment/     # Amendment enums (AmendmentType, etc.)
      bill/          # Bill enums (BillType, BillTextVersionCode, etc.)
      committee/     # Committee enums
      common/        # Shared congressional types (Chamber, Party, etc.)
      dos/           # Domain objects for AlloyDB (bill, member, vote, amendment, committee)
      dto/           # API-shape DTOs with toDO conversions
      vote/          # Vote enums (VoteResult, etc.)
    constants/       # API paths, date formats, pagination defaults
    llm/             # LLM output schemas
    placeholder/     # HasPlaceholder type class
    prompt/          # Prompt chain base traits
    user/            # User profiles, stances, Q&A, scores
  src/test/scala/    # Tests (mirrors source structure)
doc-generator/       # Doc compression utility (Anthropic SDK)
docs/                # Full project documentation
.claude/agent-docs/  # Compressed docs for AI agents
```

## Publishing

This library is published to GitHub Packages using `sbt-dynver` for automatic semantic versioning based on git tags:

- Tagged commits produce release versions (e.g., `v0.1.3` -> `0.1.3`)
- Untagged commits produce snapshot versions (e.g., `0.1.4+3-abcd1234-SNAPSHOT`)

To consume in another SBT project:

```scala
resolvers += "GitHub Packages" at "https://maven.pkg.github.com/Eligio-Taveras/repcheck-shared-models"
libraryDependencies += "com.repcheck" %% "repcheck-shared-models" % "<version>"
```

## Documentation

See `CLAUDE.md` for the full agent routing guide, coding conventions, and task routing table.
