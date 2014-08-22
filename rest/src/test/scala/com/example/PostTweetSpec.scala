package com.example

import com.example.backend.api.TweetApiImpl
import com.example.db.api.DbCrudProviderImpl
import com.example.service.{ServiceType, MyService}
import com.example.test.utils.db.{RestServiceMongoDbTestContext, MongoDbTestContext, RandomDbConnectionIdentifier}
import org.bson.types.ObjectId
import org.specs2.mutable.Specification
import spray.http.{HttpHeaders, HttpChallenge, StatusCodes, BasicHttpCredentials}
import spray.http.HttpHeaders.{`Content-Type`, Accept, `Remote-Address`}
import spray.testkit.Specs2RouteTest
import net.liftweb.json._
import spray.http.MediaTypes._

class PostTweetSpec
  extends RestServiceMongoDbTestContext {

  "Posting a tweet" should {
    "succeed if tweet is properly formed and user is authenticated" in serviceContext { (service: ServiceType) =>
      import service._

      val user =
        Users.createRecord
          .email("test@example.com")
          .fullName("Tester")
          .username("test")
          .password("testpass")
          .save
      val newTweet =
        Tweets.createRecord
          //.createdBy(new ObjectId()) -- this should not be allowed
          .text("Some text")
      Tweets.count must be equalTo (0)
      val validCredentials = BasicHttpCredentials(user.username.get, user.password.get)

      Post("/tweets", newTweet).withHeaders(`Content-Type`(`application/json`)) ~>
        addCredentials(validCredentials) ~> sealRoute(myRoute) ~> check {
        handled must beTrue
        responseAs[String] must be equalTo ("Saved")
        val updatedTweets = Tweets.findAll
        updatedTweets must have size (1)
        updatedTweets exists (t =>
          t.text.get == newTweet.text.get && t.createdBy.get == user.id.get) must beTrue
      }
    }

    "fail if tweet is properly formed and user is NOT authenticated" in serviceContext { (service: ServiceType) =>
      import service._

      val newTweet =
        Tweets.createRecord
          .text("Some text")
      Tweets.count must be equalTo (0)
      val invalidCredentials = BasicHttpCredentials("RandomName", "WrongPassword")

      Post("/tweets", newTweet).withHeaders(`Content-Type`(`application/json`)) ~>
        addCredentials(invalidCredentials) ~> sealRoute(myRoute) ~> check {
        handled must beTrue
        status === StatusCodes.Unauthorized
        header[HttpHeaders.`WWW-Authenticate`].get.challenges.head === HttpChallenge("Basic", "secure site")
        responseAs[String] must be equalTo ("The supplied authentication is invalid")

        val updatedTweets = Tweets.findAll
        updatedTweets must have size (0)
      }
    }

    // TODO: maybe put all tests in a Table:
    // TODO: add a test for disabled user account
    // TODO: add test for: responseAs[String] === "The resource requires authentication, which was not supplied with the request"
  }

}

