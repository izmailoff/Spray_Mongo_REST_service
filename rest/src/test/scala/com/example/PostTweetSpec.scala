package com.example

import com.example.backend.api.TweetApiImpl
import com.example.db.api.DbCrudProviderImpl
import com.example.test.utils.RandomDbConnectionIdentifier
import com.example.testutils.db.MongoDbTestContext
import org.bson.types.ObjectId
import org.specs2.mutable.Specification
import spray.http.HttpHeaders.{`Content-Type`, Accept, `Remote-Address`}
import spray.testkit.Specs2RouteTest
import net.liftweb.json._
import spray.http.MediaTypes._

class PostTweetSpec
  extends Specification
  with RandomDbConnectionIdentifier
  with MongoDbTestContext
  with Specs2RouteTest
  with MyService
  with DbCrudProviderImpl
  with TweetApiImpl {

  override def actorRefFactory = system

  "Posting a tweet" should {
    "succeed if tweet is properly formed" in databaseContext(currentMongoId) {
      val newTweet =
        Tweets.createRecord
          .createdBy(new ObjectId())
          .text("Some text")
      Tweets.count must be equalTo (0)

      Post("/tweet", newTweet).withHeaders(`Content-Type`(`application/json`)) ~> sealRoute(myRoute) ~> check {
        handled must beTrue
        responseAs[String] must be equalTo ("Saved")
        val updatedTweets = Tweets.findAll
        updatedTweets must have size (1)
        updatedTweets exists (t =>
          t.text.get == newTweet.text.get && t.createdBy.get == newTweet.createdBy.get) must beTrue
      }
    }
  }

}

