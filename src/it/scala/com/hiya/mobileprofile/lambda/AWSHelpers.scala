package com.hiya.mobileprofile.lambda

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.model._
import com.amazonaws.services.lambda.AWSLambda
import com.amazonaws.services.lambda.model.{AddPermissionRequest, CreateFunctionRequest, CreateFunctionResult, FunctionCode}
import com.amazonaws.services.sns.AmazonSNS
import com.amazonaws.services.sns.model.SubscribeRequest

object AWSHelpers {

  def createTable(name: String, client: AmazonDynamoDB): CreateTableResult = {
    val createTableRequest =
      new CreateTableRequest()
        .withTableName(name)
        .withKeySchema(new KeySchemaElement()
          .withAttributeName("phone_number")
          .withKeyType(KeyType.HASH))
        .withAttributeDefinitions(new AttributeDefinition()
          .withAttributeName("phone_number")
          .withAttributeType(ScalarAttributeType.S))
        .withProvisionedThroughput(new ProvisionedThroughput()
          .withReadCapacityUnits(5L)
          .withWriteCapacityUnits(5L))

    client.createTable(createTableRequest)
  }

  def createLambdaFunction(name: String, functionPath: String, bucket: String, jar: String, client: AWSLambda): CreateFunctionResult = {
    val createFunctionRequest =
      new CreateFunctionRequest()
        .withFunctionName(name)
        .withCode(new FunctionCode()
          .withS3Bucket(bucket)
          .withS3Key(jar))
        .withHandler(functionPath)
        .withRole("arn:aws:iam::858278828125:role/lambda_dynamo")
        .withRuntime("java8")
        .withMemorySize(512)
        .withTimeout(59)
    client.createFunction(createFunctionRequest)
  }

  def linkSNStoLambda(topicARN: String, lambdaARN: String, lambdaName: String, lambdaClient: AWSLambda, snsClient: AmazonSNS) = {
    val addPermissionRequest = new AddPermissionRequest()
      .withFunctionName(lambdaName)
      .withSourceArn(topicARN)
      .withAction("lambda:InvokeFunction")
      .withPrincipal("sns.amazonaws.com")
      .withStatementId(System.currentTimeMillis().toString)
    lambdaClient.addPermission(addPermissionRequest)

    val subscribeRequest =
      new SubscribeRequest()
        .withTopicArn(topicARN)
        .withProtocol("lambda")
        .withEndpoint(lambdaARN)
    snsClient.subscribe(subscribeRequest)
  }

}
