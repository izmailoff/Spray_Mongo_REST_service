package com.example.service

import com.example.test.utils.db.ServiceTestContext
import net.liftweb.json.JsonDSL._
import net.liftweb.json._
import org.bson.types.ObjectId
import spray.http.HttpHeaders.`Content-Type`
import spray.http.MediaTypes._
import spray.http.{BasicHttpCredentials, HttpChallenge, HttpHeaders, StatusCodes}

class PostTweetSpec
  extends ServiceTestContext {

  val tweetText = "Some text"
  val newTweet: JValue = ("text" -> tweetText)
  val newTweetStr: String = pretty(render(newTweet))

  def SuccessfulTweetPostResult(creatorId: ObjectId): JValue =
    ("status" -> "SUCCESSFUL") ~
      ("value" ->
        ("text" -> tweetText) ~ ("createdBy" -> creatorId.toString))

  "Posting a tweet" should {
    "succeed if tweet is properly formed and user is authenticated" in serviceContext { (service: RestServiceApi) =>
      import service.{response => _, _}

      val user =
        Users.createRecord
          .email("test@example.com")
          .fullName("Tester")
          .username("test")
          .password("testpass")
          .save
      Tweets.count must be equalTo (0)
      val validCredentials = BasicHttpCredentials(user.username.get, user.password.get)

      Post("/tweets", newTweet).withHeaders(`Content-Type`(`application/json`)) ~>
        addCredentials(validCredentials) ~> sealRoute(route) ~> check {
        handled must beTrue
        val responseJson = removeAutogenFields(responseAs[JValue])
        SuccessfulTweetPostResult(user.id.get) must be equalTo(responseJson)
        val updatedTweets = Tweets.findAll
        updatedTweets must have size (1)
        updatedTweets exists (t =>
          t.text.get == tweetText && t.createdBy.get == user.id.get) must beTrue
      }
    }

    "fail if tweet is properly formed and user is NOT authenticated" in serviceContext { (service: RestServiceApi) =>
      import service.{response => _, liftJsonUnmarshaller => _, _} // FIXME: liftJsonUnmarshaller messes with responseAs[String]

      Tweets.count must be equalTo (0)
      val invalidCredentials = BasicHttpCredentials("RandomName", "WrongPassword")

      Post("/tweets", newTweet).withHeaders(`Content-Type`(`application/json`)) ~>
        addCredentials(invalidCredentials) ~> sealRoute(route) ~> check {
        handled must beTrue
        status === StatusCodes.Unauthorized
        header[HttpHeaders.`WWW-Authenticate`].get.challenges.head === HttpChallenge("Basic", "secure site")
        responseAs[String] must be equalTo("The supplied authentication is invalid")
        val updatedTweets = service.Tweets.findAll
        updatedTweets must have size (0)
      }
    }

    // TODO: maybe put all tests in a Table:
    // TODO: add a test for disabled user account
    // TODO: add a test for: responseAs[String] === "The resource requires authentication, which was not supplied with the request"
    // TODO: add a test for a tweet that fails validation
  }

}

