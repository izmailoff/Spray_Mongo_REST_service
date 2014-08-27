package com.example.utils.log

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}

/**
 * Helps to get a logger in traits that are not actors or do not have actor system directly available.
 * Intention is to reduce boilerplate code in all classes that need logging.
 */
trait AkkaLoggingHelper {

  val globalSystem: ActorSystem

  lazy val log: LoggingAdapter = Logging(globalSystem, getClass)
}
