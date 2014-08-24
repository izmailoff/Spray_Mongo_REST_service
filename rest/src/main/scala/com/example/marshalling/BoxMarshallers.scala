package com.example.marshalling

import java.text.SimpleDateFormat

import net.liftweb.common._
import net.liftweb.record.Record
import spray.http.{HttpEntity, ContentTypes}
import spray.httpx.marshalling.Marshaller
import net.liftweb.json._
import net.liftweb.json.JsonDSL._

/**
 * Marshallers that will take care of converting result type of [[Box]] to proper end-client
 * message format.
 */
trait BoxMarshallers {

  implicit def liftJsonFormats = new DefaultFormats {
    override def dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
  }

  /**
   * This is similar to [[spray.httpx.marshalling.MetaMarshallers.optionMarshaller]] except that
   * we can communicate generic errors and specific error messages back.
   *
   * The convention we use is the following:
   *
   * Full - result of operation was successful and it should be communicated back to the client.
   * ParamFailure - an error happened and it should be communicated back to the client together with the param value.
   * Exception will not be communicated back because it may contain sensitive information and it's not so useful to
   * the client.
   * Failure - an error happened and it should be communicated back to the client.
   * Empty - an error happened but there is no specific message to pass back to the client. Prefer other
   * types of failure over this one.
   *
   * Additionally, based on types of values passed in Box subclasses we can convert some results to
   * others. For instance if Unit is returned the message should become empty.
   */
  implicit val ValidationResultMarshaller =
    Marshaller.of[Box[_]](ContentTypes.`application/json`) {
      (value, contentType, ctx) =>
        val responseDoc = value match {
          case Full(()) =>
            successResponse
          case Full(value: Record[_]) =>
            successResponse ~ responseValue(value.asJValue)
          case Full(value) =>
            successResponse ~ responseValue(value)
          case ParamFailure(message, exception, _, value: JValue) =>
            // log exception: error(exception)
            failedResponse ~ responseMessage(message) ~ responseValue(value)
          case ParamFailure(message, exception, _, value) =>
            // log exception: error(exception)
            failedResponse ~ responseMessage(message) ~ responseValue(value)
          case Failure(message, exception, _) =>
            // log exception: error...
            failedResponse ~ responseMessage(message)
          case Empty =>
            // log some error - debug
            failedResponse ~ responseMessage("Failure reason unknown, see the logs.")
        }
        ctx.marshalTo(HttpEntity(contentType, pretty(render(responseDoc))))
    }

  // These might be enums:
  val SUCCESSFUL = "SUCCESSFUL"
  val FAILED = "FAILED"

  def response(result: String): JObject =
    ("status" -> result)

  def responseMessage(message: String): JObject =
    ("message" -> message)

  def responseValue(value: JValue): JObject =
    ("value" -> value)

  def responseValue(value: Any): JObject =
    responseValue(JString(value.toString))

  val successResponse = response(SUCCESSFUL)

  val failedResponse = response(FAILED)

}
