package com.hiya.mobileprofile

import com.amazonaws.services.cloudwatch.AmazonCloudWatch
import com.amazonaws.services.cloudwatch.model.{MetricDatum, PutMetricDataRequest}
import com.typesafe.config.Config

import scala.collection.JavaConverters._

class CloudWatchMonitoring(cloudWatchClient: AmazonCloudWatch, config: Config) extends Monitoring {
  require(cloudWatchClient != null, "should not get null client")
  require(config != null, "should not get null config")

  val namespace = config.getString("namespace")

  override def writeMetric(metric: String, value: Double): Unit = {
    require(metric != null && metric.trim.nonEmpty, "should not get null or empty")
    val dataPoint = new MetricDatum()
      .withValue(value)
      .withMetricName(metric)
    val request = new PutMetricDataRequest()
      .withNamespace(namespace)
      .withMetricData(Seq[MetricDatum](dataPoint).asJava)
    cloudWatchClient.putMetricData(request)
  }
}
