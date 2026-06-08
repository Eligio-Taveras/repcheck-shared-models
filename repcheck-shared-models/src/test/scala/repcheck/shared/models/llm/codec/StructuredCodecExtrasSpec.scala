package repcheck.shared.models.llm.codec

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repcheck.shared.models.llm.output.{ClusterConceptOutput, TaxonomyOutput}

/** Covers the StructuredCodec/StructuredSchema members not exercised by the law suite. */
class StructuredCodecExtrasSpec extends AnyFlatSpec with Matchers {

  "StructuredCodec.apply" should "summon the companion given" in {
    StructuredCodec[TaxonomyOutput].canonicalExample shouldBe a[TaxonomyOutput]
  }

  "exampleJson" should "equal the encoded canonical example" in {
    val sc = StructuredCodec[TaxonomyOutput]
    sc.exampleJson shouldBe sc.encoder(sc.canonicalExample)
  }

  "sampleJson" should "be deterministic by seed and decode back to a value" in {
    val sc = StructuredCodec[ClusterConceptOutput]
    sc.sampleJson(42L) shouldBe sc.sampleJson(42L)
    sc.decoder.decodeJson(sc.sampleJson(42L)).isRight shouldBe true
  }

  "StructuredSchema.from" should "expose the codec's schema and example" in {
    val ss = StructuredSchema.from[ClusterConceptOutput]
    val sc = StructuredCodec[ClusterConceptOutput]
    ss.jsonSchema shouldBe sc.jsonSchema
    ss.example shouldBe sc.exampleJson
  }

}
