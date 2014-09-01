package com.example.service

import akka.actor.Actor
import com.example.auth.UserPassAuthentication
import com.example.backend.api.{UserApiImpl, UserApi, TweetApi, TweetApiImpl}
import com.example.db.api.{DbCrudProvider, DbCrudProviderImpl}
import com.example.db.connection.{DbConnectionIdentifier, DefaultDbConnectionIdentifier}
import com.example.db.datamodel.{User, Tweet}
import com.example.marshalling.{BoxMarshallers, CustomMarshallers}
import net.liftweb.common.Box
import net.liftweb.json.{Extraction, JValue}
import org.bson.types.ObjectId
import spray.http.HttpHeaders.{Allow, `Access-Control-Allow-Methods`}
import spray.http.HttpMethods._
import spray.httpx.encoding.Gzip
import spray.routing.authentication.BasicAuth
import spray.routing.{Route, HttpService}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class Link(rel: String, href: String)

// this trait defines our service behavior independently from the service actor
trait RestServiceImpl
  extends RestService {

  val rootPathLinks: JValue = {
    import net.liftweb.json.JsonDSL._
    implicit val formats = net.liftweb.json.DefaultFormats
    val links =
      Link("self", "/") ::
      Link("users", "/users") ::
      Link("tweets", "/tweets") :: Nil
    ("links" -> links.map(Extraction.decompose(_)))
  }

  lazy val myRoute =
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
          entity(as[User]) { user =>
            complete {
              Future {
                saveUser(user)
              }
            }
          }
        } ~
          get {
            path(ObjectIdSegment) { id => // use .? or similar
              complete(Future {
                getUsers(Some(id))
              })
            } ~
              compressResponse(Gzip) {
                complete(Future {
                  getUsers()
                })
              }
          }
      } ~
      pathPrefix("tweets") {
        post {
          authenticate(BasicAuth(userPassAuthenticator _, realm = "secure site")) { user =>
            entity(as[Tweet]) { tweet =>
              complete {
                Future {
                  saveTweet(tweet, user)
                }
              }
            }
          }
        } ~
          get {
            parameters('pageSize.as[Int] ? 10, 'offset.as[Int] ? 0, 'userId.as[ObjectId] ?) { (pageSize, offset, uid) =>
              compressResponse(Gzip) {
                complete {
                  Future {
                    getTweets(pageSize, offset, uid)
                  }
                }
              }
            }
          }
      }
}