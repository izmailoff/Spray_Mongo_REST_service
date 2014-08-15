package com.example.db.datamodel

import com.example.db.connection.{MongoConfig, DbConnectionIdentifier}
import com.example.db.constants.CollectionNames
import com.foursquare.index.IndexedRecord
import net.liftweb.mongodb.record.field.{DateField, ObjectIdField, ObjectIdPk}
import net.liftweb.mongodb.record.{MongoMetaRecord, MongoRecord}
import net.liftweb.record.field.StringField
import net.liftweb.mongodb.BsonDSL._

abstract class Tweet
  extends MongoRecord[Tweet]
  with ObjectIdPk[Tweet]
  with IndexedRecord[Tweet] {

  object text extends StringField(this, 140)

  object when extends DateField(this)

  object createdBy extends ObjectIdField(this)
}

trait TweetMeta
  extends Tweet
  with MongoMetaRecord[Tweet] {
  self: MongoMetaRecord[Tweet] =>

  override def meta = self

  override protected def instantiateRecord: Tweet =
    new Tweet {
      override val meta = self
    }

  override def collectionName = CollectionNames.TWEETS.toString

  if (MongoConfig.isConnected(mongoIdentifier)) {
    ensureIndex(createdBy.name -> 1)
    ensureIndex(when.name -> 1)
  }
}