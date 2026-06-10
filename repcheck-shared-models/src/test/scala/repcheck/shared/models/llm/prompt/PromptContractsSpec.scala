package repcheck.shared.models.llm.prompt

import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PromptContractsSpec extends AnyFlatSpec with Matchers {

  private val prompt = AssembledPrompt("sys", List(ChatMessage("user", "hi"), ChatMessage("tool", "{}")))

  "ChatMessage" should "round-trip through circe" in {
    val message = ChatMessage("user", "hello")
    message.asJson.as[ChatMessage] shouldBe Right(message)
  }

  "AssembledPrompt" should "round-trip through circe" in {
    prompt.asJson.as[AssembledPrompt] shouldBe Right(prompt)
  }

  "appended" should "add a message to the end, preserving order" in {
    prompt.appended(ChatMessage("user", "next")).messages.map(_.content) shouldBe List("hi", "{}", "next")
  }

}
