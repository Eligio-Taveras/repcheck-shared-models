package repcheck.shared.models.llm.tool

import io.circe.Json
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repcheck.shared.models.llm.output.{ClusterConceptOutput, TaxonomyOutput}

class ToolContractsSpec extends AnyFlatSpec with Matchers {

  "ToolCall / ToolResult / ToolInputError" should "round-trip through circe" in {
    val call = ToolCall("search_taxonomy", Json.obj("q" -> Json.fromString("health")))
    val res  = ToolResult("search_taxonomy", Json.arr(Json.fromInt(1), Json.fromInt(2)), isError = false)
    val err  = ToolInputError("q", "must be a string")
    call.asJson.as[ToolCall] shouldBe Right(call)
    res.asJson.as[ToolResult] shouldBe Right(res)
    err.asJson.as[ToolInputError] shouldBe Right(err)
  }

  "ToolSpec.from" should "derive schemas and examples from the In/Out codecs" in {
    val spec = ToolSpec.from[TaxonomyOutput, ClusterConceptOutput]("propose", "proposes nodes")
    spec.name shouldBe "propose"
    spec.parametersSchema shouldBe summon[repcheck.shared.models.llm.codec.StructuredCodec[TaxonomyOutput]].jsonSchema
    spec.exampleResult shouldBe summon[
      repcheck.shared.models.llm.codec.StructuredCodec[ClusterConceptOutput]
    ].exampleJson
    spec.asJson.as[ToolSpec] shouldBe Right(spec)
  }

  // A concrete tool exercising the typed-both-ways contract.
  private object LengthTool extends LlmTool[Option, String, Int] {
    val spec: ToolSpec = ToolSpec("length", "string length", Json.obj(), Json.obj(), Json.obj(), Json.obj())

    def decode(arguments: Json): Either[ToolInputError, String] =
      arguments.asString.toRight(ToolInputError("arguments", "expected a string"))

    def execute(in: String): Option[Int] = Some(in.length)
    def encodeResult(out: Int): Json     = Json.fromInt(out)
  }

  "LlmTool" should "decode valid args, reject invalid args, execute, and encode the result" in {
    LengthTool.decode(Json.fromString("abcd")) shouldBe Right("abcd")
    LengthTool.decode(Json.fromInt(3)) shouldBe Left(ToolInputError("arguments", "expected a string"))
    LengthTool.execute("abcd") shouldBe Some(4)
    LengthTool.encodeResult(4) shouldBe Json.fromInt(4)
  }

  "LlmTool.invoke" should "run the full json round-trip, surfacing decode errors as Left" in {
    LengthTool.invoke(Json.fromString("abcd")) shouldBe Some(Right(Json.fromInt(4)))
    LengthTool.invoke(Json.fromInt(3)) shouldBe Some(Left(ToolInputError("arguments", "expected a string")))
  }

}
