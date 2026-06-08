package repcheck.shared.models.llm.output

import repcheck.shared.models.llm.codec.StructuredCodecLaws

class TaxonomyOutputCodecSpec extends StructuredCodecLaws[TaxonomyOutput]("TaxonomyOutput")

class ClusterConceptOutputCodecSpec extends StructuredCodecLaws[ClusterConceptOutput]("ClusterConceptOutput")
