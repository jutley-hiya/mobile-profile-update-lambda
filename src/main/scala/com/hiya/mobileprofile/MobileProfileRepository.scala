package com.hiya.mobileprofile

import com.typesafe.config.ConfigFactory

import scala.collection.JavaConverters._

import com.amazonaws.regions.Regions
import com.amazonaws.services.dynamodbv2.{AmazonDynamoDB, AmazonDynamoDBClient}
import com.amazonaws.services.dynamodbv2.model._

trait MobileProfileRepository {

  //TODO: get from Typesafe config
  val region = Regions.getCurrentRegion
  val dynamoDB : AmazonDynamoDB = new AmazonDynamoDBClient().withRegion(region)
  val config = ConfigFactory.load("mobile-profile-update.conf")

  val tableConf = config.getConfig("mobile-profile-update.table")
  val table = tableConf.getString("name")
  val key = tableConf.getString("key")
  val column = tableConf.getString("column")

  def save(profile: MobileProfile): Unit = {
    println("Updating Region " + region)
    val request: UpdateItemRequest = createUpdateRequest(profile)
    println("Sending request " + request)
    //TODO: exception handling on failure
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
