package repcheck.shared.models.prompt

import io.circe.{Decoder, DecodingFailure, Encoder, HCursor}
import io.circe.generic.semiauto.deriveEncoder

final case class InstructionBlock(
    name: String,
    stage: PromptStage,
    weight: Double,
    version: String,
    content: String
)

object InstructionBlock {

  implicit val encoder: Encoder[InstructionBlock] = deriveEncoder[InstructionBlock]

  implicit val decoder: Decoder[InstructionBlock] = new Decoder[InstructionBlock] {
    def apply(c: HCursor): Decoder.Result[InstructionBlock] = {
      for {
        name    <- c.downField("name").as[String]
        stage   <- c.downField("stage").as[PromptStage]
        weight  <- c.downField("weight").as[Double]
        _       <- if (weight >= 0.0 && weight <= 1.0) {
                     Right(())
                   } else {
                     Left(DecodingFailure(s"weight must be between 0.0 and 1.0, got: $weight", c.downField("weight").history))
                   }
        version <- c.downField("version").as[String]
        content <- c.downField("content").as[String]
      } yield InstructionBlock(name, stage, weight, version, content)
    }
  }

}
