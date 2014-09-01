package com.example.service

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import com.example.db.connection.MongoConfig
import com.example.utils.log.AkkaLoggingHelper
import com.typesafe.config.ConfigFactory
import spray.can.Http

object Boot
  extends App
  with AkkaLoggingHelper {

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("on-spray-can")
  override val globalSystem = system

  val conf = ConfigFactory.load.getConfig("application.network")
  val listenInterface = conf.getString("listenInterface")
  val listenPort = conf.getInt("listenPort")

  implicit val mongoId = MongoConfig.registerConnection()
  log.info("Is connected to Mongo? {}.", MongoConfig.isConnected)

  // create and start our service actor
  val service = system.actorOf(Props[RestServiceHandler], "demo-service")

  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ! Http.Bind(service, interface = listenInterface, port = listenPort)
}