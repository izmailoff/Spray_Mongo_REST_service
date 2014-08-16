package com.example

import akka.actor.Actor
import com.example.backend.api.{TweetApiImpl, TweetApi}
import com.example.db.api.{DbCrudProviderImpl, DbCrudProvider}
import com.example.db.connection.{DefaultDbConnectionIdentifier, DbConnectionIdentifier}
import com.example.marshalling.CustomMarshallers
import spray.routing._
import spray.http._
import MediaTypes._

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class MyServiceActor
  extends Actor
  with MyService
  with DefaultDbConnectionIdentifier
  with DbCrudProviderImpl
  with TweetApiImpl {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}


// this trait defines our service behavior independently from the service actor
trait MyService
  extends HttpService
  with DbConnectionIdentifier
  with DbCrudProvider
  with TweetApi
  with CustomMarshallers {

  val myRoute =
    pathPrefix("tweet") {
      post {
        complete {
          // FIXME: temporary code for populating DB
          val tweet = Tweets.createRecord
            .createdBy(new org.bson.types.ObjectId)
            .text(java.util.UUID.randomUUID.toString)
            .when(new java.util.Date())
          saveTweet(tweet)
          "ok"
        }
      } ~
        get {
          complete(getTweets(100))
        }
    }
}