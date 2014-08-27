package com.example.utils.validation

import net.liftweb.common._
import net.liftweb.json._
import net.liftweb.record.Record
import net.liftweb.util.FieldError

/**
 * Convenience methods and types related to validation.
 */
object ValidationHelpers {

  type ValidationResult = Box[Unit]

  val SUCCESS: ValidationResult = Full(())

  /**
   * Helps to extract an error message from a type of a failure to avoid extensive pattern matching.
   * To supply default error message use together with `?~` operator, for example:
   * {{{
   *   val error = Empty
   *   getErrorMessage(error ?~ "Default error message here")
   * }}}
   * @return
   */
  def getErrorMessage: PartialFunction[Box[_], String] = {
    case ParamFailure(msg, _, _, _) => msg
    case Failure(msg, _, _) => msg
  }

  def validateRecord[T <: Record[T]](rec: T): ValidationResult =
    rec validate match {
      case Nil => SUCCESS
      case errors => ParamFailure("Validation failed",
        JArray(errors map { e => JString(s"${getFieldName(e)}: ${e.msg.toString}") }))
    }

  /**
   * Extracts field name from a validation error. Unfortunately that error does not preserve
   * a reference to the field or its name. This is a bit too hackish. Alternatively you can
   * provide field name as part of error message in validation functions (valMinLen... for example).
   */
  private def getFieldName(fieldErr: FieldError): String = {
    val fieldId = fieldErr.field.uniqueFieldId.openOr("UnknownField")
    if(fieldId == "_id") fieldId
    else fieldId.replace("_id", "")
  }
}
