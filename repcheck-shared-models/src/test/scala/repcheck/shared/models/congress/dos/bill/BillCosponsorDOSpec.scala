package repcheck.shared.models.congress.dos.bill

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class BillCosponsorDOSpec extends AnyFlatSpec with Matchers {

  private val sampleCosponsor = BillCosponsorDO(
    billId = 1L,
    memberId = 1L,
    isOriginalCosponsor = Some(true),
    sponsorshipDate = Some("2024-01-20"),
  )

  "BillCosponsorDO Circe codec" should "round-trip with all fields populated" in {
    val json    = sampleCosponsor.asJson
    val decoded = json.as[BillCosponsorDO]
    decoded shouldBe Right(sampleCosponsor)
  }

  it should "round-trip with optional fields as None" in {
    val minimal = BillCosponsorDO(
      billId = 2L,
      memberId = 2L,
      isOriginalCosponsor = None,
      sponsorshipDate = None,
    )
    minimal.asJson.as[BillCosponsorDO] shouldBe Right(minimal)
  }

  it should "fail on missing required field" in {
    decode[BillCosponsorDO]("""{"billId":1}""").isLeft shouldBe true
  }

  it should "have Doobie Read instance" in {
    import doobie._
    implicitly[Read[BillCosponsorDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    implicitly[Write[BillCosponsorDO]].shouldBe(a[AnyRef])
  }

  it should "accumulate decode errors" in {
    import io.circe.Decoder
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[BillCosponsorDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

}
