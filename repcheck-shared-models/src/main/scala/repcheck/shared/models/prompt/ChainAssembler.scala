package repcheck.shared.models.prompt

trait ChainAssembler {

  def assemble(
      profile: PromptProfile,
      blocks: Map[String, InstructionBlock],
      context: Map[String, String]
  ): Either[String, String]

}

object DefaultChainAssembler extends ChainAssembler {

  private val placeholderPattern = """\{\{(\w+)\}\}""".r

  def assemble(
      profile: PromptProfile,
      blocks: Map[String, InstructionBlock],
      context: Map[String, String]
  ): Either[String, String] = {
    val sortedStages = profile.chain.sortBy(_.stage.stageOrder)

    val allBlockNames = sortedStages.flatMap(_.blockNames)
    val missingNames  = allBlockNames.filterNot(blocks.contains)

    if (missingNames.nonEmpty) {
      Left(s"Missing blocks: ${missingNames.mkString(", ")}")
    } else {
      val stageSections = sortedStages.map { stageConfig =>
        val stageBlocks = stageConfig.blockNames.flatMap(blocks.get)
        val rendered = stageBlocks.map { block =>
          val content = if (block.stage == PromptStage.Context) {
            replacePlaceholders(block.content, context)
          } else {
            block.content
          }
          WeightTranslator.translate(block.weight, content)
        }
        rendered.mkString("\n")
      }
      Right(stageSections.mkString("\n\n"))
    }
  }

  private def replacePlaceholders(content: String, context: Map[String, String]): String = {
    placeholderPattern.replaceAllIn(content, m => {
      val key = m.group(1)
      java.util.regex.Matcher.quoteReplacement(context.getOrElse(key, m.matched))
    })
  }

}
