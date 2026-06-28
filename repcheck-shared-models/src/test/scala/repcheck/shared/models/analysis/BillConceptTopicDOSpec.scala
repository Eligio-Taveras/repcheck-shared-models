package repcheck.shared.models.analysis

import java.time.Instant

import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repcheck.shared.models.llm.{Effect, Impact, Scope}

class BillConceptTopicDOSpec extends AnyFlatSpec with Matchers {

  private val base = BillConceptTopicDO(
    id = 1L,
    conceptGroupId = 42L,
    phrase = "expanding solar tax credits",
    topic = "solar energy incentives",
    effect = Effect.Expands,
    entity = "homeowners",
    impact = Impact.Positive,
    scope = Scope.Major,
    topicEmbedding = None,
    createdAt = Some(Instant.parse("2026-06-28T00:00:00Z")),
  )

  "BillConceptTopicDO" should "round-trip via circe when the embedding is absent" in {
    base.asJson.as[BillConceptTopicDO] shouldBe Right(base)
  }

  it should "preserve every field (incl. embedding) through circe" in {
    val withEmb = base.copy(topicEmbedding = Some(Array(0.1f, 0.2f, 0.3f)))
    withEmb.asJson.as[BillConceptTopicDO] match {
      case Right(d) =>
        val _ = d.id shouldBe withEmb.id
        val _ = d.conceptGroupId shouldBe withEmb.conceptGroupId
        val _ = d.phrase shouldBe withEmb.phrase
        val _ = d.topic shouldBe withEmb.topic
        val _ = d.effect shouldBe Effect.Expands
        val _ = d.entity shouldBe withEmb.entity
        val _ = d.impact shouldBe Impact.Positive
        val _ = d.scope shouldBe Scope.Major
        val _ = d.createdAt shouldBe withEmb.createdAt
        d.topicEmbedding.map(_.toList) shouldBe Some(List(0.1f, 0.2f, 0.3f))
      case Left(e) => fail(s"decode failed: $e")
    }
  }

}
