package com.hiya.mobileprofile

import com.amazonaws.regions.Regions
import com.amazonaws.services.cloudwatch.{AmazonCloudWatchClient, AmazonCloudWatch}
import com.amazonaws.services.dynamodbv2.{AmazonDynamoDBClient, AmazonDynamoDB}
import com.amazonaws.services.lambda.runtime.events.SNSEvent
import com.typesafe.config.ConfigFactory

import scala.collection.JavaConverters._
import scala.util.control.NonFatal


/**
  * Inspired from https://aws.amazon.com/blogs/compute/writing-aws-lambda-functions-in-scala/
  */
class MobileProfileUpdater extends Logging {
  import MobileProfileUpdater._

  def update(event: SNSEvent): Unit = {
    val messages: Seq[String] = event.getRecords.asScala.toSeq.map(record => record.getSNS.getMessage)
    messages.foreach { profileAsJson =>
      repository.save(deserializer.deserialize(profileAsJson))
    }
    val cutePrint = messages
    writeLog(cutePrint:_*)
  }
}

object MobileProfileUpdater {
  lazy val config = ConfigFactory.load("mobile-profile-update.conf")
  lazy val cloudWatchClient : AmazonCloudWatch = new AmazonCloudWatchClient()
  lazy val monitor: Monitoring = new CloudWatchMonitoring(cloudWatchClient, config)

  lazy val recoveryStrategy = PartialFunction[Throwable, Unit] {
    case NonFatal(t) =>
      monitor.writeMetric("WriteFailure", 1.0)
      throw t
  }

  lazy val region = Regions.getCurrentRegion
  lazy val dynamoDB : AmazonDynamoDB = new AmazonDynamoDBClient().withRegion(region)

  lazy val repository: MobileProfileRepository =
    new RecoverableMobileProfileRepository(
      new DynamoDbMobileProfileRepository(dynamoDB, config.getConfig("mobile-profile-update.table")), recoveryStrategy)

  lazy val deserializer: Deserializer[String, MobileProfile] = new MobileProfileDeserializer
}