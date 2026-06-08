package repcheck.shared.models.llm.output

import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repcheck.shared.models.llm.codec.StructuredCodecLaws

class TaxonomyOutputCodecSpec extends StructuredCodecLaws[TaxonomyOutput]("TaxonomyOutput")

class ClusterConceptOutputCodecSpec extends StructuredCodecLaws[ClusterConceptOutput]("ClusterConceptOutput")

/** Directly exercises the nested ProposedNode codecs (both parentName cases) so its derived encoder/decoder are hit. */
class ProposedNodeCodecSpec extends AnyFlatSpec with Matchers {

  "ProposedNode" should "round-trip with and without a parent" in {
    val root  = ProposedNode("Energy", "Energy policy.", None)
    val child = ProposedNode("Solar", "Solar incentives.", Some("Energy"))
    root.asJson.as[ProposedNode] shouldBe Right(root)
    child.asJson.as[ProposedNode] shouldBe Right(child)
  }

}
