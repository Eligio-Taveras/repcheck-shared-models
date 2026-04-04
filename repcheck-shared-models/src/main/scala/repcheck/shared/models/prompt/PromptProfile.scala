package repcheck.shared.models.prompt

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

final case class PromptProfile(
    name: String,
    chain: List[StageConfig]
)

object PromptProfile {

  implicit val encoder: Encoder[PromptProfile] = deriveEncoder[PromptProfile]
  implicit val decoder: Decoder[PromptProfile] = deriveDecoder[PromptProfile]

}
