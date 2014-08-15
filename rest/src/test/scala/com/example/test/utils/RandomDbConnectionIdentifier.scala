package com.example.test.utils

import com.example.db.connection.{MongoConfig, DbConnectionIdentifier}
import net.liftweb.util.StringHelpers

/**
 * Provides random DB name for each instance of test suite.
 */
trait RandomDbConnectionIdentifier
  extends DbConnectionIdentifier {

  override val currentMongoId = MongoConfig.createMongoId(StringHelpers.randomString(10))
}
