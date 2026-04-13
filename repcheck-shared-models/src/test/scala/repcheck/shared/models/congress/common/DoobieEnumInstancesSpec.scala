package repcheck.shared.models.congress.common

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class DoobieEnumInstancesSpec extends AnyFlatSpec with Matchers {

  "DoobieEnumInstances" should "provide Get[Party] instance" in {
    DoobieEnumInstances.partyGet.toString should not be empty
  }

  it should "provide Put[Party] instance" in {
    DoobieEnumInstances.partyPut.toString should not be empty
  }

  it should "provide Get[Chamber] instance" in {
    DoobieEnumInstances.chamberGet.toString should not be empty
  }

  it should "provide Put[Chamber] instance" in {
    DoobieEnumInstances.chamberPut.toString should not be empty
  }

  it should "provide Get[UsState] instance" in {
    DoobieEnumInstances.usStateGet.toString should not be empty
  }

  it should "provide Put[UsState] instance" in {
    DoobieEnumInstances.usStatePut.toString should not be empty
  }

  it should "provide Get[BillType] instance" in {
    DoobieEnumInstances.billTypeGet.toString should not be empty
  }

  it should "provide Put[BillType] instance" in {
    DoobieEnumInstances.billTypePut.toString should not be empty
  }

  it should "provide Get[FormatType] instance" in {
    DoobieEnumInstances.formatTypeGet.toString should not be empty
  }

  it should "provide Put[FormatType] instance" in {
    DoobieEnumInstances.formatTypePut.toString should not be empty
  }

  it should "provide Get[VoteMethod] instance" in {
    DoobieEnumInstances.voteMethodGet.toString should not be empty
  }

  it should "provide Put[VoteMethod] instance" in {
    DoobieEnumInstances.voteMethodPut.toString should not be empty
  }

  it should "provide Get[MemberType] instance" in {
    DoobieEnumInstances.memberTypeGet.toString should not be empty
  }

  it should "provide Put[MemberType] instance" in {
    DoobieEnumInstances.memberTypePut.toString should not be empty
  }

  it should "provide Get[TextVersionCode] instance" in {
    DoobieEnumInstances.textVersionCodeGet.toString should not be empty
  }

  it should "provide Put[TextVersionCode] instance" in {
    DoobieEnumInstances.textVersionCodePut.toString should not be empty
  }

  it should "provide Get[VoteCast] instance" in {
    DoobieEnumInstances.voteCastGet.toString should not be empty
  }

  it should "provide Put[VoteCast] instance" in {
    DoobieEnumInstances.voteCastPut.toString should not be empty
  }

  it should "provide Get[AmendmentType] instance" in {
    DoobieEnumInstances.amendmentTypeGet.toString should not be empty
  }

  it should "provide Put[AmendmentType] instance" in {
    DoobieEnumInstances.amendmentTypePut.toString should not be empty
  }

  it should "provide Get[CommitteeType] instance" in {
    DoobieEnumInstances.committeeTypeGet.toString should not be empty
  }

  it should "provide Put[CommitteeType] instance" in {
    DoobieEnumInstances.committeeTypePut.toString should not be empty
  }

  it should "provide Get[CommitteePosition] instance" in {
    DoobieEnumInstances.committeePositionGet.toString should not be empty
  }

  it should "provide Put[CommitteePosition] instance" in {
    DoobieEnumInstances.committeePositionPut.toString should not be empty
  }

  it should "provide Get[CommitteeSide] instance" in {
    DoobieEnumInstances.committeeSideGet.toString should not be empty
  }

  it should "provide Put[CommitteeSide] instance" in {
    DoobieEnumInstances.committeeSidePut.toString should not be empty
  }

}
