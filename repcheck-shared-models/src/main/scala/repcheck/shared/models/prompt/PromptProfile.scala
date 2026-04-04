package repcheck.shared.models.prompt

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

final case class PromptProfile(
  name: String,
  chain: List[StageConfig],
)

object PromptProfile {

  implicit val encoder: Encoder[PromptProfile] = deriveEncoder[PromptProfile]
  implicit val decoder: Decoder[PromptProfile] = deriveDecoder[PromptProfile]

}
