package com.hiya.mobileprofile.lambda

import com.hiya.mobileprofile.MobileProfile
import org.scalatest.{FunSpec, ShouldMatchers}

class MobileProfileDeserializerTest extends FunSpec with ShouldMatchers {

  describe("MobileProfileDeserializer") {
    it("should properly deserialize a json string into a MobileProfile domain object") {
      // Given: A profile string
      val profile = "profile"
      // And: A phone number string
      val phoneNumber = "phone number"
      // And: A mobile profile json string containing the phone number and profile
      val mobileProfileJson =
        s"""
           |{
           |  "phoneNumber": "$phoneNumber",
           |  "profile": "$profile"
           |}""".stripMargin
      // And: a MobileProfileDeserializer
      val deserializer = new MobileProfileDeserializer
      // When: I deserialize the json string
      val domainProfile = deserializer.deserialize(mobileProfileJson)
      // Then: I get a domain object with the correct phone and profile
      domainProfile should be(MobileProfile(phoneNumber, profile))
    }

    it("should throw an IllegalArgumentException when given a null profile") {
      // Given: A MobileProfileDeserializer
      val deserializer = new MobileProfileDeserializer
      // When: I try to deserialize a null value
      // Then: An IllegalArgumentException is thrown
      an [IllegalArgumentException] should be thrownBy deserializer.deserialize(null)
    }
  }

}
