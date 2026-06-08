package repcheck.shared.models.llm.agentic

import scala.concurrent.duration.{DurationInt, FiniteDuration}

/**
 * Bounds the agentic tool-use loop: the most iterations to run, the per-model-call timeout, and an optional token
 * budget. Runtime config (not persisted), so it carries no codec.
 */
final case class LoopPolicy(maxIterations: Int, perCallTimeout: FiniteDuration, tokenBudget: Option[Int])

object LoopPolicy {
  val default: LoopPolicy = LoopPolicy(maxIterations = 8, perCallTimeout = 60.seconds, tokenBudget = None)
}
