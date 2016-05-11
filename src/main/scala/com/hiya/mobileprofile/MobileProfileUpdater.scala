package com.hiya.mobileprofile

import com.amazonaws.services.lambda.runtime.events.SNSEvent

import scala.collection.JavaConverters._


/**
  * Inspired from https://aws.amazon.com/blogs/compute/writing-aws-lambda-functions-in-scala/
  */
class MobileProfileUpdater extends MobileProfileRepository with MobileProfileDeserializer {
  def update(event: SNSEvent): Unit = {
    val result = event.getRecords.asScala.toSeq.map(record => record.getSNS.getMessage)
    result.foreach(profileAsJson =>
      save(deserializeMessage(profileAsJson)))
    val cutePrint = result.asJava
    println(cutePrint)
  }
}
