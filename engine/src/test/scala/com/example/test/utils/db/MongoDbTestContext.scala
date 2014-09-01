package com.example.test.utils.db

import akka.event.LoggingAdapter
import com.example.db.constants.CollectionNames
import com.example.db.constants.CollectionNames.CollectionNames
import com.example.utils.log.AkkaLoggingHelper
import com.github.fakemongo.Fongo
//import com.mongodb.casbah.commons.MongoDBObject
import com.mongodb.{DB, DBCollection, Mongo}
import net.liftweb.mongodb.{MongoDB, MongoIdentifier}
import org.specs2.execute.{AsResult, Result}
import org.specs2.mutable.Around

/**
 * Defines a helper test trait that provides DB connection for tests.
 */
trait MongoDbTestContext
  extends AkkaLoggingHelper {

  /**
   * Override this if you want to define real MongoDB database connection or faked in-memory Fongo database.
   * @return
   */
  def mongo: Mongo = {
    val fongo = new Fongo("testserver")
    fongo.getMongo
  }

  /**
   * Sets Mongo connection, drops all previous collections, creates empty collections.
   *
   * Prints "delimiter" between each DB reset so that it's easier to read logs.
   */
  def databaseContext(implicit mongoId: MongoIdentifier) = new Around {
    override def around[T: AsResult](t: => T): Result = {
      val dbName = mongoId.jndiName
      log.debug("\n" + "*" * 20 + s" SPINNING UP MONGO DATABASE [$dbName] " + "*" * 20)
      MongoDB.defineDb(mongoId, mongo, dbName)
      val currentDb = MongoDB.getDb(mongoId).get
      createAllEmptyCollections(currentDb)
      dropAllCollections(currentDb)
      createAllEmptyCollections(currentDb)
      val result = AsResult(t)
      log.debug("*" * 20 + s"  SHUTTING DOWN MONGO DATABASE [$dbName]  " + "*" * 20 + "\n")
      currentDb.dropDatabase()
      result
    }
  }

  def getCollection(db: DB, name: String): DBCollection =
    db.getCollection(name)

  def getCollection(db: DB, constName: CollectionNames): DBCollection =
    getCollection(db, constName.toString)

  def dropCollection(db: DB, name: String): Unit =
    getCollection(db, name).drop()

  def dropCollection(db: DB, constName: CollectionNames): Unit =
    dropCollection(db, constName.toString)

  /**
   * Just referencing a collection should create it.
   */
  def createEmptyCollection(db: DB, name: String): Unit =
    getCollection(db, name)

  def createEmptyCollection(db: DB, constName: CollectionNames): Unit =
    createEmptyCollection(db, constName.toString)

  /**
   * Drops all collections keeping DB.
   */
  def dropAllCollections(db: DB): Unit =
    CollectionNames.values foreach {
      dropCollection(db, _)
    }

  /**
   * Creates/touches all empty collections in our DB. This avoids errors with index creation
   * if collection does not exist.
   */
  def createAllEmptyCollections(db: DB): Unit =
    CollectionNames.values foreach {
      createEmptyCollection(db, _)
    }

//  /**
//   * Removes all docs from specified collection by IDs.
//   */
//  def removeAllDocuments(db: DB, docIds: Traversable[ObjectId], collection: CollectionNames): Unit = {
//    val col = getCollection(db, collection)
//    docIds foreach { id => col.remove(MongoDBObject("_id" -> id))}
//  }
//
//  def findAllDocuments(db: DB, docIds: Traversable[ObjectId], collection: CollectionNames) = {
//    import com.mongodb.casbah.Imports._
//    val col = getCollection(db, collection)
//    col.find("_id" $in docIds.toList)
//  }
}
