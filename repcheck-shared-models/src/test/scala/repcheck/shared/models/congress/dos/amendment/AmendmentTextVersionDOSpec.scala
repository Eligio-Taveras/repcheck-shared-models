package repcheck.shared.models.congress.dos.amendment

import java.time.Instant

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repcheck.shared.models.congress.common.FormatType

class AmendmentTextVersionDOSpec extends AnyFlatSpec with Matchers {

  private val sample = AmendmentTextVersionDO(
    id = 11L,
    amendmentId = 42L,
    versionType = "Submitted",
    versionDate = Instant.parse("2021-08-01T04:00:00Z"),
    formatType = FormatType.FormattedText,
    url = "https://www.congress.gov/117/crec/2021/08/01/167/136/CREC-2021-08-01-pt1-PgS5255.htm",
    downloadUrl = Some("https://api.govinfo.gov/packages/CREC-2021-08-01/granules/CREC-2021-08-01-pt1-PgS5255/htm"),
    textLength = Some(123456),
    fetchedAt = Some(Instant.parse("2026-05-08T12:00:00Z")),
    createdAt = Some(Instant.parse("2026-05-08T11:59:00Z")),
  )

  "AmendmentTextVersionDO Circe codec" should "round-trip with all fields populated" in {
    val json    = sample.asJson
    val decoded = json.as[AmendmentTextVersionDO]
    decoded shouldBe Right(sample)
  }

  it should "round-trip with optional fields as None" in {
    val minimal = AmendmentTextVersionDO(
      id = 12L,
      amendmentId = 42L,
      versionType = "Modified",
      versionDate = Instant.parse("2021-08-02T04:00:00Z"),
      formatType = FormatType.PDF,
      url = "https://www.congress.gov/117/crec/2021/08/02/167/137/CREC-2021-08-02-pt1-PgS5300.pdf",
      downloadUrl = None,
      textLength = None,
      fetchedAt = None,
      createdAt = None,
    )
    minimal.asJson.as[AmendmentTextVersionDO] shouldBe Right(minimal)
  }

  it should "fail on missing required field" in {
    decode[AmendmentTextVersionDO]("""{"id":1}""").isLeft shouldBe true
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.postgres.implicits._
    import repcheck.shared.models.congress.common.DoobieEnumInstances._
    implicitly[Read[AmendmentTextVersionDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.postgres.implicits._
    import repcheck.shared.models.congress.common.DoobieEnumInstances._
    implicitly[Write[AmendmentTextVersionDO]].shouldBe(a[AnyRef])
  }

  it should "accumulate decode errors" in {
    import io.circe.Decoder
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[AmendmentTextVersionDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

  it should "carry the surrogate amendmentId rather than a natural-key string" in {
    // Compile-time anchor: post db-migrations 011 the amendment_text_versions FK is BIGINT, not TEXT.
    val names = sample.productElementNames.toList
    val _     = names should contain("amendmentId")
    names should not contain "naturalKey"
  }

}
