package com.example.service

import com.example.auth.UserPassAuthentication
import com.example.backend.api.{TweetApi, UserApi}
import com.example.db.api.DbCrudProvider
import com.example.marshalling.CustomMarshallers
import com.github.izmailoff.service.RestService
import spray.routing._

trait RestServiceApi
  extends RestService
  with DbCrudProvider
  with UserApi
  with TweetApi
  with CustomMarshallers
  with UserPassAuthentication

