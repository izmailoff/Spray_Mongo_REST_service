package com.example.marshalling

import java.text.SimpleDateFormat

import com.example.utils.log.AkkaLoggingHelper
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
  this: AkkaLoggingHelper =>

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
            logConditionally(successResponse)
          case Full(value: Record[_]) =>
            logConditionally(successResponse ~ responseValue(value.asJValue))
          case Full(value) =>
            logConditionally(successResponse ~ responseValue(value))
          case ParamFailure(message, exception, _, value: JValue) =>
            logConditionally(failedResponse ~ responseMessage(message) ~ responseValue(value), exception)
          case ParamFailure(message, exception, _, value) =>
            logConditionally(failedResponse ~ responseMessage(message) ~ responseValue(value), exception)
          case Failure(message, exception, _) =>
            logConditionally(failedResponse ~ responseMessage(message), exception)
          case Empty =>
            logConditionally(failedResponse ~ responseMessage("Failure reason unknown, see the logs."))
        }
        ctx.marshalTo(HttpEntity(contentType, pretty(render(responseDoc))))
    }

  /**
   * If exception was encountered during processing we want to log it at the ERROR level - this should be
   * quite uncommon and requires investigation. Otherwise we log everything at DEBUG level for
   * potential troubleshooting.
   * @param exception an optional exception that occured
   * @param value value that is returned to the client and also logged for debugging purposes.
   * @return returns back unmodified result/value
   */
  private def logConditionally(value: JValue, exception: Box[Throwable] = Empty): JValue = {
    val msg = s"Returned to the client:\n ${pretty(render(value))}."
    exception match {
      case Full(e) =>
        log.error(e, msg)
      case _ =>
        log.debug(msg)
    }
    value
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
