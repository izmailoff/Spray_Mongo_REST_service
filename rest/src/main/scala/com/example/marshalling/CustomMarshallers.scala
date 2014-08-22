package com.example.marshalling

import java.util.UUID

import com.example.db.api.DbCrudProvider
import com.example.db.datamodel.{User, Tweet}
import net.liftweb.common.{Box, Failure, Full}
import net.liftweb.json._
import net.liftweb.mongodb.record.BsonRecord
import net.liftweb.record.{MetaRecord, Record}
import org.bson.types.ObjectId
import spray.http.ContentTypes.`application/json`
import spray.httpx.marshalling.Marshaller
import spray.httpx.unmarshalling._
import spray.http._
import spray.http.ContentTypes._
import spray.routing._

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
      jsonToRecordUnpickler(_, Tweets)
    }

  implicit val Json2UserUnmarshaller =
    Unmarshaller.delegate[JValue, Box[User]](ContentTypeRange.`*`) {
      jsonToRecordUnpickler(_, Users)
    }

  private def jsonToRecordUnpickler[T <: Record[T]](json: JValue, metaRec: T with MetaRecord[T]): Box[T] = {
    val record = metaRec.createRecord
    record.setFieldsFromJValue(json) match {
      case Full(()) =>
        record.validate match {
          case Nil => Full(record)
          case errors => Failure(s"Validation failed with: $errors.") // TODO: might need nicer formatting
        }
      case _ => Failure("Bad request format.")
    }
  }

  // Use it for query params:
  //  implicit val String2ObjectIdConverter = new Deserializer[String, ObjectId] {
  //    def apply(value: String) =
  //      if (ObjectId.isValid(value))
  //        Right(new ObjectId(value))
  //      else
  //        Left(MalformedContent("'" + value + "' is not a valid ObjectId value"))
  //  }

  /**
   * A PathMatcher that matches and extracts an ObjectId instance.
   */
  val ObjectIdSegment = PathMatcher("""^[0-9a-fA-F]{24}$""".r).flatMap { str =>
    if (ObjectId.isValid(str)) Some(new ObjectId(str))
    else None
  }

}
