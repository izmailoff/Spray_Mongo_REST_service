package com.example.test.utils.db

import com.example.backend.api.{TweetApiImpl, UserApiImpl}
import com.example.db.api.DbCrudProviderImpl
import com.example.service.{RestServiceApi, RestServiceImpl}
import com.github.izmailoff.mongo.connection.RandomDbConnectionIdentifier
import com.github.izmailoff.service.RestService
import com.github.izmailoff.testing.{AroundOutsideRestService, RestServiceMongoDbTestContext}
import net.liftweb.json.JsonAST.JField
import net.liftweb.json._
import spray.http.HttpHeaders.`Content-Encoding`
import spray.http.{HttpEncoding, HttpResponse}
import spray.util._

trait ServiceTestContext
  extends RestServiceMongoDbTestContext[RestServiceApi] {

  def serviceContext = new AroundOutsideRestService[RestServiceApi](system) {
    override val service = new RestServiceImpl  ///????????????????? which RestService type to use????
      with RandomDbConnectionIdentifier
      with DbCrudProviderImpl
      with UserApiImpl
      with TweetApiImpl {
      def actorRefFactory = system
      val globalSystem = system
    }
  }

  def haveContentEncoding(encoding: HttpEncoding) =
    beEqualTo(Some(`Content-Encoding`(encoding))) ^^ {
      (_: HttpResponse).headers.findByType[`Content-Encoding`]
    }

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
