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

  "DoobieArrayCodecs.intArrayToList" should "convert int array to list preserving order" in {
    DoobieArrayCodecs.intArrayToList(Array(1, 2, 3)) shouldBe List(1, 2, 3)
  }

  it should "handle empty int array" in {
    DoobieArrayCodecs.intArrayToList(Array.empty[Int]) shouldBe List.empty[Int]
  }

  "DoobieArrayCodecs.intListToArray" should "convert int list to array preserving order" in {
    DoobieArrayCodecs.intListToArray(List(1, 2, 3)).toSeq shouldBe Seq(1, 2, 3)
  }

  it should "handle empty int list" in {
    DoobieArrayCodecs.intListToArray(List.empty[Int]) shouldBe empty
  }

  "intArrayToList and intListToArray" should "form inverse pair" in {
    val original = List(4, 5, 6)
    DoobieArrayCodecs.intArrayToList(DoobieArrayCodecs.intListToArray(original)) shouldBe original
  }

}
