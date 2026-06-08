package repcheck.shared.models.congress.dos.bill

import java.time.Instant

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

/**
 * A parsed section of a bill text version (decomposition pipeline). One row per clustering unit: `subIndex` is 0 for a
 * section that fits the embedder, 1..n for the overlapping sub-parts of an oversize section (O6). Uniqueness is on
 * `(version_id, section_index, sub_index)`. `embedding` is the raw-section embedding we cluster on (NULL until
 * embedded).
 */
final case class BillTextSectionDO(
  id: Long,
  versionId: Long,
  billId: Long,
  sectionIndex: Int,
  subIndex: Int,
  sectionIdentifier: Option[String],
  heading: Option[String],
  content: String,
  embedding: Option[Array[Float]],
  createdAt: Option[Instant],
  updatedAt: Option[Instant],
)

object BillTextSectionDO {

  import repcheck.shared.models.codecs.VectorCodec.{floatArrayDecoder, floatArrayEncoder}

  implicit val encoder: Encoder[BillTextSectionDO] = deriveEncoder[BillTextSectionDO]
  implicit val decoder: Decoder[BillTextSectionDO] = deriveDecoder[BillTextSectionDO]

}
