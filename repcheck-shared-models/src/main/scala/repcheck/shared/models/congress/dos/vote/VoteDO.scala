package repcheck.shared.models.congress.dos.vote

import java.time.Instant

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import repcheck.shared.models.placeholder.HasPlaceholder

final case class VoteDO(
  voteId: Long,
  naturalKey: String,
  congress: Int,
  chamber: String,
  rollNumber: Int,
  sessionNumber: Option[Int],
  billId: Option[Long],
  question: Option[String],
  voteType: Option[String],
  voteMethod: Option[String],
  result: Option[String],
  voteDate: Option[String],
  legislationNumber: Option[String],
  legislationType: Option[String],
  legislationUrl: Option[String],
  sourceDataUrl: Option[String],
  updateDate: Option[String],
  createdAt: Option[Instant],
  updatedAt: Option[Instant],
)

object VoteDO {

  implicit val encoder: Encoder[VoteDO] = deriveEncoder[VoteDO]
  implicit val decoder: Decoder[VoteDO] = deriveDecoder[VoteDO]

  implicit val hasPlaceholder: HasPlaceholder[VoteDO] = new HasPlaceholder[VoteDO] {
    def placeholder(naturalKey: String): VoteDO =
      VoteDO(
        voteId = 0L,
        naturalKey = naturalKey,
        congress = 0,
        chamber = "",
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
