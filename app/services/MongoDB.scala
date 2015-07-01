package services

/**
 * Created by Simon Wang on 2014/12/17.
 */

import play.api.Play
import play.api.libs.iteratee.{Concurrent, Enumeratee, Iteratee}
import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.api._
import reactivemongo.bson.BSONDocument
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Contains DB related functions.
 */
class MongoDB(location:String, dbname:String)  {

  // get connection to the database
  val db: DefaultDB = createConnection(location, dbname)
  // create a enumerator that we use to broadcast received documents
  //  val (bcEnumerator, channel) = Concurrent.broadcast[BSONDocument]
  // assign the channel to the mongodb cursor enumerator
  /*val iteratee = createCursor(getCollection(Configuration.collection))
    .enumerate()
    .apply(Iteratee
    .foreach({doc: BSONDocument => channel.push(doc)}));*/
  val userCollection = getCollection("users")

  /**
   * Return a simple collection
   */
  private def getCollection(collection: String): BSONCollection = {
    db(collection)
  }

  /**
   * Create the connection
   */
  private def createConnection(location: String, dbname: String)  : DefaultDB = {
    // needed to connect to mongoDB.
    // gets an instance of the driver
    // (creates an actor system)
    val driver = new MongoDriver
    val connection = driver.connection(List(location))

    // Gets a reference to the database
    connection(dbname)
  }

  /**
   * Create the cursor
   */
  private def createCursor(collection: BSONCollection): Cursor[BSONDocument] = {
    import reactivemongo.api._
    import reactivemongo.bson._
    import scala.concurrent.Future

    import scala.concurrent.ExecutionContext.Implicits.global

    val query = BSONDocument(
      "currentDate" -> BSONDocument(
        "$gte" -> BSONDateTime(System.currentTimeMillis())
      ));

    // we enumerate over a capped collection
    collection.find(query)
      .options(QueryOpts().tailable.awaitData)
      .cursor[BSONDocument]
  }

  /**
   * Simple function that registers a callback and a predicate on the
   * broadcasting enumerator
   */
  /* def listenToCollection(f: BSONDocument => Unit,
                          p: BSONDocument => Boolean ) = {

     val it = Iteratee.foreach(f)
     val itTransformed = Enumeratee.takeWhile[BSONDocument](p).transform(it);
     bcEnumerator.apply(itTransformed);
   }*/
}

object MongoDB {
  private val mongoDB = new MongoDB(Play.current.configuration.getString("mongodb.uri").get, "life-log")
  val userCollection = mongoDB.userCollection
}
