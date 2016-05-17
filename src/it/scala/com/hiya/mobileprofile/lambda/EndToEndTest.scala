package com.hiya.mobileprofile.lambda

import com.amazonaws.services.dynamodbv2.model.{ResourceNotFoundException, _}
import com.amazonaws.services.dynamodbv2.{AmazonDynamoDB, AmazonDynamoDBClient}
import com.amazonaws.services.lambda.model._
import com.amazonaws.services.lambda.{AWSLambda, AWSLambdaClient}
import com.amazonaws.services.s3.{AmazonS3, AmazonS3Client}
import com.amazonaws.services.sns.{AmazonSNS, AmazonSNSClient}
import com.hiya.mobileprofile.lambda.sbtinfo.BuildInfo
import com.typesafe.config.ConfigFactory
import org.scalatest.concurrent.Eventually
import org.scalatest.concurrent.PatienceConfiguration.{Interval, Timeout}
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, FunSpec, ShouldMatchers}

import scala.collection.JavaConverters._
import scala.sys.process.Process
import scala.util.Try

class EndToEndTest extends FunSpec with ShouldMatchers with BeforeAndAfterAll with Eventually {

  val salt = scala.util.Random.alphanumeric.take(5).mkString
  val config = ConfigFactory.load("mobile-profile-update-it")

  val projectName = BuildInfo.name
  val projectVersion = BuildInfo.version
  val jarPath = BuildInfo.it_assembly_assemblyOutputPath

  val tableName = config.getString("mobile-profile-update.table.name")
  val functionName = s"$projectName-test-$salt"
  val functionPath = "com.hiya.mobileprofile.lambda.MobileProfileUpdaterTest::update"
  val topicName = s"$projectName-test-$salt"
  val bucketName = "gateway-lambdas"
  val s3JarName = s"staging/$projectName-test-$salt"

  val region = MobileProfileUpdaterTest.region
  val dynamoDBClient: AmazonDynamoDB = new AmazonDynamoDBClient().withRegion(region)
  val lambdaClient: AWSLambda = new AWSLambdaClient().withRegion(region)
  val s3Client: AmazonS3 = new AmazonS3Client().withRegion(region)
  val snsClient: AmazonSNS = new AmazonSNSClient().withRegion(region)

  var topicArn: String = _

  override def beforeAll(): Unit = {
    // Build fat jar and upload to S3
    val assemblyRes: Int = Process("sbt it:assembly").!
    assemblyRes should be(0)

    println(s"Uploading jar $s3JarName to S3")
    s3Client.putObject(bucketName, s3JarName, jarPath)

    println(s"Creating DynamoDB table $tableName")
    AWSHelpers.createTable(tableName, dynamoDBClient)

    println(s"Creating lambda $functionName")
    val lambdaRes = AWSHelpers.createLambdaFunction(functionName, functionPath, bucketName, s3JarName, lambdaClient)

    println(s"Creating SNS topic $topicName")
    val topicRes = snsClient.createTopic(topicName)
    topicArn = topicRes.getTopicArn

    println("Creating SNS -> Lambda connection")
    AWSHelpers.linkSNStoLambda(topicRes.getTopicArn, lambdaRes.getFunctionArn, functionName, lambdaClient, snsClient)
  }

  override def afterAll(): Unit = {
    println("Removing table")
    val deleteTableRequest = new DeleteTableRequest().withTableName(tableName)
    dynamoDBClient.deleteTable(deleteTableRequest)

    println("Removing lambda")
    val deleteFunctionRequest = new DeleteFunctionRequest().withFunctionName(functionName)
    lambdaClient.deleteFunction(deleteFunctionRequest)

    println("Removing SNS topic")
    snsClient.deleteTopic(topicArn)

    println("Removing jar from S3")
    s3Client.deleteObject(bucketName, s3JarName)
  }

  describe("MobileProfileUpdater") {
    it("should blank") {
      val payload =
        """
          |{
          |  "phoneNumber":"phone",
          |  "profile":"profile"
          |}
        """.stripMargin
      println("Putting item into dynamo")
      val pubRes = snsClient.publish(topicArn, payload)

      val getItemRequest =
        new GetItemRequest()
          .withTableName(tableName)
          .withKey(Map("phone_number" -> new AttributeValue().withS("phone")).asJava)
          .withAttributesToGet("mobile_profile")

      eventually(Timeout(Span(90, Seconds)), Interval(Span(2, Seconds))) {
        println("Fetching from dynamo")
        val getItemResOpt = Try {
          Option(dynamoDBClient.getItem(getItemRequest))
        }.recover {
          case _: ResourceNotFoundException =>
            println("No table")
            None
        }.get
        println(getItemResOpt)
        getItemResOpt shouldBe defined
        getItemResOpt.get.getItem.asScala("mobile_profile").getS should be("profile")
      }
    }
  }

}
