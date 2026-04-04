package repcheck.shared.models.constants

object CongressGovApiPaths {

  val BaseUrl: String = "https://api.congress.gov"

  val BillPath: String       = "/v3/bill"
  val MemberPath: String     = "/v3/member"
  val HouseVotePath: String  = "/v3/house-vote"
  val SenateVotePath: String = "/v3/senate-vote"
  val AmendmentPath: String  = "/v3/amendment"
  val CommitteePath: String  = "/v3/committee"

  def billUrl(congress: Int, billType: String, billNumber: Int): String =
    s"$BaseUrl$BillPath/$congress/$billType/$billNumber"

  def memberUrl(bioguideId: String): String =
    s"$BaseUrl$MemberPath/$bioguideId"

  def amendmentUrl(congress: Int, amendmentType: String, amendmentNumber: Int): String =
    s"$BaseUrl$AmendmentPath/$congress/$amendmentType/$amendmentNumber"

  def committeeUrl(chamber: String, committeeCode: String): String =
    s"$BaseUrl$CommitteePath/$chamber/$committeeCode"

}
