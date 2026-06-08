package repcheck.shared.models.llm.output

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import org.scalacheck.Gen
import repcheck.shared.models.llm.codec.StructuredCodec

/**
 * LLM output of the per-cluster concept step (D4): the extracted concept `label` + `summary` for a section cluster,
 * plus the taxonomy node ids selected. `selectedNodeIds` is constrained downstream to the active taxonomy's candidate
 * ids ∪ the reserved `unclassified` node — a hallucinated id fails validation and re-prompts.
 */
final case class ClusterConceptOutput(label: String, summary: String, selectedNodeIds: List[Long])

object ClusterConceptOutput {
  given Encoder[ClusterConceptOutput] = deriveEncoder[ClusterConceptOutput]
  given Decoder[ClusterConceptOutput] = deriveDecoder[ClusterConceptOutput]

  private val example =
    ClusterConceptOutput("Veterans education benefits", "Expands work-study eligibility for veterans.", List(42L, 108L))

  private val gen: Gen[ClusterConceptOutput] = for {
    label   <- Gen.identifier
    summary <- Gen.alphaNumStr
    n       <- Gen.choose(0, 5)
    ids     <- Gen.listOfN(n, Gen.choose(1L, 100000L))
  } yield ClusterConceptOutput(label, summary, ids)

  given StructuredCodec[ClusterConceptOutput] =
    StructuredCodec.instance(TapirSchemas.clusterConceptOutput, example, gen)

}
