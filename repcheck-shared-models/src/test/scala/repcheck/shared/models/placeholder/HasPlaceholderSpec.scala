package repcheck.shared.models.placeholder

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repcheck.shared.models.congress.common.Chamber
import repcheck.shared.models.congress.dos.amendment.AmendmentDO
import repcheck.shared.models.congress.dos.bill.BillDO
import repcheck.shared.models.congress.dos.committee.CommitteeDO
import repcheck.shared.models.congress.dos.member.MemberDO
import repcheck.shared.models.congress.dos.vote.VoteDO

class HasPlaceholderSpec extends AnyFlatSpec with Matchers {

  "HasPlaceholder companion apply" should "summon the implicit instance" in {
    val instance = HasPlaceholder[MemberDO]
    instance shouldBe a[HasPlaceholder[?]]
  }

  // --- MemberDO ---

  "HasPlaceholder[MemberDO]" should "have PK field set to 0L" in {
    val placeholder = HasPlaceholder[MemberDO].placeholder("B000944")
    placeholder.memberId shouldBe 0L
  }

  it should "set naturalKey to the provided key" in {
    val placeholder = HasPlaceholder[MemberDO].placeholder("B000944")
    placeholder.naturalKey shouldBe "B000944"
  }

  it should "have all optional fields set to None" in {
    val placeholder = HasPlaceholder[MemberDO].placeholder("B000944")
    val _           = placeholder.firstName shouldBe None
    val _           = placeholder.lastName shouldBe None
    val _           = placeholder.directOrderName shouldBe None
    val _           = placeholder.invertedOrderName shouldBe None
    val _           = placeholder.honorificName shouldBe None
    val _           = placeholder.birthYear shouldBe None
    val _           = placeholder.currentParty shouldBe None
    val _           = placeholder.state shouldBe None
    val _           = placeholder.district shouldBe None
    val _           = placeholder.imageUrl shouldBe None
    val _           = placeholder.imageAttribution shouldBe None
    val _           = placeholder.officialUrl shouldBe None
    val _           = placeholder.updateDate shouldBe None
    val _           = placeholder.createdAt shouldBe None
    placeholder.updatedAt shouldBe None
  }

  it should "use the natural key as the naturalKey field" in {
    val key         = "S000148"
    val placeholder = HasPlaceholder[MemberDO].placeholder(key)
    placeholder.naturalKey shouldBe key
  }

  // --- BillDO ---

  "HasPlaceholder[BillDO]" should "have PK field set to 0L" in {
    val placeholder = HasPlaceholder[BillDO].placeholder("hr1234-118")
    placeholder.billId shouldBe 0L
  }

  it should "set naturalKey to the provided key" in {
    val placeholder = HasPlaceholder[BillDO].placeholder("hr1234-118")
    placeholder.naturalKey shouldBe "hr1234-118"
  }

  it should "have zero/empty defaults for required non-Option fields" in {
    val placeholder = HasPlaceholder[BillDO].placeholder("hr1234-118")
    val _           = placeholder.congress shouldBe 0
    val _           = placeholder.billType shouldBe ""
    val _           = placeholder.number shouldBe ""
    placeholder.title shouldBe ""
  }

  it should "have all optional fields set to None" in {
    val placeholder = HasPlaceholder[BillDO].placeholder("hr1234-118")
    val _           = placeholder.originChamber shouldBe None
    val _           = placeholder.originChamberCode shouldBe None
    val _           = placeholder.introducedDate shouldBe None
    val _           = placeholder.policyArea shouldBe None
    val _           = placeholder.latestActionDate shouldBe None
    val _           = placeholder.latestActionText shouldBe None
    val _           = placeholder.constitutionalAuthorityText shouldBe None
    val _           = placeholder.sponsorMemberId shouldBe None
    val _           = placeholder.textUrl shouldBe None
    val _           = placeholder.textFormat shouldBe None
    val _           = placeholder.textVersionType shouldBe None
    val _           = placeholder.textDate shouldBe None
    val _           = placeholder.textContent shouldBe None
    val _           = placeholder.textEmbedding shouldBe None
    val _           = placeholder.summaryText shouldBe None
    val _           = placeholder.summaryActionDesc shouldBe None
    val _           = placeholder.summaryActionDate shouldBe None
    val _           = placeholder.updateDate shouldBe None
    val _           = placeholder.updateDateIncludingText shouldBe None
    val _           = placeholder.legislationUrl shouldBe None
    val _           = placeholder.apiUrl shouldBe None
    val _           = placeholder.createdAt shouldBe None
    val _           = placeholder.updatedAt shouldBe None
    placeholder.latestTextVersionId shouldBe None
  }

  // --- VoteDO ---

  "HasPlaceholder[VoteDO]" should "have PK field set to 0L" in {
    val placeholder = HasPlaceholder[VoteDO].placeholder("118-senate-42")
    placeholder.voteId shouldBe 0L
  }

  it should "set naturalKey to the provided key" in {
    val placeholder = HasPlaceholder[VoteDO].placeholder("118-senate-42")
    placeholder.naturalKey shouldBe "118-senate-42"
  }

  it should "have zero/empty defaults for required non-Option fields" in {
    val placeholder = HasPlaceholder[VoteDO].placeholder("118-senate-42")
    val _           = placeholder.congress shouldBe 0
    val _           = placeholder.chamber shouldBe Chamber.House
    placeholder.rollNumber shouldBe 0
  }

  it should "have all optional fields set to None" in {
    val placeholder = HasPlaceholder[VoteDO].placeholder("118-senate-42")
    val _           = placeholder.sessionNumber shouldBe None
    val _           = placeholder.billId shouldBe None
    val _           = placeholder.question shouldBe None
    val _           = placeholder.voteType shouldBe None
    val _           = placeholder.voteMethod shouldBe None
    val _           = placeholder.result shouldBe None
    val _           = placeholder.voteDate shouldBe None
    val _           = placeholder.legislationNumber shouldBe None
    val _           = placeholder.legislationType shouldBe None
    val _           = placeholder.legislationUrl shouldBe None
    val _           = placeholder.sourceDataUrl shouldBe None
    val _           = placeholder.updateDate shouldBe None
    val _           = placeholder.createdAt shouldBe None
    placeholder.updatedAt shouldBe None
  }

  // --- AmendmentDO ---

  "HasPlaceholder[AmendmentDO]" should "have PK field set to 0L" in {
    val placeholder = HasPlaceholder[AmendmentDO].placeholder("hamdt-500-118")
    placeholder.amendmentId shouldBe 0L
  }

  it should "set naturalKey to the provided key" in {
    val placeholder = HasPlaceholder[AmendmentDO].placeholder("hamdt-500-118")
    placeholder.naturalKey shouldBe "hamdt-500-118"
  }

  it should "have zero/empty defaults for required non-Option fields" in {
    val placeholder = HasPlaceholder[AmendmentDO].placeholder("hamdt-500-118")
    val _           = placeholder.congress shouldBe 0
    placeholder.number shouldBe ""
  }

  it should "have all optional fields set to None" in {
    val placeholder = HasPlaceholder[AmendmentDO].placeholder("hamdt-500-118")
    val _           = placeholder.amendmentType shouldBe None
    val _           = placeholder.billId shouldBe None
    val _           = placeholder.chamber shouldBe None
    val _           = placeholder.description shouldBe None
    val _           = placeholder.purpose shouldBe None
    val _           = placeholder.sponsorMemberId shouldBe None
    val _           = placeholder.submittedDate shouldBe None
    val _           = placeholder.latestActionDate shouldBe None
    val _           = placeholder.latestActionText shouldBe None
    val _           = placeholder.updateDate shouldBe None
    val _           = placeholder.apiUrl shouldBe None
    val _           = placeholder.createdAt shouldBe None
    placeholder.updatedAt shouldBe None
  }

  // --- CommitteeDO ---

  "HasPlaceholder[CommitteeDO]" should "have PK field set to 0L" in {
    val placeholder = HasPlaceholder[CommitteeDO].placeholder("SSFI")
    placeholder.committeeId shouldBe 0L
  }

  it should "set naturalKey to the provided key" in {
    val placeholder = HasPlaceholder[CommitteeDO].placeholder("SSFI")
    placeholder.naturalKey shouldBe "SSFI"
  }

  it should "have empty string for required name field" in {
    val placeholder = HasPlaceholder[CommitteeDO].placeholder("SSFI")
    placeholder.name shouldBe ""
  }

  it should "have all optional fields set to None" in {
    val placeholder = HasPlaceholder[CommitteeDO].placeholder("SSFI")
    val _           = placeholder.chamber shouldBe None
    val _           = placeholder.committeeType shouldBe None
    val _           = placeholder.parentCommitteeId shouldBe None
    val _           = placeholder.isCurrent shouldBe None
    val _           = placeholder.updateDate shouldBe None
    val _           = placeholder.createdAt shouldBe None
    placeholder.updatedAt shouldBe None
  }

  // --- Generic behavior ---

  "HasPlaceholder" should "produce different placeholders for different natural keys" in {
    val p1 = HasPlaceholder[MemberDO].placeholder("A000001")
    val p2 = HasPlaceholder[MemberDO].placeholder("B000002")
    p1.naturalKey should not be p2.naturalKey
  }

  it should "handle empty string as natural key" in {
    val placeholder = HasPlaceholder[MemberDO].placeholder("")
    placeholder.naturalKey shouldBe ""
  }

  it should "handle special characters in natural key" in {
    val key         = "member-with/special_chars.v2"
    val placeholder = HasPlaceholder[MemberDO].placeholder(key)
    placeholder.naturalKey shouldBe key
  }

  it should "be accessible via implicit resolution" in {
    def createPlaceholder[T](key: String)(implicit hp: HasPlaceholder[T]): T =
      hp.placeholder(key)
    val member = createPlaceholder[MemberDO]("X000001")
    member.naturalKey shouldBe "X000001"
  }

}
