package repcheck.shared.models.congress.dos.bill

import io.circe.parser.decode
import io.circe.syntax._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class BillSubjectDOSpec extends AnyFlatSpec with Matchers {

  private val sampleSubject = BillSubjectDO(
    billId = 1L,
    subjectName = "Health Care",
    embedding = None,
    updateDate = Some("2024-03-01T12:00:00Z"),
  )

  "BillSubjectDO Circe codec" should "round-trip with all fields populated" in {
    val json    = sampleSubject.asJson
    val decoded = json.as[BillSubjectDO]
    decoded shouldBe Right(sampleSubject)
  }

  it should "round-trip with optional fields as None" in {
    val minimal = BillSubjectDO(
      billId = 2L,
      subjectName = "Education",
      embedding = None,
      updateDate = None,
    )
    minimal.asJson.as[BillSubjectDO] shouldBe Right(minimal)
  }

  it should "fail on missing required field" in {
    decode[BillSubjectDO]("""{"billId":1}""").isLeft shouldBe true
  }

  it should "have Doobie Read instance" in {
    import doobie._
    import doobie.implicits._
    import repcheck.shared.models.codecs.VectorCodec.floatArrayGet
    implicitly[Read[BillSubjectDO]].shouldBe(a[AnyRef])
  }

  it should "have Doobie Write instance" in {
    import doobie._
    import doobie.implicits._
    import repcheck.shared.models.codecs.VectorCodec.floatArrayPut
    implicitly[Write[BillSubjectDO]].shouldBe(a[AnyRef])
  }

  it should "accumulate decode errors" in {
    import io.circe.Decoder
    val bad    = io.circe.parser.parse("{}").getOrElse(io.circe.Json.Null)
    val result = Decoder[BillSubjectDO].decodeAccumulating(bad.hcursor)
    result.isInvalid should be(true)
  }

}
