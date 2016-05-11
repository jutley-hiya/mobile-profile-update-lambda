package com.hiya.mobileprofile
import java.io.ByteArrayInputStream
import java.nio.charset.StandardCharsets

trait MobileProfileDeserializer {

  val scalaMapper = {
    import com.fasterxml.jackson.databind.ObjectMapper
    import com.fasterxml.jackson.module.scala.DefaultScalaModule
    new ObjectMapper().registerModule(new DefaultScalaModule)
  }

  def deserializeMessage(profileAsJson: String): MobileProfile = {
    val stream = new ByteArrayInputStream(profileAsJson.getBytes(StandardCharsets.UTF_8));
    val profile = scalaMapper.readValue(stream, classOf[MobileProfile])
    println("Got profile " + profile)
    profile
  }
}
