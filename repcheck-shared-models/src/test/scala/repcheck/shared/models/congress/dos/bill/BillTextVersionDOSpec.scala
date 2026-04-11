package repcheck.shared.models.congress.dos.bill

import java.time.Instant

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repcheck.shared.models.congress.common.FormatType

class BillTextVersionDOSpec extends AnyFlatSpec with Matchers {

  private val sampleVersion = BillTextVersionDO(
    id = 1L,
    billId = 1L,
    versionCode = "ih",
    versionType = "Introduced in House",
    versionDate = Some("2024-01-15"),
    formatType = Some(FormatType.FormattedXml),
    url = Some("https://congress.gov/bill/118/hr/1234/text/ih"),
    content = Some("Full text of the bill version"),
    embedding = None,
    fetchedAt = Some(Instant.parse("2024-06-01T12:00:00Z")),
    createdAt = Some(Instant.parse("2024-06-01T12:00:00Z")),
  )

  "BillTextVersionDO Circe codec" should "round-trip with all fields populated" in {
    val json    = sampleVersion.asJson
    val decoded = json.as[BillTextVersionDO]
    decoded shouldBe Right(sampleVersion)
  }

  it should "round-trip with optional fields as None" in {
    val minimal = BillTextVersionDO(
      id = 2L,
      billId = 1L,
      versionCode = "ih",
      versionType = "Introduced in House",
      versionDate = None,
      formatType = None,
      url = None,
      content = None,
      embedding = None,
      fetchedAt = None,
      createdAt = None,
    )
    minimal.asJson.as[BillTextVersionDO] shouldBe Right(minimal)
  }

  it should "fail on missing required field" in {
    decode[BillTextVersionDO]("""{"id":1}""").isLeft shouldBe true
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.postgres.implicits._
    import repcheck.shared.models.codecs.VectorCodec.floatArrayGet
    import repcheck.shared.models.congress.common.DoobieEnumInstances._
    implicitly[Read[BillTextVersionDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.postgres.implicits._
    import repcheck.shared.models.codecs.VectorCodec.floatArrayPut
    import repcheck.shared.models.congress.common.DoobieEnumInstances._
    implicitly[Write[BillTextVersionDO]].shouldBe(a[AnyRef])
  }

  it should "accumulate decode errors" in {
    import io.circe.Decoder
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[BillTextVersionDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

}
