package repcheck.shared.models.congress.dos.bill

import java.time.Instant

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class RawBillTextDOSpec extends AnyFlatSpec with Matchers {

  private val sampleChunk = RawBillTextDO(
    id = 1L,
    billId = 42L,
    versionId = Some(7L),
    chunkIndex = 0,
    content = "SECTION 1. SHORT TITLE. This Act may be cited as the Example Act.",
    embedding = Some(Array.fill(1536)(0.001f)),
    createdAt = Some(Instant.parse("2026-04-24T22:00:00Z")),
  )

  "RawBillTextDO Circe codec" should "round-trip with all fields populated" in {
    val json    = sampleChunk.asJson
    val decoded = json.as[RawBillTextDO]
    val _       = decoded.isRight shouldBe true
    val dto     = decoded.getOrElse(fail("decode failed"))
    val _       = dto.id shouldBe 1L
    val _       = dto.billId shouldBe 42L
    val _       = dto.versionId shouldBe Some(7L)
    val _       = dto.chunkIndex shouldBe 0
    val _       = dto.content should include("SECTION 1")
    val _       = dto.embedding.map(_.length) shouldBe Some(1536)
    dto.createdAt shouldBe Some(Instant.parse("2026-04-24T22:00:00Z"))
  }

  it should "round-trip with versionId + embedding + createdAt as None" in {
    val minimal = RawBillTextDO(
      id = 2L,
      billId = 42L,
      versionId = None,
      chunkIndex = 1,
      content = "chunk body",
      embedding = None,
      createdAt = None,
    )
    val result = minimal.asJson.as[RawBillTextDO]
    val _      = result.isRight shouldBe true
    val dto    = result.getOrElse(fail("decode failed"))
    val _      = dto.versionId shouldBe None
    val _      = dto.embedding shouldBe None
    dto.createdAt shouldBe None
  }

  it should "fail on missing required field" in {
    decode[RawBillTextDO]("""{"id":1}""").isLeft shouldBe true
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.postgres.implicits._
    import repcheck.shared.models.codecs.VectorCodec.floatArrayGet
    implicitly[Read[RawBillTextDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.postgres.implicits._
    import repcheck.shared.models.codecs.VectorCodec.floatArrayPut
    implicitly[Write[RawBillTextDO]].shouldBe(a[AnyRef])
  }

  it should "accumulate decode errors" in {
    import io.circe.Decoder
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[RawBillTextDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

}
