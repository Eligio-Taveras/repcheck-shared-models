package repcheck.shared.models.congress.amendment

import io.circe.parser.{decode, parse}
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repcheck.shared.models.congress.common.BillType

class LegislationRefSpec extends AnyFlatSpec with Matchers {

  "LegislationRef Bill" should "round-trip through Circe" in {
    val ref: LegislationRef = LegislationRef.Bill(BillType.HR)
    val encoded             = ref.asJson.noSpaces
    val _                   = decode[LegislationRef](encoded) shouldBe Right(ref)
    parse(encoded).flatMap(_.as[LegislationRef]) shouldBe Right(ref)
  }

  it should "encode each BillType case via its apiValue" in {
    val ref: LegislationRef = LegislationRef.Bill(BillType.SJRES)
    ref.asJson.noSpaces.shouldBe("""{"kind":"bill","value":"sjres"}""")
  }

  "LegislationRef Amendment" should "round-trip through Circe" in {
    val ref: LegislationRef = LegislationRef.Amendment(AmendmentType.SAMDT)
    val encoded             = ref.asJson.noSpaces
    decode[LegislationRef](encoded) shouldBe Right(ref)
  }

  it should "encode each AmendmentType case" in {
    val ref: LegislationRef = LegislationRef.Amendment(AmendmentType.HAMDT)
    ref.asJson.noSpaces.shouldBe("""{"kind":"amendment","value":"HAMDT"}""")
  }

  "LegislationRef decoder" should "fail on unknown discriminator" in {
    val result = decode[LegislationRef]("""{"kind":"treaty","value":"hr"}""")
    val _      = result.isLeft shouldBe true
    result.left.map(_.getMessage.contains("treaty")) shouldBe Left(true)
  }

  it should "fail when kind is missing" in {
    decode[LegislationRef]("""{"value":"hr"}""").isLeft shouldBe true
  }

  it should "fail when value doesn't match the kind" in {
    val _ = decode[LegislationRef]("""{"kind":"bill","value":"NOTABILL"}""").isLeft shouldBe true
    decode[LegislationRef]("""{"kind":"amendment","value":"NOTANAMD"}""").isLeft shouldBe true
  }

  "LegislationRef pattern matching" should "discriminate Bill vs Amendment" in {
    val refs: List[LegislationRef] = List(
      LegislationRef.Bill(BillType.HR),
      LegislationRef.Amendment(AmendmentType.SAMDT),
    )
    val tagged = refs.map {
      case LegislationRef.Bill(bt)      => s"bill:${bt.apiValue}"
      case LegislationRef.Amendment(at) => s"amd:${at.apiValue}"
    }
    tagged shouldBe List("bill:hr", "amd:samdt")
  }

}
