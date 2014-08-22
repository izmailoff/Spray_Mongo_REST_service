package com.example.db.datamodel

import com.example.db.connection.MongoConfig
import com.example.db.constants.CollectionNames
import com.foursquare.index.IndexedRecord
import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.ObjectIdPk
import net.liftweb.record.field.{BooleanField, EmailField, StringField}
import net.liftweb.mongodb.BsonDSL._

abstract class User
  extends MongoRecord[User]
  with ObjectIdPk[User]
  with IndexedRecord[User] {

  object username extends StringField(this, 40)

  /**
   * Currently passwords are stored in plain text because I didn't have time to do proper encryption.
   * FIXME: fix plain pass later, alternatively use other auth mechanisms.
   */
  object password extends StringField(this, 64)

  object fullName extends StringField(this, 128)

  object email extends EmailField(this, 128)

  /**
   * If user account is deactivated the value will be false and he can't perform any actions.
   */
  object isActive extends BooleanField(this) {
    override def defaultValue = true
  }
}

trait UserMeta
  extends User
  with MongoMetaRecord[User] {
  self: MongoMetaRecord[User] =>

  override def meta = self

  override protected def instantiateRecord: User =
    new User {
      override val meta = self
    }

  override def collectionName = CollectionNames.USERS.toString

  if (MongoConfig.isConnected(mongoIdentifier)) {
    ensureIndex(username.name -> 1)
    ensureIndex(email.name -> 1)
  }
}