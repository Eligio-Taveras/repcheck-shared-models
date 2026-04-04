package repcheck.shared.models.codecs

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class VectorCodecSpec extends AnyFlatSpec with Matchers {

  import VectorCodec.*

  "VectorCodec" should "round-trip a float array via Circe" in {
    val arr     = Array(1.0f, 2.5f, 3.7f)
    val json    = arr.asJson
    val decoded = json.as[Array[Float]]
    decoded.map(_.toSeq) shouldBe Right(arr.toSeq)
  }

  it should "encode empty array" in {
    val arr  = Array.empty[Float]
    val json = arr.asJson
    json.isArray shouldBe true
  }

  it should "decode empty JSON array" in {
    val result = decode[Array[Float]]("[]")
    result.map(_.length) shouldBe Right(0)
  }

  "VectorCodec.parseFloatVector" should "parse pgvector string with multiple values" in {
    val result = VectorCodec.parseFloatVector("[1.0,2.5,3.7]")
    result.toSeq shouldBe Seq(1.0f, 2.5f, 3.7f)
  }

  it should "parse pgvector string with whitespace around values" in {
    val result = VectorCodec.parseFloatVector("[ 1.0 , 2.5 , 3.7 ]")
    result.toSeq shouldBe Seq(1.0f, 2.5f, 3.7f)
  }

  it should "return empty array for empty pgvector string" in {
    val result = VectorCodec.parseFloatVector("[]")
    result shouldBe empty
  }

  it should "parse single-element pgvector string" in {
    val result = VectorCodec.parseFloatVector("[0.5]")
    result.toSeq shouldBe Seq(0.5f)
  }

  "VectorCodec.formatFloatVector" should "format array as pgvector string" in {
    val result = VectorCodec.formatFloatVector(Array(1.0f, 2.5f, 3.7f))
    result shouldBe "[1.0,2.5,3.7]"
  }

  it should "format empty array as empty pgvector string" in {
    val result = VectorCodec.formatFloatVector(Array.empty[Float])
    result shouldBe "[]"
  }

  it should "format single-element array" in {
    val result = VectorCodec.formatFloatVector(Array(0.5f))
    result shouldBe "[0.5]"
  }

}
