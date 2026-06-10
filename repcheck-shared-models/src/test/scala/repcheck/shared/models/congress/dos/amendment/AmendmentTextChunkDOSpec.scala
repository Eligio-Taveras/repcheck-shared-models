package repcheck.shared.models.congress.dos.amendment

import java.time.Instant

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class AmendmentTextChunkDOSpec extends AnyFlatSpec with Matchers {

  private val sample = AmendmentTextChunkDO(
    id = 1L,
    amendmentId = 42L,
    versionId = Some(7L),
    chunkIndex = 0,
    content = "SECTION 1. AMENDED. Striking 'X' and inserting 'Y'.",
    embedding = Some(Array.fill(1024)(0.001f)),
    createdAt = Some(Instant.parse("2026-05-08T12:00:00Z")),
  )

  "AmendmentTextChunkDO Circe codec" should "round-trip with all fields populated" in {
    val json    = sample.asJson
    val decoded = json.as[AmendmentTextChunkDO]
    val _       = decoded.isRight shouldBe true
    val dto     = decoded.getOrElse(fail("decode failed"))
    val _       = dto.id shouldBe 1L
    val _       = dto.amendmentId shouldBe 42L
    val _       = dto.versionId shouldBe Some(7L)
    val _       = dto.chunkIndex shouldBe 0
    val _       = dto.content should include("SECTION 1")
    val _       = dto.embedding.map(_.length) shouldBe Some(1024)
    dto.createdAt shouldBe Some(Instant.parse("2026-05-08T12:00:00Z"))
  }

  it should "round-trip with versionId + embedding + createdAt as None" in {
    val minimal = AmendmentTextChunkDO(
      id = 2L,
      amendmentId = 42L,
      versionId = None,
      chunkIndex = 1,
      content = "chunk body",
      embedding = None,
      createdAt = None,
    )
    val result = minimal.asJson.as[AmendmentTextChunkDO]
    val _      = result.isRight shouldBe true
    val dto    = result.getOrElse(fail("decode failed"))
    val _      = dto.versionId shouldBe None
    val _      = dto.embedding shouldBe None
    dto.createdAt shouldBe None
  }

  it should "fail on missing required field" in {
    decode[AmendmentTextChunkDO]("""{"id":1}""").isLeft shouldBe true
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.postgres.implicits._
    import com.repcheck.utils.doobie.VectorCodec.floatArrayGet
    implicitly[Read[AmendmentTextChunkDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.postgres.implicits._
    import com.repcheck.utils.doobie.VectorCodec.floatArrayPut
    implicitly[Write[AmendmentTextChunkDO]].shouldBe(a[AnyRef])
  }

  it should "accumulate decode errors" in {
    import io.circe.Decoder
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[AmendmentTextChunkDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

  it should "use the 1024-dim qwen3-embedding shape rather than the legacy 1536-dim" in {
    // §7.6 spec contract: amendment chunks are embedded with qwen3-embedding:0.6b which emits 1024-dim
    // vectors via truncated MRL output. The legacy amendment_text_versions.embedding column was 1536 dims
    // (migration 007); the chunks table (migration 040) is explicitly vector(1024) to match the bills side.
    val embeddingLength = sample.embedding.map(_.length)
    embeddingLength shouldBe Some(1024)
  }

}
