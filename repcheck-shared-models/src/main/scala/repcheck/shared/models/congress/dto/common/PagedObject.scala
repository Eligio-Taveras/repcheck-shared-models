package repcheck.shared.models.congress.dto.common

trait PagedObject[T] {
  def items: List[T]
  def pagination: Option[PaginationInfoDTO]
}
