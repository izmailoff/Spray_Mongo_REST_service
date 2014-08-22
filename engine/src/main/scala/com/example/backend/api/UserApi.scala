package com.example.backend.api

import com.example.db.api.{DbCrudProviderImpl, DbCrudProvider}
import com.example.db.datamodel.User
import com.example.utils.ValidationHelpers._
import org.bson.types.ObjectId
import com.foursquare.rogue.LiftRogue._
import net.liftweb.common._

trait UserApi
  extends DbCrudProvider {

  def saveUser(user: User): Unit

  def getUsers(userId: Option[ObjectId] = None): List[User]

  def userAllowedToTweet(user: User): ValidationResult =
    if (user.isActive.get) SUCCESS
    else Failure("Sorry, you are not allowed to tweet because your account was deactivated.")

  def authenticate(username: String, pass: String): Box[User]
}

trait UserApiImpl
  extends UserApi
  with DbCrudProviderImpl {

  def saveUser(user: User): Unit =
    user.save

  def getUsers(userId: Option[ObjectId] = None) =
    Users.whereOpt(userId)(_.id eqs _).fetch()

  def authenticate(username: String, pass: String): Box[User] = {
    val user: Box[User] =
      Users.where(_.username eqs username)
        .and(_.password eqs pass)
        .limit(1)
        .fetch()
        .headOption
    user ?~ "Authentication failed"
  }
}