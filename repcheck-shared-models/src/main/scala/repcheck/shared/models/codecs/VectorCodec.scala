package repcheck.shared.models.codecs

import io.circe.{Decoder, Encoder, Json}

import doobie.{Get, Put}

object VectorCodec {

  implicit val floatArrayEncoder: Encoder[Array[Float]] =
    Encoder.instance(arr => Json.arr(arr.map(f => Json.fromFloatOrNull(f)).toIndexedSeq*))

  implicit val floatArrayDecoder: Decoder[Array[Float]] =
    Decoder.decodeArray[Float].map(_.toArray)

  implicit val floatArrayGet: Get[Array[Float]] =
    Get[String].map { pgVector =>
      val trimmed = pgVector.stripPrefix("[").stripSuffix("]")
      if (trimmed.isEmpty) {
        Array.empty[Float]
      } else {
        trimmed.split(",").map(_.trim.toFloat)
      }
    }

  implicit val floatArrayPut: Put[Array[Float]] =
    Put[String].contramap(arr => arr.mkString("[", ",", "]"))

}
