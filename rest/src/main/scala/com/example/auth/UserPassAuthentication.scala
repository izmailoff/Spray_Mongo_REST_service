package com.example.auth

import com.example.backend.api.UserApi
import com.example.db.datamodel.User
import net.liftweb.common._
import spray.routing.authentication.UserPass
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Provides authentication for users.
 */
trait UserPassAuthentication
extends UserApi {

  def userPassAuthenticator(userPass: Option[UserPass]): Future[Option[User]] =
    Future {
      userPass flatMap (creds => authenticate(creds.user, creds.pass))
    }
}
