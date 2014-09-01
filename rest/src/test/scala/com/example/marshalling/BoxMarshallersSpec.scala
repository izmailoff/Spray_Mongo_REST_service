package com.example.marshalling

import akka.actor.ActorSystem
import com.example.utils.log.AkkaLoggingHelper
import net.liftweb.common._
import org.specs2.mutable.Specification
import net.liftweb.json._
import net.liftweb.json.JsonDSL._
import spray.http._
import spray.httpx.marshalling._

class BoxMarshallersSpec
  extends Specification
  with BoxMarshallers
  with AkkaLoggingHelper {

  implicit val globalSystem = ActorSystem()

  "The Box Marshaller" should {
    "properly marshall an empty box instance to JSON" in {
      val expectedResponse = pretty(render(("status" -> "SUCCESSFUL")))
      marshal[Box[_]](Full(())) === Right(HttpEntity(ContentTypes.`application/json`, expectedResponse))
    }

    "properly marshall a full box instance to JSON" in {
      val expectedResponse = pretty(render(("status" -> "SUCCESSFUL") ~ ("value" -> "123")))
      marshal[Box[_]](Full(123)) === Right(HttpEntity(ContentTypes.`application/json`, expectedResponse))
    }

    // TODO: add tests for all cases here
  }

}