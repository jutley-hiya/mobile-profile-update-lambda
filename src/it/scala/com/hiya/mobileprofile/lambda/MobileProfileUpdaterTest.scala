package com.hiya.mobileprofile.lambda

import com.amazonaws.regions.Regions
import com.typesafe.config.{Config, ConfigFactory}

object MobileProfileUpdaterTest extends MobileProfileUpdater {
  override lazy val config: Config = ConfigFactory.load("mobile-profile-update-it")
  override lazy val region: Regions = Regions.US_EAST_1
}
