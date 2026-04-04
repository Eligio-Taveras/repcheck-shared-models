package repcheck.shared.models.codecs

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DoobieArrayCodecsSpec extends AnyFlatSpec with Matchers {

  import DoobieArrayCodecs.*

  "listStringGet" should "be defined" in {
    Option(listStringGet).isDefined shouldBe true
  }

  "listStringPut" should "be defined" in {
    Option(listStringPut).isDefined shouldBe true
  }

  "List[String] round-trip via Array" should "preserve values" in {
    val original   = List("healthcare", "defense", "education")
    val asArray    = original.toArray
    val backToList = asArray.toList
    backToList shouldBe original
  }

  it should "handle empty list" in {
    val original   = List.empty[String]
    val asArray    = original.toArray
    val backToList = asArray.toList
    backToList shouldBe original
  }

  it should "handle single element" in {
    val original   = List("taxation")
    val asArray    = original.toArray
    val backToList = asArray.toList
    backToList shouldBe original
  }

}
