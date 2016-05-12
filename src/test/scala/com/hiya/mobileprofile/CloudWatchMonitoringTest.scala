package com.hiya.mobileprofile

import com.amazonaws.services.cloudwatch.AmazonCloudWatch
import com.amazonaws.services.cloudwatch.model.{MetricDatum, PutMetricDataRequest}
import com.typesafe.config.Config
import org.scalatest.{ShouldMatchers, FunSpec}
import org.scalamock.scalatest.MockFactory

import scala.collection.JavaConverters._


class CloudWatchMonitoringTest extends FunSpec with ShouldMatchers with MockFactory {

  describe("CloudWatchMonitoring") {
    it("should write a metric to cloudwatch") {
      // Given: A configuration
      val expectedNamespace = "testo"
      val config = mock[Config]
      (config.getString _).expects("namespace").returns(expectedNamespace).once()
      // And: A client to cloudwatch
      val client = mock[AmazonCloudWatch]
      // And a CloudwatchMonitoring
      val monitor : Monitoring = new CloudWatchMonitoring(client, config)
      // And: a metric to log
      val metric = "test"
      val value = 1.0
      val dataPoint = new MetricDatum()
        .withValue(value)
        .withMetricName(metric)
      val expectedRequest = new PutMetricDataRequest()
        .withNamespace(expectedNamespace)
        .withMetricData((Seq[MetricDatum](dataPoint).asJava))
      (client.putMetricData _).expects(expectedRequest).once()
          // When: I write a metric
      monitor.writeMetric(metric, value)
      // Then: My metric is written in my namespace with metricdata in client
    }
  }
}
