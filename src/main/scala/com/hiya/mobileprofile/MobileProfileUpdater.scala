package com.hiya.mobileprofile

import com.amazonaws.services.lambda.runtime.events.SNSEvent

import scala.collection.JavaConverters._


/**
  * Inspired from https://aws.amazon.com/blogs/compute/writing-aws-lambda-functions-in-scala/
  */
class MobileProfileUpdater extends MobileProfileDeserializer {
  val repository: MobileProfileRepository =
    new RecoverableMobileProfileRepository(
      new DynamoDbMobileProfileRepository(), PartialFunction.empty)

  def update(event: SNSEvent): Unit = {
    val result = event.getRecords.asScala.toSeq.map(record => record.getSNS.getMessage)
    result.foreach(profileAsJson =>
      repository.save(deserializeMessage(profileAsJson)))
    val cutePrint = result.asJava
    println(cutePrint)
  }
}
