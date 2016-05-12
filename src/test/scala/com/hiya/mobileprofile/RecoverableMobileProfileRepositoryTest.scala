package com.hiya.mobileprofile

import org.scalamock.scalatest.MockFactory
import org.scalatest.{ShouldMatchers, FunSpec}

class RecoverableMobileProfileRepositoryTest extends FunSpec with ShouldMatchers with MockFactory {
  describe("RecoverableMobileProfileRepositoryTest") {
    it("should recover errors from a MobileProfileRepository according to a specified strategy") {
      // Given: a MobileProfileRepository
      val repo = mock[MobileProfileRepository]
      // And: A MobileProfile to save
      val profile = MobileProfile("phone", "profile")
      // And: A recovery strategy
      val handleThrowable = mockFunction[Throwable, Unit]
      handleThrowable.expects(*).once
      val recoveryStrat = PartialFunction[Throwable, Unit] {
        case t => handleThrowable(t)
      }
      // And: A RecoverableMobileProfileRepositoryTest
      val recoverableRepo = new RecoverableMobileProfileRepository(repo, recoveryStrat)
      // When: Saving a profile leads to an exception
      // Then: The exception is caught by the recoveryStrategy
      (repo.save _).expects(profile).throwing(new Exception("Catastrophic failure!"))
      recoverableRepo.save(profile)
    }
  }
}
