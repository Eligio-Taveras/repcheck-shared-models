package repcheck.shared.models.prompt

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class WeightTranslatorSpec extends AnyFlatSpec with Matchers {

  "WeightTranslator" should "wrap with 'You MUST:' for weight 1.0" in {
    WeightTranslator.translate(1.0, "Follow the rules") shouldBe "You MUST: Follow the rules"
  }

  it should "return content as-is for weight >= 0.7" in {
    WeightTranslator.translate(0.7, "Analyze the bill") shouldBe "Analyze the bill"
    WeightTranslator.translate(0.8, "Be thorough") shouldBe "Be thorough"
    WeightTranslator.translate(0.99, "Almost must") shouldBe "Almost must"
  }

  it should "wrap with 'When possible:' for weight >= 0.3 and < 0.7" in {
    WeightTranslator.translate(0.5, "Include examples") shouldBe "When possible: Include examples"
    WeightTranslator.translate(0.3, "Cite sources") shouldBe "When possible: Cite sources"
    WeightTranslator.translate(0.69, "Check references") shouldBe "When possible: Check references"
  }

  it should "wrap with 'Consider:' for weight < 0.3" in {
    WeightTranslator.translate(0.2, "Add humor") shouldBe "Consider: Add humor"
    WeightTranslator.translate(0.1, "Use analogies") shouldBe "Consider: Use analogies"
    WeightTranslator.translate(0.0, "Optional detail") shouldBe "Consider: Optional detail"
    WeightTranslator.translate(0.29, "Edge case") shouldBe "Consider: Edge case"
  }

}
