package repcheck.shared.models.llm.output

import sttp.tapir.Schema

/**
 * Tapir-derived JSON schemas for the Component-10 analysis LLM outputs, isolated here (coverage-excluded like
 * [[TapirSchemas]] via the `.*TapirSchemas.*` rule) because `Schema.derived` is a macro tests can't exercise.
 *
 * The enum-field constraints come from each enum's `LlmEnumCompanion` (its `given Schema`), so the derived output
 * schemas automatically publish the closed sets. Verified by the §10c #5b law and
 * `AnalysisOutputSchemaEnforcementSpec`.
 */
private[output] object AnalysisTapirSchemas {

  private given topicStance: Schema[TopicStance] = Schema.derived[TopicStance]
  private given impactItem: Schema[ImpactItem]   = Schema.derived[ImpactItem]
  private given porkFinding: Schema[PorkFinding] = Schema.derived[PorkFinding]

  val stanceClassificationOutput: Schema[StanceClassificationOutput] = Schema.derived[StanceClassificationOutput]
  val porkDetectionOutput: Schema[PorkDetectionOutput]               = Schema.derived[PorkDetectionOutput]
  val impactAnalysisOutput: Schema[ImpactAnalysisOutput]             = Schema.derived[ImpactAnalysisOutput]
}
