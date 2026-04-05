package repcheck.shared.models.user.qa

import java.time.Instant
import java.util.UUID

import io.circe.parser.decodeAccumulating
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class QaDOsSpec extends AnyFlatSpec with Matchers {

  private val responseId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000")
  private val uid        = UUID.fromString("660e8400-e29b-41d4-a716-446655440000")
  private val instant    = Instant.parse("2024-06-01T14:00:00Z")

  // --- QaQuestionDO ---

  "QaQuestionDO Circe codec" should "round-trip with all fields" in {
    val q = QaQuestionDO(
      questionId = "q-healthcare-001",
      questionText = "How important is universal healthcare to you?",
      category = "healthcare",
      displayOrder = 1,
      allowCustom = true,
      active = true,
      createdAt = Some(instant),
    )
    q.asJson.as[QaQuestionDO] shouldBe Right(q)
  }

  it should "round-trip with None fields" in {
    val q = QaQuestionDO("q-001", "Question?", "general", 1, false, true, None)
    q.asJson.as[QaQuestionDO] shouldBe Right(q)
  }

  it should "decodeAccumulating valid JSON" in {
    val json =
      """{"questionId":"q-1","questionText":"Q?","category":"c","displayOrder":1,"allowCustom":false,"active":true}"""
    decodeAccumulating[QaQuestionDO](json).isValid shouldBe true
  }

  it should "decodeAccumulating invalid field types" in {
    val json =
      """{"questionId":123,"questionText":456,"category":789,"displayOrder":"bad","allowCustom":"bad","active":"bad"}"""
    decodeAccumulating[QaQuestionDO](json).isInvalid should be(true)
  }

  "QaQuestionDO" should "have Doobie Read instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._ // Instant requires JavaTimeInstantMeta
    implicitly[Read[QaQuestionDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._ // Instant requires JavaTimeInstantMeta
    implicitly[Write[QaQuestionDO]].shouldBe(a[AnyRef])
  }

  // --- QaQuestionTopicDO ---

  "QaQuestionTopicDO Circe codec" should "round-trip" in {
    val qt = QaQuestionTopicDO("q-001", "healthcare", "Progressive", 0.8f)
    qt.asJson.as[QaQuestionTopicDO] shouldBe Right(qt)
  }

  it should "decodeAccumulating valid JSON" in {
    val json = """{"questionId":"q-1","topic":"t","agreeStance":"Progressive","weight":0.5}"""
    decodeAccumulating[QaQuestionTopicDO](json).isValid shouldBe true
  }

  "QaQuestionTopicDO" should "have Doobie Read instance" in {
    import doobie._
    import doobie.implicits._
    implicitly[Read[QaQuestionTopicDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.implicits._
    implicitly[Write[QaQuestionTopicDO]].shouldBe(a[AnyRef])
  }

  // --- QaAnswerOptionDO ---

  "QaAnswerOptionDO Circe codec" should "round-trip" in {
    val ao = QaAnswerOptionDO(
      questionId = "q-001",
      optionValue = "strongly_agree",
      displayText = "Strongly Agree",
      stanceMultiplier = 1.0f,
      importanceSignal = 5,
      displayOrder = 1,
    )
    ao.asJson.as[QaAnswerOptionDO] shouldBe Right(ao)
  }

  it should "decodeAccumulating valid JSON" in {
    val json =
      """{"questionId":"q-1","optionValue":"v","displayText":"t","stanceMultiplier":1.0,"importanceSignal":5,"displayOrder":1}"""
    decodeAccumulating[QaAnswerOptionDO](json).isValid shouldBe true
  }

  "QaAnswerOptionDO" should "have Doobie Read instance" in {
    import doobie._
    import doobie.implicits._
    implicitly[Read[QaAnswerOptionDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.implicits._
    implicitly[Write[QaAnswerOptionDO]].shouldBe(a[AnyRef])
  }

  // --- QaUserResponseDO ---

  "QaUserResponseDO Circe codec" should "round-trip with all fields" in {
    val resp = QaUserResponseDO(
      responseId = responseId,
      userId = uid,
      questionId = "q-001",
      selectedOption = Some("strongly_agree"),
      customText = Some("I believe in universal healthcare"),
      respondedAt = Some(instant),
    )
    resp.asJson.as[QaUserResponseDO] shouldBe Right(resp)
  }

  it should "round-trip with None fields" in {
    val resp = QaUserResponseDO(responseId, uid, "q-001", None, None, None)
    resp.asJson.as[QaUserResponseDO] shouldBe Right(resp)
  }

  it should "decodeAccumulating valid JSON" in {
    val json = s"""{"responseId":"$responseId","userId":"$uid","questionId":"q-1"}"""
    decodeAccumulating[QaUserResponseDO](json).isValid shouldBe true
  }

  it should "decodeAccumulating invalid field types" in {
    val json = """{"responseId":"bad","userId":"bad","questionId":123}"""
    decodeAccumulating[QaUserResponseDO](json).isInvalid should be(true)
  }

  "QaUserResponseDO" should "have Doobie Read instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._ // Instant requires JavaTimeInstantMeta
    implicitly[Read[QaUserResponseDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.implicits._
    import doobie.postgres.implicits._ // Instant requires JavaTimeInstantMeta
    implicitly[Write[QaUserResponseDO]].shouldBe(a[AnyRef])
  }

}
