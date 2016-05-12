package com.hiya.mobileprofile

trait Logging {
  def writeLog(message: String*) : Unit = {
    println(message)
  }
}
