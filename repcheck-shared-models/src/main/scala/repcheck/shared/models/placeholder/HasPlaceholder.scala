package repcheck.shared.models.placeholder

/**
 * Type class for entity types that can be created as placeholders.
 *
 * When a pipeline encounters a cross-entity reference to an entity not yet ingested (e.g., a bill references a sponsor
 * whose bioguide_id is not in the members table), it creates a placeholder row with only the natural key populated. The
 * owning pipeline fills in the full data later via normal upsert + ChangeDetector diff.
 *
 * @tparam T
 *   the domain object type that can be a placeholder
 */
trait HasPlaceholder[T] {

  /**
   * Creates a minimal valid instance with only the natural key populated. All other fields are set to sensible defaults
   * (None for Options, zero/empty for required primitives).
   *
   * @param naturalKey
   *   the natural key value (e.g., bioguideId, billId)
   * @return
   *   a placeholder instance of T
   */
  def placeholder(naturalKey: String): T
}

object HasPlaceholder {

  def apply[T](implicit ev: HasPlaceholder[T]): HasPlaceholder[T] = ev
}
