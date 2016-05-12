package com.hiya.mobileprofile

import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

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
    writeLog("Got profile " + profile)
    profile
  }

}
