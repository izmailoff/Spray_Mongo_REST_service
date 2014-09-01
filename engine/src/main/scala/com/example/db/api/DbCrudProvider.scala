package com.example.db.api

import com.example.db.connection.DbConnectionIdentifier
import com.example.db.datamodel.{UserMeta, User, Tweet, TweetMeta}
import net.liftweb.mongodb.MongoIdentifier
import net.liftweb.mongodb.record.MongoMetaRecord

/**
 * Defines all available CRUD interfaces for collections. This has far more flexible configuration
 * than regular `object MetaRecord` which is especially useful for tests.
 */
trait DbCrudProvider {
  this: DbConnectionIdentifier =>

  val Tweets: Tweet with MongoMetaRecord[Tweet]

  val Users: User with MongoMetaRecord[User]
}

trait DbCrudProviderImpl
  extends DbCrudProvider {
  this: DbConnectionIdentifier =>

  override val Tweets = new TweetMeta {
    override implicit def mongoIdentifier = currentMongoId
  }

  override val Users = new UserMeta {
    override implicit def mongoIdentifier = currentMongoId
  }
}
