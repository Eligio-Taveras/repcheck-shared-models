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

  "VectorCodec Get" should "parse pgvector string format" in {
    val pgString = "[1.0,2.5,3.7]"
    val trimmed  = pgString.stripPrefix("[").stripSuffix("]")
    val result   = trimmed.split(",").map(_.trim.toFloat)
    result.toSeq shouldBe Seq(1.0f, 2.5f, 3.7f)
  }

  "VectorCodec Put" should "format array as pgvector string" in {
    val arr    = Array(1.0f, 2.5f, 3.7f)
    val result = arr.mkString("[", ",", "]")
    result shouldBe "[1.0,2.5,3.7]"
  }

}
