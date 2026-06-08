package repcheck.shared.models.congress.dos.results

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repcheck.shared.models.congress.common.{Party, UsState}
import repcheck.shared.models.congress.vote.VoteCast

class ConversionResultsSpec extends AnyFlatSpec with Matchers {

  "UnresolvedVotePosition" should "default voteCastCandidateName to None" in {
    val position = UnresolvedVotePosition(
      memberSource = Left("A000001"),
      voteCast = Some(VoteCast.Yea),
      partyAtVote = Some(Party.Democrat),
      stateAtVote = Some(UsState.California),
    )
    position.voteCastCandidateName shouldBe None
  }

  it should "carry an explicit candidate name when provided" in {
    val position = UnresolvedVotePosition(
      memberSource = Left("A000001"),
      voteCast = Some(VoteCast.Candidate),
      partyAtVote = None,
      stateAtVote = None,
      voteCastCandidateName = Some("Jane Doe"),
    )
    position.voteCastCandidateName shouldBe Some("Jane Doe")
  }

}
