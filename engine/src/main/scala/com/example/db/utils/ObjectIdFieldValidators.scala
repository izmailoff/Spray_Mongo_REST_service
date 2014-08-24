package com.example.db.utils

import net.liftweb.common.Full
import net.liftweb.util.{FieldIdentifier, FieldError}
import org.bson.types.ObjectId

import scala.xml.Text

/**
 * Provides validation functions for Record fields based on ObjectId.
 */
trait ObjectIdFieldValidators {
  self: FieldIdentifier =>

  /**
   * A validation helper. Make sure the ObjectId is set, i.e. not null
   * and generate validation issue if not.
   */
  def valNonEmpty(msg: => String = "Value is required")(value: ObjectId): List[FieldError] =
    if(value == null) List(FieldError(this, Text(msg)))
    else Nil
}
