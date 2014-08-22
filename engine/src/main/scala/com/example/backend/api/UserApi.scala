package com.example.backend.api

import com.example.db.api.{DbCrudProviderImpl, DbCrudProvider}
import com.example.db.datamodel.User
import org.bson.types.ObjectId
import com.foursquare.rogue.LiftRogue._

trait UserApi
  extends DbCrudProvider {

  def saveUser(user: User): Unit

  def getUsers(userId: Option[ObjectId] = None): List[User]
}

trait UserApiImpl
  extends UserApi
  with DbCrudProviderImpl {

  def saveUser(user: User): Unit =
    user.save

  def getUsers(userId: Option[ObjectId] = None) =
    Users.whereOpt(userId)(_.id eqs _).fetch()
}