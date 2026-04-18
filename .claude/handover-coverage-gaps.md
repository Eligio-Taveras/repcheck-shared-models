# Handover Plan: Fix Coverage Gaps to 90%

## Repo & Branch

- **Repo:** `C:\Users\elita\source\repos2024\repcheck-shared-models`
- **Branch:** `main` (commit `41f210f`)
- **Current state:** 71 main sources, 43 test files, 316 tests all passing
- **Build:** `sbt clean test` passes. `sbt-scoverage` plugin is installed (`project/plugins.sbt`).
- **Run coverage:** `sbt coverage test coverageReport` — report lands in `repcheck-shared-models/target/scala-3.4.1/scoverage-report/index.html`

## Problem Statement

The acceptance criteria for Areas 1.1, 1.2, 1.5, 1.6, and 1.7 require >90% coverage. Two categories of gaps exist:

### Gap 1: Zero Doobie Read/Write instances or tests (HIGH PRIORITY)

The acceptance criteria for **Area 1.2** and **Area 1.5** explicitly require:
- "Every DO has Doobie auto-derived `Read` and `Write` instances"
- Round-trip Doobie Read/Write tests

**Current state:** ZERO Doobie imports in any DO file. All DOs are bare case classes with only Circe codecs (or no codecs at all for Area 1.2 DOs).

**What needs to happen:**

For every DO, add compile-time verification that Doobie can derive `Read` and `Write`. Since this is a pure models library with no database, use `implicitly` checks in tests — no Testcontainers needed.

Required custom `Get`/`Put` instances already exist:
- `codecs/VectorCodec.scala` — `Get[Array[Float]]` / `Put[Array[Float]]` for pgvector
- `codecs/DoobieArrayCodecs.scala` — `Get[List[String]]` / `Put[List[String]]` for TEXT[]
- `java.time.Instant` — provided by `doobie-postgres` (`doobie.implicits.javatimedrivernative._`)

**DO files needing Doobie Read/Write (12 files in `congress/dos/`):**

| File | Special fields |
|------|---------------|
| `dos/bill/BillDO.scala` | `textEmbedding: Option[Array[Float]]` — needs VectorCodec in scope |
| `dos/bill/BillCosponsorDO.scala` | Simple types only |
| `dos/bill/BillSubjectDO.scala` | `embedding: Option[Array[Float]]` — needs VectorCodec |
| `dos/member/MemberDO.scala` | Simple types only |
| `dos/member/MemberTermDO.scala` | `termId: UUID` |
| `dos/member/MemberPartyHistoryDO.scala` | `partyHistoryId: UUID` |
| `dos/member/LisMemberMappingDO.scala` | `lastVerified: Instant` |
| `dos/vote/VoteDO.scala` | Simple types only |
| `dos/vote/VotePositionDO.scala` | `createdAt: Option[Instant]` |
| `dos/vote/VoteHistoryDO.scala` | `historyId: UUID`, `archivedAt: Option[Instant]` — also contains `VoteHistoryPositionDO` |
| `dos/amendment/AmendmentDO.scala` | Simple types only |
| `dos/results/ConversionResults.scala` | Contains nested DOs — NOT a DB table, skip Doobie |
| `dos/errors/ConversionErrors.scala` | Exception classes — skip Doobie |

**User DO files needing Doobie Read/Write (11 files in `user/`):**

| File | Special fields |
|------|---------------|
| `user/UserDO.scala` | `userId: UUID` |
| `user/UserPreferenceDO.scala` | `embedding: Option[Array[Float]]`, `preferenceId: UUID` |
| `user/MemberBillStanceDO.scala` | `topics: List[String]` — needs DoobieArrayCodecs |
| `user/score/ScoreDO.scala` | `userId: UUID` |
| `user/score/ScoreTopicDO.scala` | `userId: UUID` |
| `user/score/ScoreCongressDO.scala` | `userId: UUID` |
| `user/score/ScoreCongressTopicDO.scala` | `userId: UUID` |
| `user/score/ScoreHistoryDO.scala` | `scoreId: UUID`, `computedAt: Option[Instant]` |
| `user/score/ScoreHistoryCongressDO.scala` | `scoreId: UUID` |
| `user/score/ScoreHistoryCongressTopicDO.scala` | `scoreId: UUID` |
| `user/score/ScoreHistoryHighlightDO.scala` | `scoreId: UUID` |

**Approach for each DO:**

1. In the DO companion object (create one if it doesn't exist), add:
   ```scala
   import doobie._
   import doobie.implicits._
   import doobie.postgres.implicits._  // for Instant, UUID
   // import repcheck.shared.models.codecs.VectorCodec.{floatArrayGet, floatArrayPut}  // only if Array[Float] field
   // import repcheck.shared.models.codecs.DoobieArrayCodecs._  // only if List[String] field
   ```

2. In the test, verify derivation compiles:
   ```scala
   "BillDO" should "have Doobie Read instance" in {
     import doobie._
     import doobie.implicits._
     import doobie.postgres.implicits._
     import repcheck.shared.models.codecs.VectorCodec._
     implicitly[Read[BillDO]] should not be null
   }
   it should "have Doobie Write instance" in {
     // same imports
     implicitly[Write[BillDO]] should not be null
   }
   ```

**IMPORTANT:** `VoteHistoryDO.scala` contains TWO case classes: `VoteHistoryDO` and `VoteHistoryPositionDO`. Both need Read/Write verification.

### Gap 2: Source files with no dedicated test coverage (30 files)

Run `sbt coverage test coverageReport` first to get actual line coverage numbers. Then prioritize files below 90%.

**Files with NO test file at all (but may have indirect coverage):**

#### Definitely need new test files:
These DOs have no test and are only exercised indirectly through conversion tests (which test the conversion logic, not the DO itself):

- `dos/bill/BillDO.scala` — bare case class, no companion
- `dos/bill/BillCosponsorDO.scala` — bare case class
- `dos/bill/BillSubjectDO.scala` — bare case class
- `dos/member/MemberDO.scala` — bare case class
- `dos/member/MemberTermDO.scala` — bare case class
- `dos/member/MemberPartyHistoryDO.scala` — bare case class
- `dos/member/LisMemberMappingDO.scala` — bare case class
- `dos/vote/VoteDO.scala` — bare case class
- `dos/vote/VotePositionDO.scala` — bare case class
- `dos/vote/VoteHistoryDO.scala` — contains VoteHistoryDO + VoteHistoryPositionDO
- `dos/amendment/AmendmentDO.scala` — bare case class

These are currently just `final case class` with no companion object. Once you add Doobie Read/Write in companion objects, the test for that becomes the coverage.

#### Score DOs with no dedicated test (5 files):
- `user/score/ScoreTopicDO.scala`
- `user/score/ScoreCongressDO.scala`
- `user/score/ScoreCongressTopicDO.scala`
- `user/score/ScoreHistoryCongressDO.scala`
- `user/score/ScoreHistoryCongressTopicDO.scala`
- `user/score/ScoreHistoryHighlightDO.scala`

These have Circe codecs in companion objects but no tests. Need Circe round-trip + Doobie Read/Write tests.

#### Likely already covered by umbrella test:
- `llm/output/*.scala` (7 files) — all covered by `LlmOutputSpec.scala` which tests every output schema
- `constants/*.scala` (4 files) — covered by `ConstantsSpec.scala` which tests all constants objects
- `dos/results/ConversionResults.scala` — covered by conversion spec tests
- `prompt/StageConfig.scala` — likely covered by `ChainAssemblerSpec.scala` and `PromptProfileSpec.scala`

Verify with scoverage before adding redundant tests for these.

### Gap 3: Circe codecs missing from Area 1.2 DOs

The Area 1.2 DOs (BillDO, VoteDO, MemberDO, etc.) have NO Circe codecs at all. The acceptance criteria don't explicitly require Circe for Area 1.2 DOs (they're Doobie-only), but Area 1.5 acceptance criteria item 11 says "Codec round-trip test for each user and score DO with Circe (Pub/Sub serialization)."

The Area 1.2 DOs that will need Circe codecs for Pub/Sub events in later components:
- `BillDO` — yes (bill events)
- `VoteDO` — yes (vote events)
- `AmendmentDO` — yes (amendment events)
- `MemberDO` — yes (member events)

Consider adding Circe semi-auto codecs to these DO companion objects while you're there.

## WartRemover Rules (MUST comply)

Tests also go through WartRemover. These are **errors** (not warnings):
- No `.get` on Option — use `.fold`, `.map`, `.getOrElse`, or pattern match
- No `.head` on collections — use `.headOption`
- No `null`, `var`, `return`, mutable collections
- No `AsInstanceOf`, `IsInstanceOf`
- No `Try.get`

In tests, instead of:
```scala
// WRONG — WartRemover will reject
decoded.toOption.get.field shouldBe expected
```
Use:
```scala
// CORRECT
decoded shouldBe Right(expectedValue)
// or
decoded.map(_.field) shouldBe Right(expected)
// or
decoded match {
  case Right(result) => result.field shouldBe expected
  case Left(err) => fail(s"Unexpected error: $err")
}
```

## Execution Order

1. Run `sbt coverage test coverageReport` to get baseline numbers
2. Add Doobie Read/Write to all DOs (Gap 1) — this is the biggest structural gap
3. Add Circe codecs to Area 1.2 DOs that lack them (Gap 3)
4. Write tests for each DO: Doobie Read/Write compile check + Circe round-trip
5. Re-run coverage report to verify >90% on all files
6. Fix any remaining files below 90%

## Files NOT to touch

- `doc-generator/` — excluded from coverage (`coverageEnabled := false` in build.sbt)
- `dos/errors/ConversionErrors.scala` — exception case classes, already tested by `ConversionErrorsSpec.scala`
- `dos/results/ConversionResults.scala` — plain case classes, tested by conversion specs

## Build Commands

```bash
sbt compile                              # Compile with WartRemover
sbt test                                 # Run all tests
sbt coverage test coverageReport         # Generate coverage report
sbt scalafmtCheckAll                     # Check formatting
sbt scalafmtAll                          # Auto-format
```

## Commit Rules

- Use `scripts/ci-functions.sh` functions (`CreatePR`, `pushToPR`) — never bare `git push`
- Run all CI checks locally before pushing: `sbt compile test scalafmtCheckAll`
- Create a new branch from main for this work

---

# Phase 3: Areas 1.3 and 1.4

After coverage gaps are fixed and merged to main, implement the remaining two areas. Each should be on its own branch from main.

## Area 1.3 — Committee DTOs & DOs

**Branch:** `dev/area-1.3-committee`

**Acceptance criteria:** Read `C:\Users\elita\source\repos2024\votr\.claude\worktrees\modest-cori\.claude\agent-docs\architecture\acceptance-criteria\01-shared-models\01.3-committee-dtos-dos.compressed.md`

### Data Sources
- Senate: `senate.gov/legislative/LIS_MEMBER/cvc_member_data.xml`
- House: `clerk.house.gov/xml/lists/memberdata.xml`
- Congress.gov: `/bill/{congress}/{billType}/{billNumber}/committees`

### DTOs to create (under `congress/dto/committee/`)

**Senate XML DTOs:**
- `SenatorCommitteeDataXmlDTO` — lisMemberId, bioguideId, firstName, lastName, party, state, stateRank, office, leadershipPosition, committees: List[SenatorCommitteeAssignmentXmlDTO]
- `SenatorCommitteeAssignmentXmlDTO` — committeeCode, committeeName, position (Chairman/Ranking/Vice Chairman/Member)

**House XML DTOs:**
- `HouseMemberDataXmlDTO` — bioguideId, firstName, lastName, party, state, district, committees: List[HouseCommitteeAssignmentXmlDTO]
- `HouseCommitteeAssignmentXmlDTO` — committeeCode, committeeName, rank, side (majority/minority)

**Congress.gov DTOs:**
- `CommitteeListItemDTO` — chamber, committeeTypeCode, name, systemCode, updateDate, url, parent, subcommittees
- `CommitteeDetailDTO` — systemCode, type, isCurrent, history, bills (count + url), reports (count + url), subcommittees
- `BillCommitteeReferralDTO` — committeeCode, committeeName, chamber, activities: List[CommitteeActivityDTO]
- `CommitteeActivityDTO` — name (e.g., "Referred to", "Reported by"), date

All DTOs need Circe semi-auto Encoder/Decoder.

### DOs to create (under `congress/dos/committee/`)

- `CommitteeDO` — committeeCode (PK, e.g. "SSFI00"), name, chamber, committeeType (Standing/Special/Select/Joint/Subcommittee), parentCommitteeCode (Option[String], FK), isCurrent, updateDate, createdAt, updatedAt
- `CommitteeMemberDO` — committeeCode (FK), memberId (FK), position (Chairman/Ranking Member/Vice Chairman/Member), side (Majority/Minority), rank (Int), beginDate, endDate (Option — None = currently serving), congress, createdAt, updatedAt
- `BillCommitteeReferralDO` — billId (FK), committeeCode (FK), referralDate, reportDate (Option — None = still in committee), activity, createdAt

All DOs need Doobie Read/Write AND Circe codecs (learn from the pattern established in the coverage gap fix).

### Conversions (under `congress/dto/conversions/CommitteeConversions.scala`)

- `SenatorCommitteeDataXmlDTO.toMemberCommittees` → `List[CommitteeMemberDO]` (one per assignment; may be empty)
- `SenatorCommitteeDataXmlDTO.toLisMemberMapping` → `LisMemberMappingDO` (one per senator, INDEPENDENT of committee assignments — every senator gets a mapping even with zero committees)
- `HouseMemberDataXmlDTO.toMemberCommittees` → `List[CommitteeMemberDO]` (one per assignment)
- `CommitteeListItemDTO.toDO` → `Either[String, CommitteeDO]` (maps `systemCode` → `committeeCode`)
- `BillCommitteeReferralDTO.toDO` → `Either[String, BillCommitteeReferralDO]` (extracts `referralDate` from earliest "Referred to" activity; `reportDate` from earliest "Reported by" activity if present)

Note: `LisMemberMappingDO` already exists at `congress/dos/member/LisMemberMappingDO.scala` from Phase 2.

### Existing enums to reuse
These already exist from Phase 1 — import them, do NOT recreate:
- `CommitteePosition` — Chairman, RankingMember, ViceChairman, Member (in `congress/committee/`)
- `CommitteeSide` — Majority, Minority (in `congress/committee/`)
- `CommitteeType` — Standing, Special, Select, Joint, Subcommittee (in `congress/committee/`)

### Tests required
- Circe round-trip for all DTOs
- `SenatorCommitteeDataXmlDTO.toMemberCommittees` — verify correct CommitteeMemberDO count
- `SenatorCommitteeDataXmlDTO.toLisMemberMapping` for senator with NO committee assignments — must still produce mapping
- `SenatorCommitteeDataXmlDTO.toLisMemberMapping` — verify correct bioguideId and lisMemberId extraction
- `BillCommitteeReferralDTO.toDO` — verify referralDate and reportDate extraction from activities
- `CommitteeListItemDTO.toDO` — verify systemCode maps to committeeCode
- Doobie Read/Write compile check for all 3 DOs
- >90% coverage

---

## Area 1.4 — Bill Text Versioning

**Branch:** `dev/area-1.4-bill-text-versioning`

**Acceptance criteria:** Read `C:\Users\elita\source\repos2024\votr\.claude\worktrees\modest-cori\.claude\agent-docs\architecture\acceptance-criteria\01-shared-models\01.4-bill-text-versioning.compressed.md`

### New DO (under `congress/dos/bill/`)

**`BillTextVersionDO`** — `versionId: UUID` (PK), `billId: String` (FK → bills), `versionCode: String`, `versionType: String` (full name, e.g. "Reported in House"), `versionDate: Option[String]`, `formatType: Option[String]`, `url: Option[String]`, `content: Option[String]`, `embedding: Option[Array[Float]]` (pgvector), `fetchedAt: Option[Instant]`, `createdAt: Option[Instant]`

Needs Doobie Read/Write (with VectorCodec for embedding) AND Circe codecs.

### Change to BillDO

Add one field to the existing `BillDO` case class:
- `latestTextVersionId: Option[UUID]` — FK to `bill_text_versions.version_id`, None for bills with no text

**IMPORTANT:** This changes an existing case class. All existing tests that construct BillDO instances will need updating to include this new field. Check:
- `BillConversionsSpec.scala`
- `BillDTOsSpec.scala`
- Any other test that creates a `BillDO`

Also update `BillConversions.scala` — both `BillListItemDTO.toDO` and `BillDetailDTO.toDO` must set `latestTextVersionId = None` in the constructed BillDO.

### New LLM Output Schema (under `llm/output/`)

**`BillTextDiffOutput`** — `previousVersionCode: String, currentVersionCode: String, billId: String, sections: List[SectionChange], summary: String, significanceScore: Double` (0.0-1.0)

**`SectionChange`** — `sectionId: String, changeType: String, previousText: Option[String], currentText: Option[String], description: String`

Both need Circe semi-auto Encoder/Decoder.

### Existing enums to reuse
These already exist from Phase 1 — import them, do NOT recreate:
- `TextVersionCode` — IH, IS, RH, RS, RFS, RFH, EH, ES, ENR, CPH, CPS (in `congress/bill/`)
- `ChangeType` — Added, Removed, Modified, Renumbered (in `congress/bill/`)

### Tests required
- `BillTextVersionDO` Circe round-trip with all fields populated
- `BillTextVersionDO` Circe round-trip with optional fields as None
- `BillTextVersionDO` Doobie Read/Write compile check (needs VectorCodec)
- `BillTextDiffOutput` Circe round-trip with populated sections
- `BillTextDiffOutput` decode-from-LLM test: sample JSON matching expected LLM output
- `SectionChange` Circe round-trip
- `BillDO` with `latestTextVersionId = None` — bill with no text
- `BillDO` with `latestTextVersionId = Some(uuid)` — bill with text
- All existing BillDO tests still pass after the field addition
- >90% coverage

### WartRemover trap (ALREADY BIT THE PREVIOUS SESSION)
The previous session's agents attempted Area 1.4 and left failing code. The errors were:
- `.get` on Option in test files — use pattern match or `.map` instead
- `.head` on List in test files — use `.headOption` instead

Do NOT use `.get` or `.head` anywhere, including tests.

---

# Phase 4: Integration & E2E Validation

After Areas 1.3 and 1.4 are merged to main, run a final integration check.

### Step 1: Verify everything compiles and tests pass together
```bash
sbt clean compile test
```

### Step 2: Run coverage report and verify >90%
```bash
sbt coverage test coverageReport
```
Check every source file is above 90% patch coverage. Fix any stragglers.

### Step 3: Run formatting and linting
```bash
sbt scalafmtCheckAll
sbt scalafmtAll          # if check fails, auto-fix then re-check
```

### Step 4: Final git state
All 8 areas of Component 1 should be on main:
- 1.1 Legislative DTOs ✓ (already merged)
- 1.2 Legislative DOs ✓ (already merged)
- 1.3 Committee DTOs & DOs (your work)
- 1.4 Bill Text Versioning (your work)
- 1.5 User Domain Objects ✓ (already merged)
- 1.6 LLM Output Schemas ✓ (already merged)
- 1.7 Prompt Chain Base Traits ✓ (already merged)
- 1.8 Shared Serializers & Constants ✓ (already merged)

### Step 5: Docker-compose E2E (OPTIONAL — only if time permits)
The acceptance criteria mention Testcontainers for integration tests. For a pure models library without database access at runtime, this means:
- Spin up PostgreSQL + pgvector via docker-compose or Testcontainers
- Verify Doobie Read/Write actually round-trips through a real database
- Tag these as E2E tests: `taggedAs E2ETest`, excluded from `sbt test`, run via `sbt "testOnly -- -n com.repcheck.tags.E2ETest"`

This is lower priority than getting all 8 areas implemented with >90% unit test coverage.

---

## Execution Order Summary

1. **Coverage gaps** — Doobie Read/Write on all existing DOs, missing Circe codecs, missing test files → merge to main
2. **Area 1.3** — Committee DTOs/DOs/conversions on own branch → merge to main
3. **Area 1.4** — BillTextVersionDO + BillDO change + BillTextDiffOutput on own branch → merge to main
4. **Phase 4** — Final integration: clean build, coverage report, format check
