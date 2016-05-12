package com.hiya.mobileprofile

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest
import com.typesafe.config.Config
import org.scalamock.scalatest.MockFactory
import org.scalatest.{FunSpec, ShouldMatchers}
import scala.collection.JavaConverters._

class DynamoDbMobileProfileRepositoryTest extends FunSpec with ShouldMatchers with MockFactory {

  describe("DynamoDbMobileProfileRepository") {
    it("should save a profile to dynamo") {
      // Given: a configuration
      val config = mock[Config]
      val tableName = "table_name"
      val keyName = "key_name"
      val columnName = "column_name"
      (config.getString _).expects("name").returning(tableName).once()
      (config.getString _).expects("key").returning(keyName).once()
      (config.getString _).expects("column").returning(columnName).once()
      // And: a profile to write
      val profileString = "profile_dummy"
      val phoneNumber = "15555555555"
      val profile = MobileProfile(phoneNumber, profileString)
      // And: a DynamoDB client
      val dynamoDB = mock[AmazonDynamoDB]
      (dynamoDB.updateItem(_: UpdateItemRequest)).expects(where { request: UpdateItemRequest =>
        Seq[Boolean](
          request.getTableName == tableName,
          request.getKey.asScala.contains(keyName),
          request.getKey.asScala(keyName).getS == phoneNumber,
          request.getAttributeUpdates.asScala.contains(columnName),
          request.getAttributeUpdates.asScala(columnName).getValue.getS == profileString
        ).reduce(_ && _)
      })
      // And: a DynamoDbMobileProfileRepository
      val repo = new DynamoDbMobileProfileRepository(dynamoDB, config)
      // Expect: I save the profile and
      //         the profile is written into DynamoDB
      repo.save(profile)
    }

    it("should fail creation if dynamo client is null") {
      // Given: a configuration
      val config = mock[Config]
      // When: I attempt creation of a DynamoDbMobileProfileRepository with null dynamo client
      // Then: An Illegal argument exception is thrown
      an [IllegalArgumentException] should be thrownBy new DynamoDbMobileProfileRepository(null, config)
    }

    it("should fail creation if config is null") {
      // Given: a dynamo client
      val client = mock[AmazonDynamoDB]
      // When: I attempt creation of a DynamoDbMobileProfileRepository with null config
      // Then: An Illegal argument exception is thrown
      an [IllegalArgumentException] should be thrownBy new DynamoDbMobileProfileRepository(client, null)
    }

    it("should fail to save profile if profile is null") {
      // Given: a configuration
      val config = mock[Config]
      val tableName = "table_name"
      val keyName = "key_name"
      val columnName = "column_name"
      (config.getString _).expects("name").returning(tableName).once()
      (config.getString _).expects("key").returning(keyName).once()
      (config.getString _).expects("column").returning(columnName).once()
      // And: a DynamoDB client
      val dynamoDB = mock[AmazonDynamoDB]
      // And: a DynamoDbMobileProfileRepository
      val repo = new DynamoDbMobileProfileRepository(dynamoDB, config)
      // When: I try to save a null profile
      // Then: An IllegalArgumentException is thrown
      an [IllegalArgumentException] should be thrownBy repo.save(null)
    }
  }

}
