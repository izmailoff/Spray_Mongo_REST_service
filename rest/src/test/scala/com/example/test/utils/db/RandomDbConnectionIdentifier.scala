package com.example.test.utils.db

import com.example.db.connection.{DbConnectionIdentifier, MongoConfig}
import net.liftweb.util.StringHelpers

/**
 * Provides random DB name for each instance of test suite.
 */
trait RandomDbConnectionIdentifier
  extends DbConnectionIdentifier {

  override implicit val currentMongoId = MongoConfig.createMongoId(StringHelpers.randomString(10))
}
