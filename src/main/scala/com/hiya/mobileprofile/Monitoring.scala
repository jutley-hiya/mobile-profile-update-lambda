package com.hiya.mobileprofile

trait Monitoring {
  def writeMetric(metric: String, value: Double): Unit
}
