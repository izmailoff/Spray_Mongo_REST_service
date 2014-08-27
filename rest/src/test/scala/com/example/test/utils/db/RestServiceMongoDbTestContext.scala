package com.example.test.utils.db

import com.example.backend.api.{UserApiImpl, TweetApiImpl}
import com.example.db.api.DbCrudProviderImpl
import com.example.marshalling.CustomMarshallers
import com.example.service.{ServiceType, MyService}
import net.liftweb.json.JsonAST.JField
import net.liftweb.json._
import org.specs2.execute.{Result, AsResult}
import org.specs2.mutable.Specification
import org.specs2.specification.AroundOutside
import spray.http.HttpHeaders.`Content-Encoding`
import spray.http.{HttpEncoding, HttpResponse}
import spray.routing.Route
import spray.testkit.Specs2RouteTest
import spray.util._

trait RestServiceMongoDbTestContext
  extends Specification
  with MongoDbTestContext
  with Specs2RouteTest {

  val globalSystem = system

  def serviceContext =
    new AroundOutside[ServiceType] {
      val service = new MyService
        with RandomDbConnectionIdentifier
        with DbCrudProviderImpl
        with TweetApiImpl
        with UserApiImpl {
        def actorRefFactory = system
        val globalSystem = system
      }

      def around[T: AsResult](t: => T): Result =
        databaseContext(service.currentMongoId).around(t) //TODO: cleanup - inherit from MongoDbTestContext directly?

      def outside: ServiceType = service
    }

  def haveContentEncoding(encoding: HttpEncoding) =
    beEqualTo(Some(`Content-Encoding`(encoding))) ^^ { (_: HttpResponse).headers.findByType[`Content-Encoding`] }

  /**
   * Removes auto generated fields from JSON response. This makes it
   * easier to compare JSON afterwards in tests.
   */
  def removeAutogenFields(json: JValue): JValue =
    parse(compact(render(
      json remove {
        case JField("_id", _) | JField("when", _) => true
        case _ => false
      })))

}
