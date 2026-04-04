package repcheck.shared.models.prompt

object WeightTranslator {

  def translate(weight: Double, content: String): String =
    if (weight == 1.0) {
      s"You MUST: $content"
    } else if (weight >= 0.7) {
      content
    } else if (weight >= 0.3) {
      s"When possible: $content"
    } else {
      s"Consider: $content"
    }

}
