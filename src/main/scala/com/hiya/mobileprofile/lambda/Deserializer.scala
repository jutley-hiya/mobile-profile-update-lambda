package com.hiya.mobileprofile.lambda

trait Deserializer[I, O] {
  def deserialize(profile: I): O
}