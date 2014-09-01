package com.example.service

import com.example.auth.UserPassAuthentication
import com.example.backend.api.{TweetApi, UserApi}
import com.example.db.api.DbCrudProvider
import com.example.db.connection.DbConnectionIdentifier
import com.example.marshalling.{BoxMarshallers, CustomMarshallers}
import com.example.utils.log.AkkaLoggingHelper
import spray.routing._

trait RestService
  extends HttpService
  with AkkaLoggingHelper
  with DbConnectionIdentifier
  with DbCrudProvider
  with UserApi
  with TweetApi
  with CustomMarshallers
  with BoxMarshallers
  with UserPassAuthentication {
  def myRoute: Route
}
