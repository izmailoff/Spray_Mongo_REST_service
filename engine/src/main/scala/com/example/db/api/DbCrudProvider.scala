package com.example.db.api

import com.example.db.connection.DbConnectionIdentifier
import com.example.db.datamodel.{Tweet, TweetMeta}
import net.liftweb.mongodb.MongoIdentifier
import net.liftweb.mongodb.record.MongoMetaRecord

/**
 * Defines all available CRUD interfaces for collections. This has far more flexible configuration
 * than regular `object MetaRecord` which is especially useful for tests.
 */
trait DbCrudProvider extends DbConnectionIdentifier {

  val Tweets: Tweet with MongoMetaRecord[Tweet]
}

trait DbCrudProviderImpl extends DbCrudProvider {

  override val Tweets = new TweetMeta {
    override implicit def mongoIdentifier = currentMongoId
  }
}
