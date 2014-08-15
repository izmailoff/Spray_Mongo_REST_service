package com.example.db.datamodel

import com.example.db.connection.MongoConfig
import com.example.db.constants.CollectionNames
import com.foursquare.index.IndexedRecord
import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.mongodb.record.field.ObjectIdPk
import net.liftweb.record.field.{EmailField, StringField}
import net.liftweb.mongodb.BsonDSL._

abstract class User
  extends MongoRecord[User]
  with ObjectIdPk[User]
  with IndexedRecord[User] {

  object username extends StringField(this, 40)

  object email extends EmailField(this, 128)
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