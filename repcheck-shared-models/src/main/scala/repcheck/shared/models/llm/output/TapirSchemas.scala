package repcheck.shared.models.llm.output

import sttp.tapir.{Schema, Validator}

/**
 * Tapir-derived JSON schemas for the LLM output types, isolated here so this is the ONLY thing excluded from coverage
 * (build.sbt `coverageExcludedFiles`). `Schema.derived` is a macro whose expansion can't be exercised by tests; the
 * case classes, codecs, examples and generators stay in their own coverable files, and the §10c #5b law still verifies
 * these schemas accept the encoders' output.
 */
private[output] object TapirSchemas {
  given proposedNode: Schema[ProposedNode]               = Schema.derived[ProposedNode]
  val taxonomyOutput: Schema[TaxonomyOutput]             = Schema.derived[TaxonomyOutput]
  val clusterConceptOutput: Schema[ClusterConceptOutput] = Schema.derived[ClusterConceptOutput]

  // Enum-field schemas (the `enum` constraints) come from each enum's LlmEnumCompanion.
  private given conceptTopic: Schema[ConceptTopic] = Schema.derived[ConceptTopic]

  val conceptSummaryWithTopics: Schema[ConceptSummaryWithTopics] =
    Schema.derived[ConceptSummaryWithTopics].modify(_.topics)(_.validate(Validator.minSize(1)))

}
