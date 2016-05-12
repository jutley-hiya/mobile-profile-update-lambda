package com.hiya.mobileprofile

import scala.util.Try

class RecoverableMobileProfileRepository(wrapped: MobileProfileRepository,
                                         recoveryStrategy: PartialFunction[Throwable, Unit])
  extends MobileProfileRepository
{
  override def save(profile: MobileProfile): Unit = {
    Try(wrapped.save(profile)).recover(recoveryStrategy)
  }
}