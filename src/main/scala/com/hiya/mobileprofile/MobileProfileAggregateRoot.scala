package com.hiya.mobileprofile

import com.amazonaws.regions.Regions
import com.amazonaws.services.cloudwatch.{AmazonCloudWatch, AmazonCloudWatchClient}
import com.amazonaws.services.dynamodbv2.{AmazonDynamoDB, AmazonDynamoDBClient}
import com.typesafe.config.ConfigFactory

import scala.util.control.NonFatal

/**
  * The AGGREGATE ROOT/top-level FACADE for Mobile Profile updates.
  * http://martinfowler.com/bliki/DDD_Aggregate.html
  * http://blog.sapiensworks.com/post/2012/04/18/DDD-Aggregates-And-Aggregates-Root-Explained.aspx
  */
object MobileProfileAggregateRoot extends Logging {
  private lazy val config = ConfigFactory.load("mobile-profile-update.conf")
  private lazy val cloudWatchClient : AmazonCloudWatch = new AmazonCloudWatchClient()
  private lazy val monitor: Monitoring = new CloudWatchMonitoring(cloudWatchClient, config.getConfig("mobile-profile-update.metrics"))

  private lazy val recoveryStrategy = PartialFunction[Throwable, Unit] {
    case NonFatal(t) =>
      monitor.writeMetric("WriteFailure", 1.0)
      throw t
  }

  private lazy val region = Regions.fromName(System.getenv("AWS_DEFAULT_REGION"))
  private lazy val dynamoDB : AmazonDynamoDB = new AmazonDynamoDBClient().withRegion(region)

  private lazy val repository: MobileProfileRepository =
    new RecoverableMobileProfileRepository(
      new DynamoDbMobileProfileRepository(dynamoDB, config.getConfig("mobile-profile-update.table")),
      recoveryStrategy)

  private lazy val deserializer: Deserializer[String, MobileProfile] = new MobileProfileDeserializer

  def writeMessages(messages: Seq[String],
                    repository: MobileProfileRepository = repository,
                    deserializer: MobileProfileDeserializer = deserializer): Unit =
  {
    messages.foreach { profileAsJson =>
      repository.save(deserializer.deserialize(profileAsJson))
    }
    writeLog(messages:_*)
  }
}
