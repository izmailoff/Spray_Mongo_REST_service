package com.example.marshalling

import java.util.UUID

import com.example.db.api.DbCrudProvider
import com.example.db.datamodel.{User, Tweet}
import net.liftweb.common._
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
import net.liftweb.util.ControlHelpers._

trait CustomMarshallers
  extends DbCrudProvider {

  implicit val JsonMarshaller = jsonMarshaller(`application/json`)

  // TODO: replace with lib trait and the reverse one too...
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

  implicit lazy val TweetUnmarshaller = requestToRecordUnmarshaller(Tweets)

  implicit lazy val UserUnmarshaller = requestToRecordUnmarshaller(Users)

  def requestToRecordUnmarshaller[T <: Record[T]](metaRec: T with MetaRecord[T]) =
    new FromRequestUnmarshaller[T] {
      def apply(req: HttpRequest): Deserialized[T] =
        req.entity match {
          case HttpEntity.NonEmpty(contentType, data) =>
            val res = for {
              jsonData <- tryo(parse(data.asString))
              result <- jsonToRecordUnpickler(jsonData, metaRec)
            } yield result
            res toRight (MalformedContent(s"'${data.asString}' is not valid"))
          case HttpEntity.Empty =>
            Left(ContentExpected)
        }
    }

  /**
   * Converts JSON to a DB object (Record) by setting all fields provided in JSON document.
   * It's expected that users perform validation of the result before they use it.
   * @param json
   * @param metaRec
   * @tparam T
   * @return A full box with DB object is returned if all fields could be set.
   */
  private def jsonToRecordUnpickler[T <: Record[T]](json: JValue, metaRec: T with MetaRecord[T]): Box[T] = {
    val record = metaRec.createRecord
    record.setFieldsFromJValue(json) match {
      case Full(()) => Full(record)
      case _ => Failure("Bad request format.")
    }
  }

  implicit val String2ObjectIdConverter = new Deserializer[String, ObjectId] {
    def apply(value: String) =
      if (ObjectId.isValid(value))
        Right(new ObjectId(value))
      else
        Left(MalformedContent("'" + value + "' is not a valid ObjectId value"))
  }

  /**
   * A PathMatcher that matches and extracts an ObjectId instance.
   */
  val ObjectIdSegment = PathMatcher( """^[0-9a-fA-F]{24}$""".r).flatMap { str => // TODO: no need for regex! - double check
    if (ObjectId.isValid(str)) Some(new ObjectId(str))
    else None
  }

}
