package com.hiya.mobileprofile.lambda

import com.amazonaws.regions.Regions

object MobileProfileUpdaterRegions {

  lazy val USEast1 = new MobileProfileUpdater {
    override val region: Regions = Regions.US_EAST_1
  }

  lazy val USWest2 = new MobileProfileUpdater {
    override val region: Regions = Regions.US_WEST_2
  }

  lazy val EUCentral1 = new MobileProfileUpdater {
    override val region: Regions = Regions.EU_CENTRAL_1
  }

  lazy val APSoutheast1 = new MobileProfileUpdater {
    override val region: Regions = Regions.AP_SOUTHEAST_1
  }

  lazy val APSoutheast2 = new MobileProfileUpdater {
    override val region: Regions = Regions.AP_SOUTHEAST_2
  }

  lazy val SAEast1 = new MobileProfileUpdater {
    override val region: Regions = Regions.SA_EAST_1
  }

}
