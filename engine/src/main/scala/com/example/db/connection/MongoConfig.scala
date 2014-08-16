package com.example.db.connection

import com.mongodb.{WriteConcern, ServerAddress, MongoClient}
import com.typesafe.config.ConfigFactory
import net.liftweb.mongodb.{MongoIdentifier, MongoDB}
import scala.util.{Success, Failure, Try}

/**
 * Defines default MongoDB connection settings for Record based entity classes and Rogue DSL.
 * Provides helper methods for defining connection properties.
 */
object MongoConfig {

  val conf = ConfigFactory.load.getConfig("application.mongo")

  val defaultHostname = conf.getString("host")
  val defaultPort = conf.getInt("port")
  val defaultDbName = conf.getString("dbName")

  /**
   * Defines a Mongo connection identifier based on supplied settings and falls back to config defaults.
   * Call this method multiple times if multiple connections need to be defined.
   * This is a first method to call before ANY DB operations.
   */
  def registerConnection(hostname: String = defaultHostname, port: Int = defaultPort,
                                dbName: String = defaultDbName): MongoIdentifier = {
    val dbConnection = getConnection(hostname, port)
    val currentMongoId = createMongoId(dbName)
    MongoDB.defineDb(currentMongoId, dbConnection, dbName)
    currentMongoId
  }

  def getConnection(hostname: String = defaultHostname, port: Int = defaultPort): MongoClient = {
    val server = new ServerAddress(hostname, port)
    val dbConnection = new MongoClient(server)
    dbConnection setWriteConcern WriteConcern.ACKNOWLEDGED
    dbConnection
  }

  def createMongoId(dbName: String = defaultDbName): MongoIdentifier =
    new MongoIdentifier {
      def jndiName: String = dbName
    }

  /**
   * Checks if there is connectivity to MongoDB server by
   * testing supplied DB connection identifier and running a DB operation on it.
   */
  def isConnected(implicit mongoId: MongoIdentifier): Boolean =
    Try {
      MongoDB.getDb(mongoId).map(_.getCollectionNames()).isDefined
    } match {
      case Success(isConnected) => isConnected
      case Failure(e) => false // TODO: add err logging later
    }
}
