package repcheck.shared.models.llm.codec

import io.circe.Json

/**
 * What the model is told about the expected output: the JSON Schema ("tell it") plus a codec-encoded canonical example
 * ("show it"). Both come from the type's [[StructuredCodec]], so they can never drift from each other.
 */
final case class StructuredSchema[A](jsonSchema: Json, example: Json)

object StructuredSchema {

  def from[A](using sc: StructuredCodec[A]): StructuredSchema[A] =
    StructuredSchema(sc.jsonSchema, sc.exampleJson)

}
