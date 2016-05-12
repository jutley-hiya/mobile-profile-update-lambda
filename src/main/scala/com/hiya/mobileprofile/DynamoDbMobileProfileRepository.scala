package com.hiya.mobileprofile

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.model.{AttributeAction, AttributeValue, AttributeValueUpdate, UpdateItemRequest}
import com.typesafe.config.Config

import scala.collection.JavaConverters._

class DynamoDbMobileProfileRepository(dynamoDB: AmazonDynamoDB, config: Config) extends MobileProfileRepository with Logging {
  val tableConf = config.getConfig("mobile-profile-update.table")
  val table = tableConf.getString("name")
  val key = tableConf.getString("key")
  val column = tableConf.getString("column")

  def save(profile: MobileProfile): Unit = {

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
      .withKey(Map((key, new AttributeValue().withS(profile.phoneNumber))).asJava)
      .withAttributeUpdates(Map((column, update)).asJava)
  }
}
