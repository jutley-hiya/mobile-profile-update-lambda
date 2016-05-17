package com.hiya.mobileprofile.lambda

import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

import com.hiya.mobileprofile.{Logging, MobileProfile}

class MobileProfileDeserializer extends Deserializer[String, MobileProfile] with Logging {
  val scalaMapper = {
    import com.fasterxml.jackson.databind.ObjectMapper
    import com.fasterxml.jackson.module.scala.DefaultScalaModule
    new ObjectMapper().registerModule(new DefaultScalaModule)
  }

  def deserialize(profileAsJson: String): MobileProfile = {
    require(profileAsJson != null, "profileAsJson must not be null")
    val stream = new ByteArrayInputStream(profileAsJson.getBytes(StandardCharsets.UTF_8));
    val profile = scalaMapper.readValue(stream, classOf[MobileProfile])
    logInfo("Got profile " + profile)
    profile
  }

}
