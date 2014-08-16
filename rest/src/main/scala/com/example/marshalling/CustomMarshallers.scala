package com.example.marshalling

import com.example.db.api.DbCrudProvider
import com.example.db.datamodel.Tweet
import net.liftweb.common.{Box, Failure, Full}
import net.liftweb.json._
import net.liftweb.mongodb.record.BsonRecord
import spray.http.ContentTypes.`application/json`
import spray.httpx.marshalling.Marshaller
import spray.httpx.unmarshalling._
import spray.http._
import spray.http.ContentTypes._

trait CustomMarshallers extends DbCrudProvider {

  implicit val JsonMarshaller = jsonMarshaller(`application/json`)

  def jsonMarshaller(contentType: ContentType, more: ContentType*): Marshaller[JValue] =
    Marshaller.of[JValue](contentType +: more: _*) { (value, contentType, ctx) =>
      ctx.marshalTo(HttpEntity(contentType, pretty(render(value))))
    }

  implicit val BsonRecordMarshaller =
    Marshaller.delegate[BsonRecord[_], JValue](`application/json`)(_.asJValue)

  implicit val BsonRecordCollectionMarshaller =
    Marshaller.delegate[Traversable[BsonRecord[_]], JValue](`application/json`) { records =>
      JArray(records.map(_.asJValue).toList)
    }


  implicit val String2JsonUnmarshaller =
    Unmarshaller[JValue](MediaTypes.`application/json`) {
      case HttpEntity.NonEmpty(contentType, data) =>
        parse(new String(data.toByteArray.map(_.toChar)))
    }

  implicit val Json2TweetUnmarshaller =
    Unmarshaller.delegate[JValue, Box[Tweet]](ContentTypeRange.`*`) {
      jsonToTweetUnpickler(_)
    }

  private def jsonToTweetUnpickler(json: JValue): Box[Tweet] = {
    val tweet = Tweets.createRecord
    tweet.setFieldsFromJValue(json) match {
      case Full(()) =>
        tweet.validate match {
          case Nil => Full(tweet)
          case errors => Failure(s"Validation failed with: $errors.") // TODO: might need nicer formatting
        }
      case _ => Failure("Bad request format.")
    }
  }

}
