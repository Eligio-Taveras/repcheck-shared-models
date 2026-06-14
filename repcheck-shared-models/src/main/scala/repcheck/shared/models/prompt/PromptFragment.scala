package repcheck.shared.models.prompt

import io.circe.generic.semiauto.deriveEncoder
import io.circe.{Decoder, DecodingFailure, Encoder, HCursor}

/**
 * A reusable, stage-tagged fragment of a prompt: its text (`content`), where it sits (`stage`), and its emphasis
 * (`weight`, 0.0-1.0). Fragments are composed into a full prompt by [[ChainAssembler]] per a [[PromptProfile]] chain.
 */
final case class PromptFragment(
  name: String,
  stage: PromptStage,
  weight: Double,
  version: String,
  content: String,
)

object PromptFragment {

  implicit val encoder: Encoder[PromptFragment] = deriveEncoder[PromptFragment]

  implicit val decoder: Decoder[PromptFragment] = new Decoder[PromptFragment] {
    def apply(c: HCursor): Decoder.Result[PromptFragment] =
      for {
        name   <- c.downField("name").as[String]
        stage  <- c.downField("stage").as[PromptStage]
        weight <- c.downField("weight").as[Double]
        _ <-
          if (weight >= 0.0 && weight <= 1.0) {
            Right(())
          } else {
            Left(DecodingFailure(s"weight must be between 0.0 and 1.0, got: $weight", c.downField("weight").history))
          }
        version <- c.downField("version").as[String]
        content <- c.downField("content").as[String]
      } yield PromptFragment(name, stage, weight, version, content)
  }

}
