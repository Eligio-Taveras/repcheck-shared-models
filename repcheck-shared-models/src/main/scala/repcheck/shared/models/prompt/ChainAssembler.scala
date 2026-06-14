package repcheck.shared.models.prompt

trait ChainAssembler {

  def assemble(
    profile: PromptProfile,
    promptFragments: Map[String, PromptFragment],
    context: Map[String, String],
  ): Either[String, String]

}

object DefaultChainAssembler extends ChainAssembler {

  private val placeholderPattern = """\{\{(\w+)\}\}""".r

  def assemble(
    profile: PromptProfile,
    promptFragments: Map[String, PromptFragment],
    context: Map[String, String],
  ): Either[String, String] = {
    val sortedStages = profile.chain.sortBy(_.stage.stageOrder)

    val allPromptFragmentNames = sortedStages.flatMap(_.promptFragmentNames)
    val missingNames           = allPromptFragmentNames.filterNot(promptFragments.contains)

    if (missingNames.nonEmpty) {
      Left(s"Missing prompt fragments: ${missingNames.mkString(", ")}")
    } else {
      val stageSections = sortedStages.map { stageConfig =>
        val stagePromptFragments = stageConfig.promptFragmentNames.flatMap(promptFragments.get)
        val rendered = stagePromptFragments.map { promptFragment =>
          val content = if (promptFragment.stage == PromptStage.Context) {
            replacePlaceholders(promptFragment.content, context)
          } else {
            promptFragment.content
          }
          WeightTranslator.translate(promptFragment.weight, content)
        }
        rendered.mkString("\n")
      }
      Right(stageSections.mkString("\n\n"))
    }
  }

  private def replacePlaceholders(content: String, context: Map[String, String]): String =
    placeholderPattern.replaceAllIn(
      content,
      m => {
        val key = m.group(1)
        java.util.regex.Matcher.quoteReplacement(context.getOrElse(key, m.matched))
      },
    )

}
