package repcheck.shared.models.llm

import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class StanceTypeSpec extends AnyFlatSpec with Matchers {

  "StanceType.fromString" should "parse all values" in {
    StanceType.fromString("Conservative") shouldBe Right(StanceType.Conservative)
    StanceType.fromString("Progressive") shouldBe Right(StanceType.Progressive)
    StanceType.fromString("Bipartisan") shouldBe Right(StanceType.Bipartisan)
    StanceType.fromString("Neutral") shouldBe Right(StanceType.Neutral)
  }

  it should "have exactly 4 values" in {
    StanceType.values.length shouldBe 4
  }

  it should "be case-insensitive" in {
    StanceType.fromString("CONSERVATIVE") shouldBe Right(StanceType.Conservative)
    StanceType.fromString("conservative") shouldBe Right(StanceType.Conservative)
    StanceType.fromString("Conservative") shouldBe Right(StanceType.Conservative)
    StanceType.fromString("progressive") shouldBe Right(StanceType.Progressive)
    StanceType.fromString("BIPARTISAN") shouldBe Right(StanceType.Bipartisan)
  }

  it should "return Left for unknown values" in {
    val result = StanceType.fromString("Unknown")
    result.isLeft shouldBe true
  }

  "StanceType Circe codec" should "round-trip all values" in {
    StanceType.values.foreach { st =>
      val json    = st.asJson
      val decoded = json.as[StanceType]
      decoded shouldBe Right(st)
    }
  }

  it should "serialize to lowercase" in {
    StanceType.Conservative.asJson.asString shouldBe Some("conservative")
    StanceType.Progressive.asJson.asString shouldBe Some("progressive")
    StanceType.Bipartisan.asJson.asString shouldBe Some("bipartisan")
    StanceType.Neutral.asJson.asString shouldBe Some("neutral")
  }

}
