package com.example.service

import akka.actor.Actor
import com.example.backend.api.{UserApiImpl, UserApi, TweetApi, TweetApiImpl}
import com.example.db.api.{DbCrudProvider, DbCrudProviderImpl}
import com.example.db.connection.{DbConnectionIdentifier, DefaultDbConnectionIdentifier}
import com.example.db.datamodel.{User, Tweet}
import com.example.marshalling.CustomMarshallers
import net.liftweb.common.Box
import net.liftweb.json.{Extraction, JValue}
import org.bson.types.ObjectId
import spray.http.HttpHeaders.{Allow, `Access-Control-Allow-Methods`}
import spray.http.HttpMethods._
import spray.routing.{Route, HttpService}

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class MyServiceActor
  extends Actor
  with MyService
  with DefaultDbConnectionIdentifier
  with DbCrudProviderImpl
  with TweetApiImpl
  with UserApiImpl {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}

trait ServiceType
  extends HttpService
  with DbConnectionIdentifier
  with DbCrudProvider
  with TweetApi
  with UserApi
  with CustomMarshallers {
  def myRoute: Route
}

case class Link(rel: String, href: String)

// this trait defines our service behavior independently from the service actor
trait MyService
  extends ServiceType {

  val rootPathLinks: JValue = {
    import net.liftweb.json.JsonDSL._
    implicit val formats = net.liftweb.json.DefaultFormats
    val links =
      Link("self", "/") ::
      Link("users", "/users") ::
      Link("tweets", "/tweets") :: Nil
    ("links" -> links.map(Extraction.decompose(_)))
  }

  val myRoute =
    path("") {
      options {
        respondWithHeader(Allow(OPTIONS, GET)) {
          complete {
            rootPathLinks
          }
        }
      } ~
        get {
          complete("static content goes here later")
        }
    } ~
      pathPrefix("users") {
        post {
          entity(as[Box[User]]) { user =>
            validate(user.isDefined, "Bad data format - TODO: need a better message here") {
              complete {
                saveUser(user.get)
                "User saved"
              }
            }
          }
        } ~
        get {
          path(ObjectIdSegment) { id => // use .? or similar
            complete(getUsers(Some(id)))
          } ~
          complete(getUsers())
        }
      } ~
      pathPrefix("tweets") {
        post {
          entity(as[Box[Tweet]]) { tweet =>
            validate(tweet.isDefined, "Bad data format - TODO: need a better message here") {
              complete {
                saveTweet(tweet.get)
                "Saved" // TODO: return the tweet back?
              }
            }
          }
        } ~
          get {
            complete(getTweets(100))
          }
      }
}