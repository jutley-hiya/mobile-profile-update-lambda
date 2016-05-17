package com.hiya.mobileprofile.lambda

import com.amazonaws.services.lambda.runtime.Context
import com.amazonaws.services.lambda.runtime.events.SNSEvent
import com.hiya.mobileprofile._

import scala.collection.JavaConverters._

/**
  * Inspired from https://aws.amazon.com/blogs/compute/writing-aws-lambda-functions-in-scala/
  */
trait MobileProfileUpdater extends MobileProfileUpdateAggregateRoot {
  val deserializer = new MobileProfileDeserializer

  def update(event: SNSEvent, context: Context): Unit = {
    val messages: Seq[String] = event.getRecords.asScala.toSeq.map(record => record.getSNS.getMessage)
    val domainProfiles = messages.map(deserializer.deserialize)
    writeMessages(domainProfiles)
  }
}