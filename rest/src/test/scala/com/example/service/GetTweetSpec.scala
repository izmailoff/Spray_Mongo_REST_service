package com.example.service

import com.example.test.utils.db.ServiceTestContext
import net.liftweb.json.JsonAST.JValue
import spray.http.HttpHeaders.`Content-Type`
import spray.http.MediaTypes._
import net.liftweb.util.StringHelpers._
import spray.http.HttpEncodings._
import spray.httpx.encoding.Gzip
import spray.routing.directives.EncodingDirectives._
import net.liftweb.json._

class GetTweetSpec
  extends ServiceTestContext {

  "Getting all tweets" should {
    "successfully return all of them compressed" in serviceContext { (service: RestServiceApi) =>
      import service.{response => weDontNeedIt, _}

      val user =
        Users.createRecord
          .email("test@example.com")
          .fullName("Tester")
          .username("test")
          .password("testpass")
          .save
      Tweets.findAll must have size 0
      val tweets = (1 to 10) map { _ =>
        Tweets.createRecord.createdBy(user.id.get).text(randomString(20))
      }
      tweets foreach (_.save)
      Tweets.findAll must have size 10
      val dbTweets = tweets.map { t => removeAutogenFields(t.asJValue) }

      Get("/tweets") ~> decompressRequest() {
        sealRoute(route)
      } ~> check {
        handled must beTrue
        response must haveContentEncoding(gzip)
        val result = Gzip.decode(response)
        val jsonBody = parse(result.entity.data.asString)
        jsonBody must beAnInstanceOf[JArray]
        val receivedTweets = removeAutogenFields(jsonBody).asInstanceOf[JArray].arr
        dbTweets must containTheSameElementsAs(receivedTweets)
      }
    }
  }

}
