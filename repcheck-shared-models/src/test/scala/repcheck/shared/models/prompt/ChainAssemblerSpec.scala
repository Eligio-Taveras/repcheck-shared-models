package repcheck.shared.models.prompt

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ChainAssemblerSpec extends AnyFlatSpec with Matchers {

  private val assembler = DefaultChainAssembler

  private val systemPromptFragment =
    PromptFragment("system-base", PromptStage.System, 1.0, "1.0.0", "You are an assistant.")

  private val personaPromptFragment =
    PromptFragment("analyst", PromptStage.Persona, 0.8, "1.0.0", "Act as a policy analyst.")

  private val lensPromptFragment1 =
    PromptFragment("fiscal-lens", PromptStage.Lens, 0.5, "1.0.0", "Focus on fiscal impact.")

  private val lensPromptFragment2 =
    PromptFragment("social-lens", PromptStage.Lens, 0.5, "1.0.0", "Focus on social impact.")

  private val contextPromptFragment =
    PromptFragment("bill-context", PromptStage.Context, 0.8, "1.0.0", "Bill text: {{bill_text}}")

  private val guardrailsPromptFragment =
    PromptFragment("safety", PromptStage.Guardrails, 1.0, "1.0.0", "Do not hallucinate.")

  private val outputPromptFragment = PromptFragment("json-out", PromptStage.Output, 1.0, "1.0.0", "Return JSON.")
  private val customPromptFragment = PromptFragment("custom-1", PromptStage.Custom, 0.6, "1.0.0", "Extra instruction.")

  private val allPromptFragments: Map[String, PromptFragment] = Map(
    "system-base"  -> systemPromptFragment,
    "analyst"      -> personaPromptFragment,
    "fiscal-lens"  -> lensPromptFragment1,
    "social-lens"  -> lensPromptFragment2,
    "bill-context" -> contextPromptFragment,
    "safety"       -> guardrailsPromptFragment,
    "json-out"     -> outputPromptFragment,
    "custom-1"     -> customPromptFragment,
  )

  "DefaultChainAssembler" should "assemble with one PromptFragment per stage in correct order" in {
    val profile = PromptProfile(
      "test",
      List(
        StageConfig(PromptStage.Output, List("json-out"), 1.0),
        StageConfig(PromptStage.System, List("system-base"), 1.0),
        StageConfig(PromptStage.Persona, List("analyst"), 0.8),
        StageConfig(PromptStage.Guardrails, List("safety"), 1.0),
      ),
    )
    val result = assembler.assemble(profile, allPromptFragments, Map.empty)
    val _      = result.isRight shouldBe true
    result.foreach { assembled =>
      val sections = assembled.split("\n\n")
      val _        = sections.length shouldBe 4
      val _        = sections(0) should include("You MUST: You are an assistant.")
      val _        = sections(1) should include("Act as a policy analyst.")
      val _        = sections(2) should include("You MUST: Do not hallucinate.")
      sections(3) should include("You MUST: Return JSON.")
    }
  }

  it should "assemble with multiple Lens PromptFragments together" in {
    val profile = PromptProfile(
      "test",
      List(
        StageConfig(PromptStage.System, List("system-base"), 1.0),
        StageConfig(PromptStage.Lens, List("fiscal-lens", "social-lens"), 0.5),
      ),
    )
    val result = assembler.assemble(profile, allPromptFragments, Map.empty)
    val _      = result.isRight shouldBe true
    result.foreach { assembled =>
      val _ = assembled should include("When possible: Focus on fiscal impact.")
      assembled should include("When possible: Focus on social impact.")
    }
  }

  it should "return Left with missing PromptFragment names" in {
    val profile = PromptProfile(
      "test",
      List(
        StageConfig(PromptStage.System, List("system-base", "nonexistent-fragment"), 1.0),
        StageConfig(PromptStage.Lens, List("missing-lens"), 0.5),
      ),
    )
    val result = assembler.assemble(profile, allPromptFragments, Map.empty)
    val _      = result.isLeft shouldBe true
    result.swap.foreach { error =>
      val _ = error should include("nonexistent-fragment")
      error should include("missing-lens")
    }
  }

  it should "replace context placeholders" in {
    val profile = PromptProfile(
      "test",
      List(
        StageConfig(PromptStage.Context, List("bill-context"), 0.8)
      ),
    )
    val context = Map("bill_text" -> "This is the actual bill text.")
    val result  = assembler.assemble(profile, allPromptFragments, context)
    val _       = result.isRight shouldBe true
    result.foreach(assembled => assembled should include("Bill text: This is the actual bill text."))
  }

  it should "leave unmatched placeholders as-is" in {
    val profile = PromptProfile(
      "test",
      List(
        StageConfig(PromptStage.Context, List("bill-context"), 0.8)
      ),
    )
    val result = assembler.assemble(profile, allPromptFragments, Map.empty)
    val _      = result.isRight shouldBe true
    result.foreach(assembled => assembled should include("{{bill_text}}"))
  }

  it should "order stages correctly: System > Persona > Lens > Context > Custom > Guardrails > Output" in {
    val profile = PromptProfile(
      "full",
      List(
        StageConfig(PromptStage.Output, List("json-out"), 1.0),
        StageConfig(PromptStage.Custom, List("custom-1"), 0.6),
        StageConfig(PromptStage.System, List("system-base"), 1.0),
        StageConfig(PromptStage.Guardrails, List("safety"), 1.0),
        StageConfig(PromptStage.Context, List("bill-context"), 0.8),
        StageConfig(PromptStage.Lens, List("fiscal-lens"), 0.5),
        StageConfig(PromptStage.Persona, List("analyst"), 0.8),
      ),
    )
    val context = Map("bill_text" -> "Some bill")
    val result  = assembler.assemble(profile, allPromptFragments, context)
    val _       = result.isRight shouldBe true
    result.foreach { assembled =>
      val sysIdx = assembled.indexOf("You are an assistant.")
      val perIdx = assembled.indexOf("Act as a policy analyst.")
      val lnsIdx = assembled.indexOf("Focus on fiscal impact.")
      val ctxIdx = assembled.indexOf("Bill text: Some bill")
      val cusIdx = assembled.indexOf("Extra instruction.")
      val grdIdx = assembled.indexOf("Do not hallucinate.")
      val outIdx = assembled.indexOf("Return JSON.")
      val _      = sysIdx should be < perIdx
      val _      = perIdx should be < lnsIdx
      val _      = lnsIdx should be < ctxIdx
      val _      = ctxIdx should be < cusIdx
      val _      = cusIdx should be < grdIdx
      grdIdx should be < outIdx
    }
  }

}
