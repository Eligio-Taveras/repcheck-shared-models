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

  "DoobieArrayCodecs.arrayToList" should "convert array to list preserving order" in {
    DoobieArrayCodecs.arrayToList(Array("healthcare", "defense", "education")) shouldBe
      List("healthcare", "defense", "education")
  }

  it should "handle empty array" in {
    DoobieArrayCodecs.arrayToList(Array.empty[String]) shouldBe List.empty[String]
  }

  it should "handle single-element array" in {
    DoobieArrayCodecs.arrayToList(Array("taxation")) shouldBe List("taxation")
  }

  "DoobieArrayCodecs.listToArray" should "convert list to array preserving order" in {
    DoobieArrayCodecs.listToArray(List("healthcare", "defense", "education")).toSeq shouldBe
      Seq("healthcare", "defense", "education")
  }

  it should "handle empty list" in {
    DoobieArrayCodecs.listToArray(List.empty[String]) shouldBe empty
  }

  it should "handle single-element list" in {
    DoobieArrayCodecs.listToArray(List("taxation")).toSeq shouldBe Seq("taxation")
  }

  "arrayToList and listToArray" should "form inverse pair" in {
    val original = List("a", "b", "c")
    DoobieArrayCodecs.arrayToList(DoobieArrayCodecs.listToArray(original)) shouldBe original
  }

}
