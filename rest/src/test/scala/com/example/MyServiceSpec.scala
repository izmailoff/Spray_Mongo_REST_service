package com.example

import com.example.backend.api.TweetApiImpl
import com.example.db.api.DbCrudProviderImpl
import com.example.service.MyService
import com.example.test.utils.db.RandomDbConnectionIdentifier
import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http._
import StatusCodes._

class MyServiceSpec
  extends Specification
  with Specs2RouteTest
  with RandomDbConnectionIdentifier
  with MyService
  with DbCrudProviderImpl
  with TweetApiImpl {

  def actorRefFactory = system
  
  "MyService" should {

    "return a greeting for GET requests to the root path" in {
      Get() ~> myRoute ~> check {
        handled must beFalse
      }
    }

    "return a Not Found error for PUT requests to the root path" in {
      Put() ~> sealRoute(myRoute) ~> check {
        status === NotFound
        responseAs[String] === "The requested resource could not be found."
      }
    }
  }
}
