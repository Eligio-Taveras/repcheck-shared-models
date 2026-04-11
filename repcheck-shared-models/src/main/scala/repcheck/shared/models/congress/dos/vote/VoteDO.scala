package repcheck.shared.models.congress.dos.vote

import java.time.{Instant, LocalDate}

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import repcheck.shared.models.congress.common.{BillType, Chamber}
import repcheck.shared.models.congress.vote.VoteMethod
import repcheck.shared.models.placeholder.HasPlaceholder

final case class VoteDO(
  voteId: Long,
  naturalKey: String,
  congress: Int,
  chamber: Chamber,
  rollNumber: Int,
  sessionNumber: Option[Int],
  billId: Option[Long],
  question: Option[String],
  voteType: Option[String],
  voteMethod: Option[VoteMethod],
  result: Option[String],
  voteDate: Option[LocalDate],
  legislationNumber: Option[String],
  legislationType: Option[BillType],
  legislationUrl: Option[String],
  sourceDataUrl: Option[String],
  updateDate: Option[Instant],
  createdAt: Option[Instant],
  updatedAt: Option[Instant],
)

object VoteDO {

  import repcheck.shared.models.codecs.DateTimeCodecs.{localDateDecoder, localDateEncoder}

  implicit val encoder: Encoder[VoteDO] = deriveEncoder[VoteDO]
  implicit val decoder: Decoder[VoteDO] = deriveDecoder[VoteDO]

  implicit val hasPlaceholder: HasPlaceholder[VoteDO] = new HasPlaceholder[VoteDO] {
    def placeholder(naturalKey: String): VoteDO =
      VoteDO(
        voteId = 0L,
        naturalKey = naturalKey,
        congress = 0,
        chamber = Chamber.House,
        rollNumber = 0,
        sessionNumber = None,
        billId = None,
        question = None,
        voteType = None,
        voteMethod = None,
        result = None,
        voteDate = None,
        legislationNumber = None,
        legislationType = None,
        legislationUrl = None,
        sourceDataUrl = None,
        updateDate = None,
        createdAt = None,
        updatedAt = None,
      )
  }

}
