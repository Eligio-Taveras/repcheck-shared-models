package repcheck.shared.models.prompt

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ChainAssemblerSpec extends AnyFlatSpec with Matchers {

  private val assembler = DefaultChainAssembler

  private val systemBlock     = InstructionBlock("system-base", PromptStage.System, 1.0, "1.0.0", "You are an assistant.")
  private val personaBlock    = InstructionBlock("analyst", PromptStage.Persona, 0.8, "1.0.0", "Act as a policy analyst.")
  private val lensBlock1      = InstructionBlock("fiscal-lens", PromptStage.Lens, 0.5, "1.0.0", "Focus on fiscal impact.")
  private val lensBlock2      = InstructionBlock("social-lens", PromptStage.Lens, 0.5, "1.0.0", "Focus on social impact.")
  private val contextBlock    = InstructionBlock("bill-context", PromptStage.Context, 0.8, "1.0.0", "Bill text: {{bill_text}}")
  private val guardrailsBlock = InstructionBlock("safety", PromptStage.Guardrails, 1.0, "1.0.0", "Do not hallucinate.")
  private val outputBlock     = InstructionBlock("json-out", PromptStage.Output, 1.0, "1.0.0", "Return JSON.")
  private val customBlock     = InstructionBlock("custom-1", PromptStage.Custom, 0.6, "1.0.0", "Extra instruction.")

  private val allBlocks: Map[String, InstructionBlock] = Map(
    "system-base"  -> systemBlock,
    "analyst"      -> personaBlock,
    "fiscal-lens"  -> lensBlock1,
    "social-lens"  -> lensBlock2,
    "bill-context" -> contextBlock,
    "safety"       -> guardrailsBlock,
    "json-out"     -> outputBlock,
    "custom-1"     -> customBlock
  )

  "DefaultChainAssembler" should "assemble with one block per stage in correct order" in {
    val profile = PromptProfile("test", List(
      StageConfig(PromptStage.Output, List("json-out"), 1.0),
      StageConfig(PromptStage.System, List("system-base"), 1.0),
      StageConfig(PromptStage.Persona, List("analyst"), 0.8),
      StageConfig(PromptStage.Guardrails, List("safety"), 1.0)
    ))
    val result = assembler.assemble(profile, allBlocks, Map.empty)
    result.isRight shouldBe true
    result.foreach { assembled =>
      val sections = assembled.split("\n\n")
      sections.length shouldBe 4
      sections(0) should include("You MUST: You are an assistant.")
      sections(1) should include("Act as a policy analyst.")
      sections(2) should include("You MUST: Do not hallucinate.")
      sections(3) should include("You MUST: Return JSON.")
    }
  }

  it should "assemble with multiple Lens blocks together" in {
    val profile = PromptProfile("test", List(
      StageConfig(PromptStage.System, List("system-base"), 1.0),
      StageConfig(PromptStage.Lens, List("fiscal-lens", "social-lens"), 0.5)
    ))
    val result = assembler.assemble(profile, allBlocks, Map.empty)
    result.isRight shouldBe true
    result.foreach { assembled =>
      assembled should include("When possible: Focus on fiscal impact.")
      assembled should include("When possible: Focus on social impact.")
    }
  }

  it should "return Left with missing block names" in {
    val profile = PromptProfile("test", List(
      StageConfig(PromptStage.System, List("system-base", "nonexistent-block"), 1.0),
      StageConfig(PromptStage.Lens, List("missing-lens"), 0.5)
    ))
    val result = assembler.assemble(profile, allBlocks, Map.empty)
    result.isLeft shouldBe true
    result.swap.foreach { error =>
      error should include("nonexistent-block")
      error should include("missing-lens")
    }
  }

  it should "replace context placeholders" in {
    val profile = PromptProfile("test", List(
      StageConfig(PromptStage.Context, List("bill-context"), 0.8)
    ))
    val context = Map("bill_text" -> "This is the actual bill text.")
    val result  = assembler.assemble(profile, allBlocks, context)
    result.isRight shouldBe true
    result.foreach { assembled =>
      assembled should include("Bill text: This is the actual bill text.")
    }
  }

  it should "leave unmatched placeholders as-is" in {
    val profile = PromptProfile("test", List(
      StageConfig(PromptStage.Context, List("bill-context"), 0.8)
    ))
    val result = assembler.assemble(profile, allBlocks, Map.empty)
    result.isRight shouldBe true
    result.foreach { assembled =>
      assembled should include("{{bill_text}}")
    }
  }

  it should "order stages correctly: System > Persona > Lens > Context > Custom > Guardrails > Output" in {
    val profile = PromptProfile("full", List(
      StageConfig(PromptStage.Output, List("json-out"), 1.0),
      StageConfig(PromptStage.Custom, List("custom-1"), 0.6),
      StageConfig(PromptStage.System, List("system-base"), 1.0),
      StageConfig(PromptStage.Guardrails, List("safety"), 1.0),
      StageConfig(PromptStage.Context, List("bill-context"), 0.8),
      StageConfig(PromptStage.Lens, List("fiscal-lens"), 0.5),
      StageConfig(PromptStage.Persona, List("analyst"), 0.8)
    ))
    val context = Map("bill_text" -> "Some bill")
    val result  = assembler.assemble(profile, allBlocks, context)
    result.isRight shouldBe true
    result.foreach { assembled =>
      val sysIdx = assembled.indexOf("You are an assistant.")
      val perIdx = assembled.indexOf("Act as a policy analyst.")
      val lnsIdx = assembled.indexOf("Focus on fiscal impact.")
      val ctxIdx = assembled.indexOf("Bill text: Some bill")
      val cusIdx = assembled.indexOf("Extra instruction.")
      val grdIdx = assembled.indexOf("Do not hallucinate.")
      val outIdx = assembled.indexOf("Return JSON.")
      sysIdx should be < perIdx
      perIdx should be < lnsIdx
      lnsIdx should be < ctxIdx
      ctxIdx should be < cusIdx
      cusIdx should be < grdIdx
      grdIdx should be < outIdx
    }
  }

}
