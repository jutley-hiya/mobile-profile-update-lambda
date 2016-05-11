package com.hiya.mobileprofile

import com.amazonaws.services.cloudwatch.AmazonCloudWatch
import com.amazonaws.services.cloudwatch.model.{MetricDatum, PutMetricDataRequest}
import com.typesafe.config.Config

import scala.collection.JavaConverters._

class CloudWatchMonitoring(cloudWatchClient: AmazonCloudWatch, config: Config) extends Monitoring {
  val namespace = config.getString("namespace")

  override def writeMetric(metric: String, value: Double): Unit = {
    val dataPoint = new MetricDatum()
      .withValue(value)
      .withMetricName(metric)
    val request = new PutMetricDataRequest()
      .withNamespace(namespace)
      .withMetricData(Seq[MetricDatum](dataPoint).asJava)
    cloudWatchClient.putMetricData(request)
  }
}
