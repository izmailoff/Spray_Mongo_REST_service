package com.example

import com.example.backend.api.{UserApiImpl, TweetApiImpl}
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
  with TweetApiImpl
  with UserApiImpl {

  def actorRefFactory = system
  
  "MyService" should {

    "return the main page for GET requests to the root path" in {
      Get() ~> myRoute ~> check {
        handled must beFalse
      }
    }.pendingUntilFixed("This test will be updated shortly once the tested path has complete implementation.")

    "return a Method Not Allowed error for PUT requests to the root path" in {
      Put() ~> sealRoute(myRoute) ~> check {
        status === MethodNotAllowed
        responseAs[String] === "HTTP method not allowed, supported methods: OPTIONS, GET"
      }
    }
  }
}
