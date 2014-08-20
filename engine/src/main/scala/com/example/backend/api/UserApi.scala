package com.example.backend.api

import com.example.db.api.{DbCrudProviderImpl, DbCrudProvider}
import com.example.db.datamodel.User

trait UserApi
  extends DbCrudProvider {

  def saveUser(user: User): Unit

}

trait UserApiImpl
  extends UserApi
  with DbCrudProviderImpl {

  def saveUser(user: User): Unit =
    user.save
}