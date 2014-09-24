package com.example.service

import com.example.test.utils.db.ServiceTestContext
import net.liftweb.json.JsonDSL._
import net.liftweb.json._
import spray.http.HttpHeaders.Allow
import spray.http.HttpMethods._
import spray.http.StatusCodes._

class RestServiceImplSpec
  extends ServiceTestContext {

  val optionsResponse: JObject =
    ("links" -> List(
      ("rel" -> "self") ~ ("href" -> "/"),
      ("rel" -> "users") ~ ("href" -> "/users"),
      ("rel" -> "tweets") ~ ("href" -> "/tweets")))
  
  "REST Service" should {

    "return static content for GET requests to the root path" in serviceContext { (service: RestServiceApi) =>
      import service.{sealRoute, route}
      Get() ~> sealRoute(route) ~> check {
        responseAs[String] must be equalTo("\"static content goes here later\"")
      }
    }

    "return a Method Not Allowed error for PUT requests to the root path" in serviceContext { (service: RestServiceApi) =>
      import service.{sealRoute, route}
      Put() ~> sealRoute(route) ~> check {
        status === MethodNotAllowed
        responseAs[String] must be equalTo("HTTP method not allowed, supported methods: OPTIONS, GET")
      }
    }

    "return supported methods together with all API links starting from the root path" in serviceContext { (service: RestServiceApi) =>
      import service._
      Options() ~> sealRoute(route) ~> check {
        header[Allow] must be equalTo(Some(Allow(OPTIONS, GET)))
        responseAs[JValue] must be equalTo(optionsResponse)
      }
    }
  }
}
