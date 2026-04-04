package repcheck.shared.models.codecs

import doobie.{Get, Put}
import doobie.postgres.implicits._

object DoobieArrayCodecs {

  implicit val listStringGet: Get[List[String]] =
    Get[Array[String]].map(_.toList)

  implicit val listStringPut: Put[List[String]] =
    Put[Array[String]].contramap(_.toArray)

}
