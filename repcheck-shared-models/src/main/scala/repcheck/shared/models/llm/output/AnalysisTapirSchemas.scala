package repcheck.shared.models.llm.output

import repcheck.shared.models.llm.{ImpactSeverity, PorkType, StanceType}
import sttp.tapir.{Schema, Validator}

/**
 * Tapir-derived JSON schemas for the Component-10 analysis LLM outputs, isolated here (coverage-excluded like
 * [[TapirSchemas]] via the `.*TapirSchemas.*` rule) because `Schema.derived` is a macro tests can't exercise.
 *
 * The enum schemas publish each closed set as an `enum` constraint so the model is told the allowed values up front
 * (the constrain half of the closed-set guarantee; the decoders are the reject half). Verified by the §10c #5b law and
 * `AnalysisOutputSchemaEnforcementSpec`.
 */
private[output] object AnalysisTapirSchemas {

  private given Schema[StanceType] =
    Schema
      .string[StanceType]
      .validate(Validator.enumeration(StanceType.values.toList, (s: StanceType) => Some(s.apiValue)))

  private given Schema[PorkType] =
    Schema
      .string[PorkType]
      .validate(Validator.enumeration(PorkType.values.toList, (p: PorkType) => Some(p.apiValue)))

  private given Schema[ImpactSeverity] =
    Schema
      .string[ImpactSeverity]
      .validate(Validator.enumeration(ImpactSeverity.values.toList, (i: ImpactSeverity) => Some(i.apiValue)))

  private given topicStance: Schema[TopicStance] = Schema.derived[TopicStance]
  private given impactItem: Schema[ImpactItem]   = Schema.derived[ImpactItem]
  private given porkFinding: Schema[PorkFinding] = Schema.derived[PorkFinding]

  val stanceClassificationOutput: Schema[StanceClassificationOutput] = Schema.derived[StanceClassificationOutput]
  val porkDetectionOutput: Schema[PorkDetectionOutput]               = Schema.derived[PorkDetectionOutput]
  val impactAnalysisOutput: Schema[ImpactAnalysisOutput]             = Schema.derived[ImpactAnalysisOutput]
}
