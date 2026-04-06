package repcheck.shared.models.congress.dos.bill

import java.time.Instant
import java.util.UUID

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class BillTextSectionDOSpec extends AnyFlatSpec with Matchers {

  private val sampleSection = BillTextSectionDO(
    sectionId = UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f12345678901"),
    versionId = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890"),
    billId = 1L,
    sectionIndex = 0,
    sectionIdentifier = Some("Sec. 1"),
    heading = Some("Short Title"),
    content = "This Act may be cited as the Example Act.",
    embedding = None,
    createdAt = Some(Instant.parse("2024-06-01T12:00:00Z")),
  )

  "BillTextSectionDO Circe codec" should "round-trip with all fields populated" in {
    val json    = sampleSection.asJson
    val decoded = json.as[BillTextSectionDO]
    decoded shouldBe Right(sampleSection)
  }

  it should "round-trip with sectionIdentifier and heading as None" in {
    val minimal = sampleSection.copy(
      sectionIdentifier = None,
      heading = None,
      embedding = None,
      createdAt = None,
    )
    minimal.asJson.as[BillTextSectionDO] shouldBe Right(minimal)
  }

  it should "fail on missing required field" in {
    decode[BillTextSectionDO]("""{"sectionId":"b2c3d4e5-f6a7-8901-bcde-f12345678901"}""").isLeft shouldBe true
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    import repcheck.shared.models.codecs.VectorCodec.floatArrayGet
    implicitly[Read[BillTextSectionDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._
    import repcheck.shared.models.codecs.VectorCodec.floatArrayPut
    implicitly[Write[BillTextSectionDO]].shouldBe(a[AnyRef])
  }

  it should "accumulate decode errors" in {
    import io.circe.Decoder
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[BillTextSectionDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

}
