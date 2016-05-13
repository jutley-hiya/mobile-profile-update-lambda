package com.hiya.mobileprofile

import com.amazonaws.regions.Regions
import com.amazonaws.services.cloudwatch.{AmazonCloudWatch, AmazonCloudWatchClient}
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient
import com.typesafe.config.{Config, ConfigFactory}

import scala.util.control.NonFatal

/**
  * The AGGREGATE ROOT/top-level FACADE for Mobile Profile updates.
  * http://martinfowler.com/bliki/DDD_Aggregate.html
  * http://blog.sapiensworks.com/post/2012/04/18/DDD-Aggregates-And-Aggregates-Root-Explained.aspx
  */
object MobileProfileUpdateAggregateRoot extends MobileProfileUpdateAggregateRootBlueprint{
  override val config: Config = ConfigFactory.load("mobile-profile-update.conf")

  override val deserializer: Deserializer[String, MobileProfile] = new MobileProfileDeserializer

  override val log: Logging = new Logging {}

  val cloudWatchClient : AmazonCloudWatch = new AmazonCloudWatchClient()
  override val monitor: Monitoring = new CloudWatchMonitoring(cloudWatchClient, config.getConfig("mobile-profile-update.metrics"))

  val region = Regions.fromName(System.getenv("AWS_DEFAULT_REGION"))
  val dynamoDB = new AmazonDynamoDBClient().withRegion(region)
  val dynamoTableConfig = config.getConfig("mobile-profile-update.table")
  override val coreRepository: MobileProfileRepository = new DynamoDbMobileProfileRepository(dynamoDB, dynamoTableConfig)
}

trait MobileProfileUpdateAggregateRootBlueprint {
  val config: Config
  val monitor: Monitoring
  val log: Logging
  val coreRepository: MobileProfileRepository
  val deserializer: Deserializer[String, MobileProfile]

  val recoveryStrategy = PartialFunction[Throwable, Unit] {
    case NonFatal(t) =>
      log.writeLog(t.toString)
      monitor.writeMetric("WriteFailure", 1.0)
  }

  val repositoryWithRecovery = new RecoverableMobileProfileRepository(coreRepository, recoveryStrategy)

  def writeMessages(messages: Seq[String]): Unit = {
    messages.foreach { profileAsJson =>
      repositoryWithRecovery.save(deserializer.deserialize(profileAsJson))
    }
    log.writeLog(messages:_*)
  }
}