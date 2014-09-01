package com.example.service

import akka.actor.Actor
import com.example.backend.api.{TweetApiImpl, UserApiImpl}
import com.example.db.api.DbCrudProviderImpl
import com.example.db.connection.DefaultDbConnectionIdentifier

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class RestServiceHandler
  extends Actor
  with DefaultDbConnectionIdentifier
  with RestServiceImpl
  with DbCrudProviderImpl
  with UserApiImpl
  with TweetApiImpl {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)

  val globalSystem = actorRefFactory.system
}
