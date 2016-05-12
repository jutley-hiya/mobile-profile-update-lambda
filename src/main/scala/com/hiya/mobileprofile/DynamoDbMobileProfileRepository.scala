package com.hiya.mobileprofile

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.model.{AttributeAction, AttributeValue, AttributeValueUpdate, UpdateItemRequest}
import com.typesafe.config.Config

import scala.collection.JavaConverters._

class DynamoDbMobileProfileRepository(dynamoDB: AmazonDynamoDB, config: Config) extends MobileProfileRepository with Logging {
  require(dynamoDB != null)
  require(config != null)

  val table = config.getString("name")
  val key = config.getString("key")
  val column = config.getString("column")

  def save(profile: MobileProfile): Unit = {
    require(profile != null)

    val request: UpdateItemRequest = createUpdateRequest(profile)
    writeLog("Sending request " + request)
    dynamoDB.updateItem(request)
  }

  private def createUpdateRequest(profile: MobileProfile): UpdateItemRequest = {
    val update = new AttributeValueUpdate()
      .withAction(AttributeAction.PUT)
      .withValue(new AttributeValue().withS(profile.profile))
    new UpdateItemRequest()
      .withTableName(table)
      .withKey(Map(key -> new AttributeValue().withS(profile.phoneNumber)).asJava)
      .withAttributeUpdates(Map(column -> update).asJava)
  }
}
