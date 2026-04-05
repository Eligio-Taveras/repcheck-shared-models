package repcheck.shared.models.congress.common

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class BillTypeSpec extends AnyFlatSpec with Matchers {

  "BillType.fromString" should "parse all 13 canonical values" in {
    BillType.fromString("HR") shouldBe Right(BillType.HR)
    BillType.fromString("S") shouldBe Right(BillType.S)
    BillType.fromString("HJRES") shouldBe Right(BillType.HJRES)
    BillType.fromString("SJRES") shouldBe Right(BillType.SJRES)
    BillType.fromString("HCONRES") shouldBe Right(BillType.HCONRES)
    BillType.fromString("SCONRES") shouldBe Right(BillType.SCONRES)
    BillType.fromString("HRES") shouldBe Right(BillType.HRES)
    BillType.fromString("SRES") shouldBe Right(BillType.SRES)
    BillType.fromString("PL") shouldBe Right(BillType.PL)
    BillType.fromString("STAT") shouldBe Right(BillType.STAT)
    BillType.fromString("USC") shouldBe Right(BillType.USC)
    BillType.fromString("SRPT") shouldBe Right(BillType.SRPT)
    BillType.fromString("HRPT") shouldBe Right(BillType.HRPT)
  }

  it should "be case-insensitive" in {
    BillType.fromString("hr") shouldBe Right(BillType.HR)
    BillType.fromString("Hr") shouldBe Right(BillType.HR)
    BillType.fromString("hJrEs") shouldBe Right(BillType.HJRES)
  }

  it should "accept apiValue aliases" in {
    BillType.fromString("hjres") shouldBe Right(BillType.HJRES)
    BillType.fromString("sconres") shouldBe Right(BillType.SCONRES)
  }

  it should "return Left for unknown values" in {
    val result = BillType.fromString("UNKNOWN")
    result.isLeft shouldBe true
    result.left.map(_.getMessage) should matchPattern {
      case Left(msg: String) if msg.contains("UNKNOWN") =>
    }
  }

  "BillType Circe codec" should "round-trip all values" in {
    BillType.values.foreach { bt =>
      val json    = bt.asJson
      val decoded = json.as[BillType]
      decoded shouldBe Right(bt)
    }
  }

  it should "decode from JSON string" in {
    decode[BillType](""""hr"""") shouldBe Right(BillType.HR)
    decode[BillType](""""HR"""") shouldBe Right(BillType.HR)
  }

  it should "encode to apiValue (lowercase)" in {
    BillType.HR.asJson.noSpaces shouldBe """"hr""""
    BillType.HJRES.asJson.noSpaces shouldBe """"hjres""""
    BillType.SCONRES.asJson.noSpaces shouldBe """"sconres""""
  }

}
