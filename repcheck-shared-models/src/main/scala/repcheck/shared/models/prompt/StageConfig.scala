package repcheck.shared.models.prompt

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

final case class StageConfig(
    stage: PromptStage,
    blockNames: List[String],
    weight: Double
)

object StageConfig {

  implicit val encoder: Encoder[StageConfig] = deriveEncoder[StageConfig]
  implicit val decoder: Decoder[StageConfig] = deriveDecoder[StageConfig]

}
