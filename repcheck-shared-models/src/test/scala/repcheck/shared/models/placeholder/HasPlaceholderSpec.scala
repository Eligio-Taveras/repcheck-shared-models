package repcheck.shared.models.placeholder

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
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
    placeholder.firstName shouldBe None
    placeholder.lastName shouldBe None
    placeholder.directOrderName shouldBe None
    placeholder.invertedOrderName shouldBe None
    placeholder.honorificName shouldBe None
    placeholder.birthYear shouldBe None
    placeholder.currentParty shouldBe None
    placeholder.state shouldBe None
    placeholder.district shouldBe None
    placeholder.imageUrl shouldBe None
    placeholder.imageAttribution shouldBe None
    placeholder.officialUrl shouldBe None
    placeholder.updateDate shouldBe None
    placeholder.createdAt shouldBe None
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
    placeholder.congress shouldBe 0
    placeholder.billType shouldBe ""
    placeholder.number shouldBe ""
    placeholder.title shouldBe ""
  }

  it should "have all optional fields set to None" in {
    val placeholder = HasPlaceholder[BillDO].placeholder("hr1234-118")
    placeholder.originChamber shouldBe None
    placeholder.originChamberCode shouldBe None
    placeholder.introducedDate shouldBe None
    placeholder.policyArea shouldBe None
    placeholder.latestActionDate shouldBe None
    placeholder.latestActionText shouldBe None
    placeholder.constitutionalAuthorityText shouldBe None
    placeholder.sponsorMemberId shouldBe None
    placeholder.textUrl shouldBe None
    placeholder.textFormat shouldBe None
    placeholder.textVersionType shouldBe None
    placeholder.textDate shouldBe None
    placeholder.textContent shouldBe None
    placeholder.textEmbedding shouldBe None
    placeholder.summaryText shouldBe None
    placeholder.summaryActionDesc shouldBe None
    placeholder.summaryActionDate shouldBe None
    placeholder.updateDate shouldBe None
    placeholder.updateDateIncludingText shouldBe None
    placeholder.legislationUrl shouldBe None
    placeholder.apiUrl shouldBe None
    placeholder.createdAt shouldBe None
    placeholder.updatedAt shouldBe None
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
    placeholder.congress shouldBe 0
    placeholder.chamber shouldBe ""
    placeholder.rollNumber shouldBe 0
  }

  it should "have all optional fields set to None" in {
    val placeholder = HasPlaceholder[VoteDO].placeholder("118-senate-42")
    placeholder.sessionNumber shouldBe None
    placeholder.billId shouldBe None
    placeholder.question shouldBe None
    placeholder.voteType shouldBe None
    placeholder.voteMethod shouldBe None
    placeholder.result shouldBe None
    placeholder.voteDate shouldBe None
    placeholder.legislationNumber shouldBe None
    placeholder.legislationType shouldBe None
    placeholder.legislationUrl shouldBe None
    placeholder.sourceDataUrl shouldBe None
    placeholder.updateDate shouldBe None
    placeholder.createdAt shouldBe None
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
    placeholder.congress shouldBe 0
    placeholder.number shouldBe ""
  }

  it should "have all optional fields set to None" in {
    val placeholder = HasPlaceholder[AmendmentDO].placeholder("hamdt-500-118")
    placeholder.amendmentType shouldBe None
    placeholder.billId shouldBe None
    placeholder.chamber shouldBe None
    placeholder.description shouldBe None
    placeholder.purpose shouldBe None
    placeholder.sponsorMemberId shouldBe None
    placeholder.submittedDate shouldBe None
    placeholder.latestActionDate shouldBe None
    placeholder.latestActionText shouldBe None
    placeholder.updateDate shouldBe None
    placeholder.apiUrl shouldBe None
    placeholder.createdAt shouldBe None
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
    placeholder.chamber shouldBe None
    placeholder.committeeType shouldBe None
    placeholder.parentCommitteeId shouldBe None
    placeholder.isCurrent shouldBe None
    placeholder.updateDate shouldBe None
    placeholder.createdAt shouldBe None
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
