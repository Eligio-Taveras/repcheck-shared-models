package repcheck.shared.models.congress.dos.errors

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ConversionErrorsSpec extends AnyFlatSpec with Matchers {

  "BillConversionFailed" should "include message in exception text" in {
    val error = BillConversionFailed("missing title")
    error.getMessage should include("Bill conversion failed")
    error.getMessage should include("missing title")
    error shouldBe a[Exception]
  }

  "MemberConversionFailed" should "include message in exception text" in {
    val error = MemberConversionFailed("empty bioguideId")
    error.getMessage should include("Member conversion failed")
    error.getMessage should include("empty bioguideId")
  }

  "VoteConversionFailed" should "include message in exception text" in {
    val error = VoteConversionFailed("invalid chamber")
    error.getMessage should include("Vote conversion failed")
    error.getMessage should include("invalid chamber")
  }

  "AmendmentConversionFailed" should "include message in exception text" in {
    val error = AmendmentConversionFailed("bad congress")
    error.getMessage should include("Amendment conversion failed")
    error.getMessage should include("bad congress")
  }

}
