package com.hiya.mobileprofile

trait MobileProfileRepository {
  def save(profile: MobileProfile): Unit
}
