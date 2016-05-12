package com.hiya.mobileprofile

trait Deserializer[I, O] {
  def deserialize(profile: I): O
}
