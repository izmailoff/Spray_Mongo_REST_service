package com.example.utils

import net.liftweb.common._
import net.liftweb.json._
import net.liftweb.record.Record

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
        pretty(render(JArray(errors map { e => JString(e.msg.toString) }))))
    }
}
