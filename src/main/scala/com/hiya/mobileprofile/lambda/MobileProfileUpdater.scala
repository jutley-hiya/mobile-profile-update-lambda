package com.hiya.mobileprofile.lambda

import com.amazonaws.regions.Regions
import com.amazonaws.services.cloudwatch.{AmazonCloudWatchClient, AmazonCloudWatch}
import com.amazonaws.services.dynamodbv2.{AmazonDynamoDBClient, AmazonDynamoDB}
import com.amazonaws.services.lambda.runtime.events.SNSEvent
import com.typesafe.config.ConfigFactory

import com.hiya.mobileprofile.MobileProfileUpdateAggregateRoot

import scala.collection.JavaConverters._
import scala.util.control.NonFatal


/**
  * Inspired from https://aws.amazon.com/blogs/compute/writing-aws-lambda-functions-in-scala/
  */
class MobileProfileUpdater {
  def update(event: SNSEvent): Unit = {
    val messages: Seq[String] = event.getRecords.asScala.toSeq.map(record => record.getSNS.getMessage)
    MobileProfileUpdateAggregateRoot.writeMessages(messages)
  }
}
