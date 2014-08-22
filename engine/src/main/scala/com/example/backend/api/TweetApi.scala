package com.example.backend.api

import com.example.db.api.{DbCrudProviderImpl, DbCrudProvider}
import com.example.db.datamodel.Tweet
import org.bson.types.ObjectId
import com.foursquare.rogue.LiftRogue._

/**
 * A backend API that implements all actions related to tweets.
 */
trait TweetApi
  extends DbCrudProvider {

  // TODO: add validation
  def saveTweet(tweet: Tweet): Unit // TODO: should this be returning back the Tweet?

  def getTweets(lastN: Int, byUserId: Option[ObjectId] = None): List[Tweet]
}

trait TweetApiImpl
  extends TweetApi
  with DbCrudProviderImpl {

  def saveTweet(tweet: Tweet) =
    tweet.save

  def getTweets(lastN: Int, byUserId: Option[ObjectId] = None) =
    Tweets.whereOpt(byUserId)(_.createdBy eqs _)
      .orderDesc(_.when)
      .limit(lastN)
      .fetch()
}