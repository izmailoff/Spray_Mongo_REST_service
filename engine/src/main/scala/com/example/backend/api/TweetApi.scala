package com.example.backend.api

import com.example.db.api.{DbCrudProviderImpl, DbCrudProvider}
import com.example.db.datamodel.{User, Tweet}
import com.example.utils.validation.ValidationHelpers
import com.mongodb.WriteConcern
import net.liftweb.common._
import org.bson.types.ObjectId
import com.foursquare.rogue.LiftRogue._
import ValidationHelpers._

/**
 * A backend API that implements all actions related to tweets.
 */
trait TweetApi
  extends DbCrudProvider
  with UserApi {

  /**
   * Validates and saves a tweet.
   * @param tweet
   * @return if tweet validation was successful a tweet is returned after being saved in DB.
   *         Failure is returned if validation failed or internal server error encountered.
   */
  def saveTweet(tweet: Tweet, creator: User): Box[Tweet]

  def getTweets(lastN: Int, byUserId: Option[ObjectId] = None): List[Tweet]
}

trait TweetApiImpl
  extends TweetApi
  with DbCrudProviderImpl {

  def saveTweet(tweet: Tweet, creator: User): Box[Tweet] = {
    val tweetWithCreator = tweet.createdBy(creator.id.get)
    for {
      _ <- validateRecord(tweetWithCreator)
      _ <- userAllowedToTweet(creator)
    } yield tweetWithCreator.save
  }

  def getTweets(lastN: Int, byUserId: Option[ObjectId] = None) =
    Tweets.whereOpt(byUserId)(_.createdBy eqs _)
      .orderDesc(_.when)
      .limit(lastN)
      .fetch()
}