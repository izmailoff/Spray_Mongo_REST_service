package com.example

import com.example.service.ServiceType
import com.example.test.utils.db.RestServiceMongoDbTestContext
import spray.http.HttpHeaders.Allow
import spray.http.HttpMethods._
import spray.http.StatusCodes._
import net.liftweb.json.JsonDSL._
import net.liftweb.json._

class MyServiceSpec
  extends RestServiceMongoDbTestContext {

  val optionsResponse: JObject =
    ("links" -> List(
      ("rel" -> "self") ~ ("href" -> "/"),
      ("rel" -> "users") ~ ("href" -> "/users"),
      ("rel" -> "tweets") ~ ("href" -> "/tweets")))
  
  "MyService" should {

    "return the main page for GET requests to the root path" in serviceContext { (service: ServiceType) =>
      import service._
      Get() ~> myRoute ~> check {
        handled must beFalse
      }
    }.pendingUntilFixed("This test will be updated shortly once the tested path has complete implementation.")

    "return a Method Not Allowed error for PUT requests to the root path" in serviceContext { (service: ServiceType) =>
      import service._
      Put() ~> sealRoute(myRoute) ~> check {
        status === MethodNotAllowed
        responseAs[String] === "HTTP method not allowed, supported methods: OPTIONS, GET"
      }
    }

    "return supported methods together with all API links starting from the root path" in serviceContext { (service: ServiceType) =>
      import service._
      Options() ~> service.myRoute ~> check {
        // check headers and allowed methods
        header[Allow] must be equalTo(Some(Allow(OPTIONS, GET)))
        responseAs[JValue] diff optionsResponse must be equalTo(Diff(JNothing, JNothing, JNothing))
      }
    }
  }
}
