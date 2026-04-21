package repcheck.shared.models.congress.common

import cats.effect.IO
import cats.effect.unsafe.implicits.global

import doobie._
import doobie.implicits._

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import repcheck.shared.models.congress.amendment.AmendmentType
import repcheck.shared.models.congress.bill.TextVersionCode
import repcheck.shared.models.congress.committee.{CommitteePosition, CommitteeSide, CommitteeType}
import repcheck.shared.models.congress.member.MemberType
import repcheck.shared.models.congress.vote.{VoteCast, VoteMethod, VoteType}

class DoobieEnumInstancesSpec extends AnyFlatSpec with Matchers {

  import DoobieEnumInstances._

  private val xa: Transactor[IO] = Transactor.fromDriverManager[IO](
    driver = "org.h2.Driver",
    url = "jdbc:h2:mem:doobie_enum_test;DB_CLOSE_DELAY=-1",
    user = "sa",
    password = "",
    logHandler = None,
  )

  // ---------------------------------------------------------------------------
  // PostgreSQL enum-backed types — round-trip via H2
  // ---------------------------------------------------------------------------

  "DoobieEnumInstances" should "round-trip Party via H2" in {
    Party.values.foreach { v =>
      val result = sql"SELECT ${v.apiValue}".query[Party].unique.transact(xa).unsafeRunSync()
      result shouldBe v
    }
  }

  it should "round-trip Chamber via H2" in {
    Chamber.values.foreach { v =>
      val result = sql"SELECT ${v.apiValue}".query[Chamber].unique.transact(xa).unsafeRunSync()
      result shouldBe v
    }
  }

  it should "round-trip BillType via H2" in {
    BillType.values.foreach { v =>
      val result = sql"SELECT ${v.apiValue}".query[BillType].unique.transact(xa).unsafeRunSync()
      result shouldBe v
    }
  }

  it should "round-trip FormatType via H2" in {
    FormatType.values.foreach { v =>
      val result = sql"SELECT ${v.text}".query[FormatType].unique.transact(xa).unsafeRunSync()
      result shouldBe v
    }
  }

  it should "round-trip VoteCast via H2" in {
    VoteCast.values.foreach { v =>
      val result = sql"SELECT ${v.apiValue}".query[VoteCast].unique.transact(xa).unsafeRunSync()
      result shouldBe v
    }
  }

  it should "round-trip AmendmentType via H2" in {
    AmendmentType.values.foreach { v =>
      val result = sql"SELECT ${v.apiValue}".query[AmendmentType].unique.transact(xa).unsafeRunSync()
      result shouldBe v
    }
  }

  it should "round-trip CommitteeType via H2" in {
    CommitteeType.values.foreach { v =>
      val result = sql"SELECT ${v.apiValue}".query[CommitteeType].unique.transact(xa).unsafeRunSync()
      result shouldBe v
    }
  }

  it should "round-trip CommitteePosition via H2" in {
    CommitteePosition.values.foreach { v =>
      val result = sql"SELECT ${v.apiValue}".query[CommitteePosition].unique.transact(xa).unsafeRunSync()
      result shouldBe v
    }
  }

  it should "round-trip VoteMethod via H2" in {
    VoteMethod.values.foreach { v =>
      val result = sql"SELECT ${v.apiValue}".query[VoteMethod].unique.transact(xa).unsafeRunSync()
      result shouldBe v
    }
  }

  it should "round-trip VoteType via H2" in {
    VoteType.values.foreach { v =>
      val result = sql"SELECT ${v.apiValue}".query[VoteType].unique.transact(xa).unsafeRunSync()
      result shouldBe v
    }
  }

  it should "round-trip MemberType via H2" in {
    MemberType.values.foreach { v =>
      val result = sql"SELECT ${v.apiValue}".query[MemberType].unique.transact(xa).unsafeRunSync()
      result shouldBe v
    }
  }

  it should "round-trip TextVersionCode via H2" in {
    TextVersionCode.values.foreach { v =>
      val result = sql"SELECT ${v.toString}".query[TextVersionCode].unique.transact(xa).unsafeRunSync()
      result shouldBe v
    }
  }

  // ---------------------------------------------------------------------------
  // Plain TEXT/VARCHAR-backed types — round-trip via H2
  // ---------------------------------------------------------------------------

  it should "round-trip UsState via H2" in {
    UsState.values.foreach { v =>
      val result = sql"SELECT ${v.code}".query[UsState].unique.transact(xa).unsafeRunSync()
      result shouldBe v
    }
  }

  it should "round-trip CommitteeSide via H2" in {
    CommitteeSide.values.foreach { v =>
      val result = sql"SELECT ${v.apiValue}".query[CommitteeSide].unique.transact(xa).unsafeRunSync()
      result shouldBe v
    }
  }

}
