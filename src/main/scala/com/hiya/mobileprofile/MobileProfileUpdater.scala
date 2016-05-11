package com.hiya.mobileprofile

import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets
import java.util.concurrent.{TimeUnit}

import com.amazonaws.regions.{Region, Regions}
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClient
import com.amazonaws.services.dynamodbv2.model._

import scala.collection.JavaConverters._
import com.amazonaws.services.lambda.runtime.events.SNSEvent

case class MobileProfile(phoneNumber: String, profile: String)

/**
  * Inspired from https://aws.amazon.com/blogs/compute/writing-aws-lambda-functions-in-scala/
  */
class MobileProfileUpdater {

  //TODO: get from Typesafe config
  val table = "caller_ids"
  val dynamoDB = new AmazonDynamoDBAsyncClient()
  val regions = List(Regions.US_WEST_2, Regions.EU_CENTRAL_1, Regions.AP_SOUTHEAST_1, Regions.AP_SOUTHEAST_2, Regions.SA_EAST_1)
  val timeout = 3

  val scalaMapper = {
    import com.fasterxml.jackson.databind.ObjectMapper
    import com.fasterxml.jackson.module.scala.DefaultScalaModule
    new ObjectMapper().registerModule(new DefaultScalaModule)
  }

  def update(event: SNSEvent): Unit = {
    val result = event.getRecords.asScala.map(record => record.getSNS.getMessage)
    result.map( profileAsJson => {
      deserializeMessageAndUpdate(profileAsJson)
    })

    val cutePrint = result.asJava
    println(cutePrint)
  }

  def deserializeMessageAndUpdate(profileAsJson: String): List[UpdateItemResult] = {
    val stream = new ByteArrayInputStream(profileAsJson.getBytes(StandardCharsets.UTF_8));
    val profile = scalaMapper.readValue(stream, classOf[MobileProfile])
    println("Got profile " + profile)
    regions.map {
      updateRegionsWithProfile(profile)
    }
  }

  def updateRegionsWithProfile(profile: MobileProfile): (Regions) => UpdateItemResult = {
    r =>
      val region = Region.getRegion(r)
      println("Updating Region " + region)
      dynamoDB.setRegion(region)
      val update = new AttributeValueUpdate().withAction(AttributeAction.PUT).withValue(new AttributeValue().withS(profile.profile))
      val request: UpdateItemRequest = createUpdateRequest(profile, update)
      println("Sending request " + request)
      //TODO: exception handling on failure
      dynamoDB.updateItemAsync(request).get(timeout, TimeUnit.MINUTES)
  }

  def createUpdateRequest(profile: MobileProfile, update: AttributeValueUpdate): UpdateItemRequest = {
    val request = new UpdateItemRequest()
    request.setTableName(table)
    //TODO: externalize config
    request.setKey(Map(("phone_number", new AttributeValue().withS(profile.phoneNumber))).asJava)
    request.setAttributeUpdates(Map(("mobile_profile", update)).asJava)
    request
  }
}
