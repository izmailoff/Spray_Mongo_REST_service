package com.example.db.connection

import net.liftweb.mongodb.MongoIdentifier

/**
 * Provides database name to be used in MetaRecords.
 * This will usually initialize before MetaRecords do.
 */
trait DbConnectionIdentifier {

  implicit def currentMongoId: MongoIdentifier
}

/**
 * Default implementation that provides DB name for production
 * and dev purposes. Unit tests will provide random or test
 * DB name instead.
 */
trait DefaultDbConnectionIdentifier
  extends DbConnectionIdentifier {

  override implicit val currentMongoId = MongoConfig.createMongoId()
}
