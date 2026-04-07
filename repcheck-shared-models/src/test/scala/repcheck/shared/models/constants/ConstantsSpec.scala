package repcheck.shared.models.constants

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ConstantsSpec extends AnyFlatSpec with Matchers {

  "CongressGovApiPaths" should "have correct base URL" in {
    CongressGovApiPaths.BaseUrl shouldBe "https://api.congress.gov"
  }

  it should "produce correct bill URL" in {
    CongressGovApiPaths.billUrl(118, "hr", 1) shouldBe "https://api.congress.gov/v3/bill/118/hr/1"
  }

  it should "produce correct member URL" in {
    CongressGovApiPaths.memberUrl("B000944") shouldBe "https://api.congress.gov/v3/member/B000944"
  }

  it should "produce correct amendment URL" in {
    CongressGovApiPaths.amendmentUrl(118, "samdt", 100) shouldBe
      "https://api.congress.gov/v3/amendment/118/samdt/100"
  }

  it should "produce correct committee URL" in {
    CongressGovApiPaths.committeeUrl("house", "hsag00") shouldBe
      "https://api.congress.gov/v3/committee/house/hsag00"
  }

  "ChamberDataPaths" should "have correct Senate member data XML URL" in {
    val _ = ChamberDataPaths.SenateMemberDataXml should include("senate.gov")
    ChamberDataPaths.SenateMemberDataXml should include("cvc_member_data.xml")
  }

  it should "have correct Senate roll call votes URL" in {
    val _ = ChamberDataPaths.SenateRollCallVotesBase should include("senate.gov")
    ChamberDataPaths.SenateRollCallVotesBase should include("roll_call_votes")
  }

  it should "have correct House member data XML URL" in {
    val _ = ChamberDataPaths.HouseMemberDataXml should include("clerk.house.gov")
    ChamberDataPaths.HouseMemberDataXml should include("memberdata.xml")
  }

  "PaginationDefaults" should "have reasonable page sizes" in {
    val _ = PaginationDefaults.defaultPageSize should be > 0
    PaginationDefaults.maxPageSize should be >= PaginationDefaults.defaultPageSize
  }

  "DateTimeFormats" should "be defined" in {
    DateTimeFormats.OutgoingDateTimeFormat should not be empty.toString
  }

}
