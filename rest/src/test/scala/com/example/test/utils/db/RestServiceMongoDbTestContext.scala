package com.example.test.utils.db

import com.example.backend.api.{UserApiImpl, TweetApiImpl}
import com.example.db.api.DbCrudProviderImpl
import com.example.marshalling.CustomMarshallers
import com.example.service.{ServiceType, MyService}
import org.specs2.execute.{Result, AsResult}
import org.specs2.mutable.Specification
import org.specs2.specification.AroundOutside
import spray.testkit.Specs2RouteTest

trait RestServiceMongoDbTestContext
  extends Specification
  with MongoDbTestContext
  with Specs2RouteTest {

  def serviceContext =
    new AroundOutside[ServiceType] {
      val service = new MyService
        with RandomDbConnectionIdentifier
        with DbCrudProviderImpl
        with TweetApiImpl
        with UserApiImpl
        with CustomMarshallers {
        override def actorRefFactory = system
      }

      def around[T: AsResult](t: => T): Result =
        databaseContext(service.currentMongoId).around(t)

      def outside: ServiceType = service
    }
}
