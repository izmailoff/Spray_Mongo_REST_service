package com.example.marshalling

import net.liftweb.json._
import net.liftweb.mongodb.record.BsonRecord
import spray.httpx.marshalling.Marshaller
import spray.httpx.unmarshalling._
import spray.http._
import spray.http.ContentTypes._

trait CustomMarshallers {

  implicit val JsonMarshaller = jsonMarshaller(`application/json`)

  def jsonMarshaller(contentType: ContentType, more: ContentType*): Marshaller[JValue] =
    Marshaller.of[JValue](contentType +: more: _*) { (value, contentType, ctx) =>
      ctx.marshalTo(HttpEntity(contentType, pretty(render(value))))
    }

  implicit val BsonRecordMarshaller =
    Marshaller.delegate[BsonRecord[_], JValue](`application/json`) (_.asJValue)

  implicit val BsonRecordCollectionMarshaller =
    Marshaller.delegate[Traversable[BsonRecord[_]], JValue](`application/json`) { records =>
      JArray(records.map(_.asJValue).toList)
    }
}
