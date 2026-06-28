package repcheck.shared.models.llm.output

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import org.scalacheck.Gen
import repcheck.shared.models.llm.codec.StructuredCodec
import repcheck.shared.models.llm.{Effect, Impact, Scope}

/**
 * One stance-tagged topic of a concept (vectors-primary). `topic` is the neutrally-framed noun phrase that gets
 * embedded as the cross-bill retrieval vector; `phrase` carries the policy direction, and `effect`/`impact`/`scope`/
 * `entity` are the stance metadata persisted for alignment scoring.
 */
final case class ConceptTopic(
  phrase: String,
  topic: String,
  effect: Effect,
  entity: String,
  impact: Impact,
  scope: Scope,
)

object ConceptTopic {
  given Encoder[ConceptTopic] = deriveEncoder[ConceptTopic]
  given Decoder[ConceptTopic] = deriveDecoder[ConceptTopic]
}

/**
 * LLM output of the per-cluster summarize step (vectors-primary): a short `label`, a retrieval-optimized `summary`, and
 * the distinct stance-tagged `topics` the concept covers. Replaces the taxonomy-era [[ClusterConceptOutput]] — the
 * orchestrator embeds `summary` (group vector) + each topic's `topic` (topic vector) for cross-bill search.
 */
final case class ConceptSummaryWithTopics(label: String, summary: String, topics: List[ConceptTopic])

object ConceptSummaryWithTopics {

  given Encoder[ConceptSummaryWithTopics] = deriveEncoder[ConceptSummaryWithTopics]
  given Decoder[ConceptSummaryWithTopics] = deriveDecoder[ConceptSummaryWithTopics]

  private val example =
    ConceptSummaryWithTopics(
      "Children's health policy",
      "Establishes the short title of the bill as the Improving the Health of Children Act.",
      List(
        ConceptTopic(
          "establishing official title for children's health legislation",
          "children's health policy",
          Effect.Modifies,
          "children",
          Impact.Neutral,
          Scope.Minor,
        )
      ),
    )

  private val topicGen: Gen[ConceptTopic] = for {
    phrase <- Gen.alphaNumStr
    topic  <- Gen.alphaNumStr
    effect <- Gen.oneOf(Effect.values.toIndexedSeq)
    entity <- Gen.alphaNumStr
    impact <- Gen.oneOf(Impact.values.toIndexedSeq)
    scope  <- Gen.oneOf(Scope.values.toIndexedSeq)
  } yield ConceptTopic(phrase, topic, effect, entity, impact, scope)

  private val gen: Gen[ConceptSummaryWithTopics] = for {
    label   <- Gen.alphaNumStr
    summary <- Gen.alphaNumStr
    n       <- Gen.choose(1, 5)
    topics  <- Gen.listOfN(n, topicGen)
  } yield ConceptSummaryWithTopics(label, summary, topics)

  given StructuredCodec[ConceptSummaryWithTopics] =
    StructuredCodec.instance(TapirSchemas.conceptSummaryWithTopics, example, gen)

}
