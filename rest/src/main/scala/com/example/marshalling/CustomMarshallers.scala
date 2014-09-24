package com.example.marshalling

import com.example.db.api.DbCrudProvider
import com.github.izmailoff.marshalling.MongoMarshallingSupport
import com.github.izmailoff.mongo.connection.DbConnectionIdentifier

trait CustomMarshallers
extends MongoMarshallingSupport
with DbConnectionIdentifier
with DbCrudProvider {
  //this: DbCrudProvider with MongoMarshallingSupport =>

  implicit lazy val TweetUnmarshaller = requestToRecordUnmarshaller(Tweets)

  implicit lazy val UserUnmarshaller = requestToRecordUnmarshaller(Users)

}
