package repcheck.shared.models.llm.agentic

import java.util.UUID

import scala.concurrent.duration.DurationInt

import io.circe.Json
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repcheck.shared.models.llm.tool.{ToolCall, ToolResult}

class AgenticContractsSpec extends AnyFlatSpec with Matchers {

  private val turn = Turn(
    index = 0,
    toolCalls = List(ToolCall("search", Json.obj("q" -> Json.fromString("x")))),
    toolResults = List(ToolResult("search", Json.fromInt(1), isError = false)),
  )

  "Turn" should "round-trip through circe" in {
    turn.asJson.as[Turn] shouldBe Right(turn)
  }

  "AgenticResult" should "round-trip through circe (with a typed output)" in {
    val result =
      AgenticResult(
        output = "done",
        iterations = 2,
        transcript = List(turn),
        correlationId = UUID.fromString("00000000-0000-0000-0000-000000000001"),
      )
    result.asJson.as[AgenticResult[String]] shouldBe Right(result)
  }

  "LoopPolicy.default" should "bound iterations and per-call timeout with no token budget" in {
    LoopPolicy.default shouldBe LoopPolicy(maxIterations = 8, perCallTimeout = 60.seconds, tokenBudget = None)
  }

}
