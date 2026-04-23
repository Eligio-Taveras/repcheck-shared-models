package repcheck.shared.models.congress.vote

import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class VoteTypeSpec extends AnyFlatSpec with Matchers {

  "VoteType.fromString" should "parse all canonical values" in {
    val _ = VoteType.fromString("Passage") shouldBe Right(VoteType.Passage)
    val _ = VoteType.fromString("Conference Report") shouldBe Right(VoteType.ConferenceReport)
    val _ = VoteType.fromString("Cloture") shouldBe Right(VoteType.Cloture)
    val _ = VoteType.fromString("Veto Override") shouldBe Right(VoteType.VetoOverride)
    val _ = VoteType.fromString("Amendment") shouldBe Right(VoteType.Amendment)
    val _ = VoteType.fromString("Committee") shouldBe Right(VoteType.Committee)
    val _ = VoteType.fromString("Recommit") shouldBe Right(VoteType.Recommit)
    VoteType.fromString("Other") shouldBe Right(VoteType.Other)
  }

  it should "accept concatenated aliases" in {
    val _ = VoteType.fromString("ConferenceReport") shouldBe Right(VoteType.ConferenceReport)
    VoteType.fromString("VetoOverride") shouldBe Right(VoteType.VetoOverride)
  }

  it should "be case-insensitive" in {
    val _ = VoteType.fromString("passage") shouldBe Right(VoteType.Passage)
    VoteType.fromString("CLOTURE") shouldBe Right(VoteType.Cloture)
  }

  it should "return Left for unknown values" in {
    val result = VoteType.fromString("Unknown")
    result.isLeft shouldBe true
  }

  "VoteType.fromQuestion" should "classify 'On Passage' as Passage" in {
    VoteType.fromQuestion("On Passage of the Bill") shouldBe VoteType.Passage
  }

  it should "classify 'On Cloture' as Cloture" in {
    VoteType.fromQuestion("On the Cloture Motion") shouldBe VoteType.Cloture
  }

  it should "classify conference report questions" in {
    VoteType.fromQuestion("On Agreeing to the Conference Report") shouldBe VoteType.ConferenceReport
  }

  it should "classify veto override questions" in {
    VoteType.fromQuestion("On the Veto Message") shouldBe VoteType.VetoOverride
  }

  it should "classify amendment questions" in {
    VoteType.fromQuestion("On the Amendment") shouldBe VoteType.Amendment
  }

  it should "classify recommit questions" in {
    VoteType.fromQuestion("On Motion to Recommit") shouldBe VoteType.Recommit
  }

  it should "return Other for unrecognized questions" in {
    VoteType.fromQuestion("Something completely different") shouldBe VoteType.Other
  }

  it should "accept 'Election' via fromString + concatenated alias" in {
    val _ = VoteType.fromString("Election") shouldBe Right(VoteType.Election)
    VoteType.fromString("election") shouldBe Right(VoteType.Election)
  }

  it should "classify 'Election of the Speaker' as Election (fromQuestion)" in {
    VoteType.fromQuestion("Election of the Speaker") shouldBe VoteType.Election
  }

  it should "classify 'Election of the Clerk' as Election (future-proof for other officer elections)" in {
    VoteType.fromQuestion("Election of the Clerk") shouldBe VoteType.Election
  }

  it should "classify 'Election' before 'Amendment' when a question contains both substrings" in {
    // Hypothetical future question — ensures our substring-ordering doesn't accidentally route
    // an election question to Amendment.
    VoteType.fromQuestion("Election of the Amendments Clerk") shouldBe VoteType.Election
  }

  "VoteType Circe codec" should "round-trip values" in {
    VoteType.values.foreach { vt =>
      val json    = vt.asJson
      val decoded = json.as[VoteType]
      decoded shouldBe Right(vt)
    }
  }

}
