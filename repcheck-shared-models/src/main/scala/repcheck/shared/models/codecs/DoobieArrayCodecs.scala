package repcheck.shared.models.codecs

import doobie.postgres.implicits._
import doobie.{Get, Put}

object DoobieArrayCodecs {

  private[codecs] val arrayToList: Array[String] => List[String] = _.toList

  private[codecs] val listToArray: List[String] => Array[String] = _.toArray

  implicit val listStringGet: Get[List[String]] =
    Get[Array[String]].map(arrayToList)

  implicit val listStringPut: Put[List[String]] =
    Put[Array[String]].contramap(listToArray)

  private[codecs] val intArrayToList: Array[Int] => List[Int] = _.toList

  private[codecs] val intListToArray: List[Int] => Array[Int] = _.toArray

  implicit val listIntGet: Get[List[Int]] =
    Get[Array[Int]].map(intArrayToList)

  implicit val listIntPut: Put[List[Int]] =
    Put[Array[Int]].contramap(intListToArray)

}
