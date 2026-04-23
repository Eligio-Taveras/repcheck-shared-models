package repcheck.shared.models.congress.dto.conversions

import java.time.{Instant, LocalDate}

import cats.Id

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repcheck.shared.models.congress.common.{BillType, Chamber, Party, UsState}
import repcheck.shared.models.congress.dto.conversions.VoteConversions._
import repcheck.shared.models.congress.dto.vote._
import repcheck.shared.models.congress.vote.{VoteCast, VoteMethod, VoteType}

class VoteConversionsSpec extends AnyFlatSpec with Matchers {

  // Default test lookup: pretend no bills are in the DB. Uses cats.Id (which is a no-op effect, so
  // `Id[Either[String, X]] == Either[String, X]` and existing pattern-match tests stay clean).
  private val noBillLookup: String => Id[Option[Long]] = _ => None

  private val validVoteMembers = VoteMembersDTO(
    congress = 118,
    chamber = "House",
    rollCallNumber = 42,
    sessionNumber = Some(1),
    startDate = Some("2024-01-15"),
    updateDate = Some("2024-01-16"),
    result = Some("Passed"),
    voteType = Some("YEA-AND-NAY"),
    legislationNumber = Some("1234"),
    legislationType = Some("HR"),
    legislationUrl = Some("https://congress.gov/bill/118/hr/1234"),
    url = Some("https://api.congress.gov/v3/vote/118/house/42"),
    identifier = Some("118-H-42"),
    sourceDataUrl = Some("https://clerk.house.gov/evs/2024/roll042.xml"),
    voteQuestion = Some("On Passage"),
    results = Some(
      List(
        VoteResultDTO(Some("A000370"), Some("Alma"), Some("Adams"), Some("Yea"), Some("D"), Some("NC")),
        VoteResultDTO(Some("B001297"), Some("Ken"), Some("Buck"), Some("Nay"), Some("R"), Some("CO")),
        VoteResultDTO(None, Some("Ghost"), Some("Member"), Some("Present"), Some("I"), Some("VT")),
      )
    ),
  )

  "VoteMembersDTO.toDO" should "produce VoteDO with correct natural key including session" in {
    val Right(result) = validVoteMembers.toDO(noBillLookup): @unchecked
    val _             = result.vote.voteId shouldBe 0L
    result.vote.naturalKey shouldBe "118-House-1-42"
  }

  it should "map all vote fields correctly and classify voteType via fromQuestion" in {
    val Right(result) = validVoteMembers.toDO(noBillLookup): @unchecked
    val v             = result.vote
    val _             = v.congress shouldBe 118
    val _             = v.chamber shouldBe Chamber.House
    val _             = v.rollNumber shouldBe 42
    val _             = v.sessionNumber shouldBe Some(1)
    val _             = v.question shouldBe Some("On Passage")
    // voteType is derived from voteQuestion via VoteType.fromQuestion, not from dto.voteType
    val _ = v.voteType shouldBe Some(VoteType.Passage)
    // voteMethod is derived from dto.voteType (the API's procedural label) via VoteMethod.fromString.
    // Fixture has "YEA-AND-NAY" which parses (case-insensitive) to YeaAndNay.
    val _ = v.voteMethod shouldBe Some(VoteMethod.YeaAndNay)
    val _ = v.result shouldBe Some("Passed")
    val _ = v.voteDate shouldBe Some(LocalDate.parse("2024-01-15"))
    val _ = v.legislationNumber shouldBe Some("1234")
    val _ = v.legislationType shouldBe Some(BillType.HR)
    val _ = v.legislationUrl shouldBe Some("https://congress.gov/bill/118/hr/1234")
    val _ = v.sourceDataUrl shouldBe Some("https://clerk.house.gov/evs/2024/roll042.xml")
    // billId stays None at the pure conversion layer — processor resolves via billNaturalKey.
    val _ = v.billId shouldBe None
    v.updateDate shouldBe Some(Instant.parse("2024-01-16T00:00:00Z"))
  }

  it should "populate billNaturalKey from legislation fields" in {
    val Right(result) = validVoteMembers.toDO(noBillLookup): @unchecked
    result.billNaturalKey shouldBe Some("118-HR-1234")
  }

  it should "leave billNaturalKey as None when legislation fields are missing (procedural vote)" in {
    val procedural    = validVoteMembers.copy(legislationType = None, legislationNumber = None)
    val Right(result) = procedural.toDO(noBillLookup): @unchecked
    result.billNaturalKey shouldBe None
  }

  it should "leave billNaturalKey as None when only legislationType is present" in {
    val partial       = validVoteMembers.copy(legislationNumber = None)
    val Right(result) = partial.toDO(noBillLookup): @unchecked
    result.billNaturalKey shouldBe None
  }

  it should "set vote.billId from the lookup result for a bill-linked vote" in {
    val lookup: String => Id[Option[Long]] = nk => if (nk == "118-HR-1234") Some(42L) else None
    val Right(result)                      = validVoteMembers.toDO(lookup): @unchecked
    val _                                  = result.vote.billId shouldBe Some(42L)
    result.billNaturalKey shouldBe Some("118-HR-1234")
  }

  it should "set vote.billId to None when the lookup returns None for a bill-linked vote" in {
    // Caller (processor) would typically wrap the lookup with placeholder creation so this doesn't happen,
    // but the signature doesn't enforce it. If lookup genuinely returns None, billId stays None.
    val Right(result) = validVoteMembers.toDO(noBillLookup): @unchecked
    val _             = result.vote.billId shouldBe None
    // billNaturalKey is still carried forward so the caller can see which bill was unresolved.
    result.billNaturalKey shouldBe Some("118-HR-1234")
  }

  it should "not call the lookup for a procedural vote (no legislation fields)" in {
    // AtomicInteger instead of `var` because WartRemover disables mutability in test code too.
    val callCount = new java.util.concurrent.atomic.AtomicInteger(0)
    val countingLookup: String => Id[Option[Long]] = _ => {
      val _ = callCount.incrementAndGet()
      Some(99L)
    }
    val procedural    = validVoteMembers.copy(legislationType = None, legislationNumber = None)
    val Right(result) = procedural.toDO(countingLookup): @unchecked
    val _             = callCount.get() shouldBe 0
    val _             = result.billNaturalKey shouldBe None
    result.vote.billId shouldBe None
  }

  it should "derive voteMethod from dto.voteType for each known API label" in {
    val cases: List[(String, VoteMethod)] = List(
      "recorded vote"     -> VoteMethod.RecordedVote,
      "Recorded Vote"     -> VoteMethod.RecordedVote,
      "Voice Vote"        -> VoteMethod.VoiceVote,
      "Unanimous Consent" -> VoteMethod.UnanimousConsent,
      "Yea-and-Nay"       -> VoteMethod.YeaAndNay,
      "2/3 Yea-And-Nay"   -> VoteMethod.TwoThirdsYeaAndNay,
      "Quorum Call"       -> VoteMethod.QuorumCall,
    )
    cases.foreach {
      case (apiLabel, expected) =>
        val dto           = validVoteMembers.copy(voteType = Some(apiLabel))
        val Right(result) = dto.toDO(noBillLookup): @unchecked
        result.vote.voteMethod shouldBe Some(expected)
    }
  }

  it should "leave voteMethod as None when dto.voteType is absent" in {
    val dto           = validVoteMembers.copy(voteType = None)
    val Right(result) = dto.toDO(noBillLookup): @unchecked
    result.vote.voteMethod shouldBe None
  }

  it should "fail fast when dto.voteType is not a known VoteMethod" in {
    val dto    = validVoteMembers.copy(voteType = Some("Something Unmapped"))
    val result = dto.toDO(noBillLookup)
    val _      = result.isLeft shouldBe true
    result.left.map(msg => msg.contains("Something Unmapped")) shouldBe Left(true)
  }

  it should "leave voteType as None when voteQuestion is absent" in {
    val dto           = validVoteMembers.copy(voteQuestion = None)
    val Right(result) = dto.toDO(noBillLookup): @unchecked
    result.vote.voteType shouldBe None
  }

  it should "classify voteType for each fromQuestion pattern" in {
    val cases: List[(String, VoteType)] = List(
      "On Passage of HR 1234"                  -> VoteType.Passage,
      "On Agreeing to the Conference Report"   -> VoteType.ConferenceReport,
      "On Cloture on the Motion to Proceed"    -> VoteType.Cloture,
      "On Overriding the Veto"                 -> VoteType.VetoOverride,
      "On Agreeing to the Amendment"           -> VoteType.Amendment,
      "Reported favorably from committee"      -> VoteType.Committee,
      "On Motion to Recommit"                  -> VoteType.Recommit,
      "Some obscure question with no keywords" -> VoteType.Other,
    )
    cases.foreach {
      case (question, expected) =>
        val dto           = validVoteMembers.copy(voteQuestion = Some(question))
        val Right(result) = dto.toDO(noBillLookup): @unchecked
        result.vote.voteType shouldBe Some(expected)
    }
  }

  it should "fail when sessionNumber is None" in {
    val result = validVoteMembers.copy(sessionNumber = None).toDO(noBillLookup)
    val _      = result.isLeft shouldBe true
    result.left.map(msg => msg.contains("sessionNumber")) shouldBe Left(true)
  }

  it should "emit UnresolvedVotePositions only for members with memberId in DTO" in {
    val Right(result) = validVoteMembers.toDO(noBillLookup): @unchecked
    // Fixture has 3 results; one has memberId = None. Should be filtered out.
    val _ = result.positions.length shouldBe 2
    // All emitted positions carry the bioguide via Left — the Right variant is reserved for a
    // future Senate-LIS path (P2.3). Every Left should be a non-empty bioguide string.
    result.positions.foreach(pos => pos.memberSource.isLeft shouldBe true)
  }

  it should "carry the bioguide in memberSource.Left for each resolved House position" in {
    val Right(result) = validVoteMembers.toDO(noBillLookup): @unchecked
    val bioguides     = result.positions.map(_.memberSource.fold(identity, _ => ""))
    bioguides shouldBe List("A000370", "B001297")
  }

  it should "map position fields correctly" in {
    val Right(result) = validVoteMembers.toDO(noBillLookup): @unchecked
    val first         = result.positions.headOption
    val _             = first.map(_.memberSource) shouldBe Some(Left("A000370"))
    val _             = first.flatMap(_.voteCast) shouldBe Some(VoteCast.Yea)
    val _             = first.flatMap(_.partyAtVote) shouldBe Some(Party.Democrat)
    first.flatMap(_.stateAtVote) shouldBe Some(UsState.NorthCarolina)
  }

  it should "fail when congress <= 0" in {
    val result = validVoteMembers.copy(congress = 0).toDO(noBillLookup)
    val _      = result.isLeft shouldBe true
    result.left.map(msg => msg.contains("congress")) shouldBe Left(true)
  }

  it should "fail when chamber is empty" in {
    val result = validVoteMembers.copy(chamber = "").toDO(noBillLookup)
    val _      = result.isLeft shouldBe true
    result.left.map(msg => msg.contains("chamber")) shouldBe Left(true)
  }

  it should "fail when chamber is unrecognized" in {
    val result = validVoteMembers.copy(chamber = "InvalidChamber").toDO(noBillLookup)
    val _      = result.isLeft shouldBe true
    result.left.map(msg => msg.contains("InvalidChamber")) shouldBe Left(true)
  }

  it should "handle None results" in {
    val Right(result) = validVoteMembers.copy(results = None).toDO(noBillLookup): @unchecked
    result.positions shouldBe List.empty
  }

  it should "fail when a position has an unrecognized voteCast" in {
    val badResult =
      VoteResultDTO(Some("X000001"), Some("Test"), Some("User"), Some("InvalidVote"), Some("D"), Some("NC"))
    val dto    = validVoteMembers.copy(results = Some(List(badResult)))
    val result = dto.toDO(noBillLookup)
    val _      = result.isLeft shouldBe true
    result.left.map(msg => msg.contains("InvalidVote")) shouldBe Left(true)
  }

  // ---------------------------------------------------------------------------
  // VoteType-aware voteCast parsing (officer-election votes → VoteCast.Candidate)
  // ---------------------------------------------------------------------------

  it should "route candidate-name voteCast values to VoteCast.Candidate when VoteType.fromQuestion classifies the vote as Election" in {
    val speakerRow =
      VoteResultDTO(Some("J000294"), Some("Hakeem"), Some("Jeffries"), Some("Jeffries"), Some("D"), Some("NY"))
    val dto = validVoteMembers.copy(
      voteQuestion = Some("Election of the Speaker"),
      legislationType = None,
      legislationNumber = None,
      legislationUrl = None,
      results = Some(List(speakerRow)),
    )
    val Right(result) = dto.toDO(noBillLookup): @unchecked
    val _             = result.vote.voteType shouldBe Some(VoteType.Election)
    val _             = result.positions.length shouldBe 1
    val pos           = result.positions.headOption
    val _             = pos.flatMap(_.voteCast) shouldBe Some(VoteCast.Candidate)
    pos.flatMap(_.voteCastCandidateName) shouldBe Some("Jeffries")
  }

  it should "STILL fail when a position has an unrecognized voteCast on a non-Election vote" in {
    // Regression guard: the VoteType-aware fallback must NOT silently absorb garbage on normal votes.
    val badResult =
      VoteResultDTO(Some("X000001"), Some("Test"), Some("User"), Some("Jeffries"), Some("D"), Some("NC"))
    val dto    = validVoteMembers.copy(voteQuestion = Some("On Passage"), results = Some(List(badResult)))
    val result = dto.toDO(noBillLookup)
    val _      = result.isLeft shouldBe true
    result.left.map(msg => msg.contains("Jeffries")) shouldBe Left(true)
  }

  it should "leave voteCastCandidateName as None for canonical casts even on an Election vote" in {
    // Mid-roll-call, a member might formally vote "Present" instead of for a candidate. That should still
    // parse as VoteCast.Present with no candidate name — the DB CHECK constraint enforces the invariant
    // (position <> 'Candidate' -> candidate_name IS NULL).
    val presentRow =
      VoteResultDTO(Some("P000001"), Some("Present"), Some("Voter"), Some("Present"), Some("I"), Some("VT"))
    val dto = validVoteMembers.copy(
      voteQuestion = Some("Election of the Speaker"),
      legislationType = None,
      legislationNumber = None,
      legislationUrl = None,
      results = Some(List(presentRow)),
    )
    val Right(result) = dto.toDO(noBillLookup): @unchecked
    val pos           = result.positions.headOption
    val _             = pos.flatMap(_.voteCast) shouldBe Some(VoteCast.Present)
    pos.flatMap(_.voteCastCandidateName) shouldBe None
  }

  it should "preserve parenthesized district disambiguators in candidate names (e.g., 'Johnson (LA)')" in {
    // Multiple members share the same surname — Congress.gov disambiguates with "(STATE)".
    // The candidate-name column is a free-text TEXT, so the parenthesized form flows through.
    val row = VoteResultDTO(Some("J000299"), Some("Mike"), Some("Johnson"), Some("Johnson (LA)"), Some("R"), Some("LA"))
    val dto = validVoteMembers.copy(
      voteQuestion = Some("Election of the Speaker"),
      legislationType = None,
      legislationNumber = None,
      legislationUrl = None,
      results = Some(List(row)),
    )
    val Right(result) = dto.toDO(noBillLookup): @unchecked
    val _             = result.positions.headOption.flatMap(_.voteCast) shouldBe Some(VoteCast.Candidate)
    result.positions.headOption.flatMap(_.voteCastCandidateName) shouldBe Some("Johnson (LA)")
  }

  it should "fail when a position has an unrecognized party" in {
    val badResult = VoteResultDTO(Some("X000001"), Some("Test"), Some("User"), Some("Yea"), Some("Z"), Some("NC"))
    val dto       = validVoteMembers.copy(results = Some(List(badResult)))
    val result    = dto.toDO(noBillLookup)
    val _         = result.isLeft shouldBe true
    result.left.map(msg => msg.contains("Z")) shouldBe Left(true)
  }

  it should "fail when a position has an unrecognized state" in {
    val badResult =
      VoteResultDTO(Some("X000001"), Some("Test"), Some("User"), Some("Yea"), Some("D"), Some("ZZ"))
    val dto    = validVoteMembers.copy(results = Some(List(badResult)))
    val result = dto.toDO(noBillLookup)
    val _      = result.isLeft shouldBe true
    result.left.map(msg => msg.contains("ZZ")) shouldBe Left(true)
  }

  it should "fail when legislationType is invalid" in {
    val dto    = validVoteMembers.copy(legislationType = Some("invalid_type"))
    val result = dto.toDO(noBillLookup)
    val _      = result.isLeft shouldBe true
    result.left.map(msg => msg.contains("invalid_type")) shouldBe Left(true)
  }

  it should "handle positions with None voteCast, party, and state" in {
    val sparseResult  = VoteResultDTO(Some("X000001"), Some("Test"), Some("User"), None, None, None)
    val dto           = validVoteMembers.copy(results = Some(List(sparseResult)))
    val Right(result) = dto.toDO(noBillLookup): @unchecked
    val _             = result.positions.length shouldBe 1
    val pos           = result.positions.headOption
    val _             = pos.flatMap(_.voteCast) shouldBe None
    val _             = pos.flatMap(_.partyAtVote) shouldBe None
    pos.flatMap(_.stateAtVote) shouldBe None
  }

  it should "handle None legislationType" in {
    val dto           = validVoteMembers.copy(legislationType = None)
    val Right(result) = dto.toDO(noBillLookup): @unchecked
    result.vote.legislationType shouldBe None
  }

  // SenateVoteXmlDTO conversion tests

  private val senateVoteXml = SenateVoteXmlDTO(
    congress = 118,
    session = 1,
    voteNumber = 99,
    question = "On the Motion",
    voteDate = "2024-06-01",
    result = "Motion Agreed to",
    document = SenateVoteDocumentDTO(
      documentCongress = 118,
      documentType = "S.",
      documentNumber = "500",
      documentName = "S. 500",
      documentTitle = "A bill for testing purposes.",
      documentShortTitle = None,
    ),
    members = List(
      SenateVoteMemberXmlDTO("S0001", "John", "Smith", "D", "NY", "Yea"),
      SenateVoteMemberXmlDTO("S0002", "Jane", "Doe", "R", "TX", "Nay"),
    ),
  )

  private val completeMapping = Map("S0001" -> "B001001", "S0002" -> "B002002")

  "SenateVoteXmlDTO.toDO" should "convert with complete mapping" in {
    val Right(result) = senateVoteXml.toDO(completeMapping): @unchecked
    val _             = result.congress shouldBe 118
    val _             = result.chamber shouldBe "Senate"
    val _             = result.rollCallNumber shouldBe 99
    val _             = result.sessionNumber shouldBe Some(1)
    val _             = result.voteQuestion shouldBe Some("On the Motion")
    val _             = result.result shouldBe Some("Motion Agreed to")
    result.startDate shouldBe Some("2024-06-01")
  }

  it should "resolve lisMemberIds to bioguideIds in results" in {
    val Right(result) = senateVoteXml.toDO(completeMapping): @unchecked
    val members       = result.results.getOrElse(List.empty)
    val _             = members.length shouldBe 2
    val _             = members.flatMap(_.memberId) shouldBe List("B001001", "B002002")
    val _             = members.flatMap(_.firstName) shouldBe List("John", "Jane")
    members.flatMap(_.voteCast) shouldBe List("Yea", "Nay")
  }

  it should "fail with missing mappings listing unresolved IDs" in {
    val partialMapping = Map("S0001" -> "B001001")
    val result         = senateVoteXml.toDO(partialMapping)
    val _              = result.isLeft shouldBe true
    result.left.map(msg => msg.contains("S0002")) shouldBe Left(true)
  }

  it should "fail with empty mapping" in {
    val result = senateVoteXml.toDO(Map.empty)
    val _      = result.isLeft shouldBe true
    val _      = result.left.map(msg => msg.contains("S0001")) shouldBe Left(true)
    result.left.map(msg => msg.contains("S0002")) shouldBe Left(true)
  }

  "buildVoteNaturalKey" should "construct correct natural key with session" in {
    val _ = VoteConversions.buildVoteNaturalKey(118, "House", 1, 42) shouldBe "118-House-1-42"
    val _ = VoteConversions.buildVoteNaturalKey(117, "Senate", 2, 100) shouldBe "117-Senate-2-100"
    VoteConversions.buildVoteNaturalKey(119, "House", 1, 17) shouldBe "119-House-1-17"
  }

}
