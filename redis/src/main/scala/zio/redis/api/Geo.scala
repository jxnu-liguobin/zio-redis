package zio.redis.api

import zio.redis.Input._
import zio.redis.Output._
import zio.redis._
import zio.{ Chunk, ZIO }

trait Geo {
  import Geo._

  /**
   *  Adds the specified geospatial `items` (latitude, longitude, name) to the specified `key`.
   *  @param key sorted set where the items will be stored
   *  @param item tuple of (latitude, longitude, name) to add
   *  @param items additional items
   *  @return number of new elements added to the sorted set
   */
  final def geoAdd(
    key: String,
    item: (LongLat, String),
    items: (LongLat, String)*
  ): ZIO[RedisExecutor, RedisError, Long] =
    GeoAdd.run((key, (item, items.toList)))

  /**
   *  Return the distance between two members in the geospatial index represented by the sorted set.
   *  @param key sorted set of geospatial members
   *  @param member1 member in set
   *  @param member2 member in set
   *  @param radiusUnit Unit of distance ("m", "km", "ft", "mi")
   *  @return distance between the two specified members in the specified unit or None if either member is missing
   */
  final def geoDist(
    key: String,
    member1: String,
    member2: String,
    radiusUnit: Option[RadiusUnit] = None
  ): ZIO[RedisExecutor, RedisError, Option[Double]] = GeoDist.run((key, member1, member2, radiusUnit))

  /**
   *  Return valid Geohash strings representing the position of one or more elements in a sorted set value representing
   *  a geospatial index.
   *  @param key sorted set of geospatial members
   *  @param member member in set
   *  @param members additional members
   *  @return chunk of geohashes, where value is `None` if a member is not in the set
   */
  final def geoHash(
    key: String,
    member: String,
    members: String*
  ): ZIO[RedisExecutor, RedisError, Chunk[Option[String]]] =
    GeoHash.run((key, (member, members.toList)))

  /**
   *  Return the positions (longitude, latitude) of all the specified members of the geospatial index represented by the
   *  sorted set at `key`.
   *  @param key sorted set of geospatial members
   *  @param member member in the set
   *  @param members additional members
   *  @return chunk of positions, where value is `None` if a member is not in the set
   */
  final def geoPos(
    key: String,
    member: String,
    members: String*
  ): ZIO[RedisExecutor, RedisError, Chunk[Option[LongLat]]] =
    GeoPos.run((key, (member, members.toList)))

  /**
   *  Return geospatial members of a sorted set which are within the area specified with a *center location* and the
   *  *maximum distance from the center*.
   *  @param key sorted set of geospatial members
   *  @param center position
   *  @param radius distance from the center
   *  @param radiusUnit Unit of distance ("m", "km", "ft", "mi")
   *  @param withCoord flag to include the position of each member in the result
   *  @param withDist flag to include the distance of each member from the center in the result
   *  @param withHash flag to include raw geohash sorted set score of each member in the result
   *  @param count limit the results to the first N matching items
   *  @param order sort returned items in the given `Order`
   *  @return chunk of members within the specified are
   */
  final def geoRadius(
    key: String,
    center: LongLat,
    radius: Double,
    radiusUnit: RadiusUnit,
    withCoord: Option[WithCoord] = None,
    withDist: Option[WithDist] = None,
    withHash: Option[WithHash] = None,
    count: Option[Count] = None,
    order: Option[Order] = None
  ): ZIO[RedisExecutor, RedisError, Chunk[GeoView]] =
    GeoRadius.run((key, center, radius, radiusUnit, withCoord, withDist, withHash, count, order))

  /**
   * Similar to geoRadius, but store the results to the argument passed to store and return the number of
   * elements stored. Added as a separate Scala function because it returns a Long instead of the list of results.
   * Find the geospatial members of a sorted set which are within the area specified with a *center location* and the
   * *maximum distance from the center*.
   *
   *  @param key sorted set of geospatial members
   *  @param center position
   *  @param radius distance from the center
   *  @param store sorted set where the results and/or distances should be stored
   *  @param radiusUnit Unit of distance ("m", "km", "ft", "mi")
   *  @param withCoord flag to include the position of each member in the result
   *  @param withDist flag to include the distance of each member from the center in the result
   *  @param withHash flag to include raw geohash sorted set score of each member in the result
   *  @param count limit the results to the first N matching items
   *  @param order sort returned items in the given `Order`
   *  @return chunk of members within the specified are
   */
  final def geoRadiusStore(
    key: String,
    center: LongLat,
    radius: Double,
    radiusUnit: RadiusUnit,
    store: StoreOptions,
    withCoord: Option[WithCoord] = None,
    withDist: Option[WithDist] = None,
    withHash: Option[WithHash] = None,
    count: Option[Count] = None,
    order: Option[Order] = None
  ): ZIO[RedisExecutor, RedisError, Long] =
    GeoRadiusStore.run(
      (key, center, radius, radiusUnit, withCoord, withDist, withHash, count, order, store.store, store.storeDist)
    )

  /**
   *  Return geospatial members of a sorted set which are within the area specified with an *existing member* in the set
   *  and the *maximum distance from the location of that member*.
   *  @param key sorted set of geospatial members
   *  @param member member in the set
   *  @param radius distance from the member
   *  @param radiusUnit Unit of distance ("m", "km", "ft", "mi")
   *  @param withCoord flag to include the position of each member in the result
   *  @param withDist flag to include the distance of each member from the center in the result
   *  @param withHash flag to include raw geohash sorted set score of each member in the result
   *  @param count limit the results to the first N matching items
   *  @param order sort returned items in the given `Order`
   *  number should be stored
   *  @return chunk of members within the specified area, or an error if the member is not in the set
   */
  final def geoRadiusByMember(
    key: String,
    member: String,
    radius: Double,
    radiusUnit: RadiusUnit,
    withCoord: Option[WithCoord] = None,
    withDist: Option[WithDist] = None,
    withHash: Option[WithHash] = None,
    count: Option[Count] = None,
    order: Option[Order] = None
  ): ZIO[RedisExecutor, RedisError, Chunk[GeoView]] =
    GeoRadiusByMember.run(
      (key, member, radius, radiusUnit, withCoord, withDist, withHash, count, order)
    )

  /**
   * Similar to geoRadiusByMember, but store the results to the argument passed to store and return the number of
   * elements stored. Added as a separate Scala function because it returns a Long instead of the list of results.
   * Find geospatial members of a sorted set which are within the area specified with an *existing member* in the set
   * and the *maximum distance from the location of that member*.
   *
   *  @param key sorted set of geospatial members
   *  @param member member in the set
   *  @param radius distance from the member
   *  @param radiusUnit Unit of distance ("m", "km", "ft", "mi")
   *  @param store sorted set where the results and/or distances should be stored
   *  @param withCoord flag to include the position of each member in the result
   *  @param withDist flag to include the distance of each member from the center in the result
   *  @param withHash flag to include raw geohash sorted set score of each member in the result
   *  @param count limit the results to the first N matching items
   *  @param order sort returned items in the given `Order`
   *  @return chunk of members within the specified area, or an error if the member is not in the set
   */
  final def geoRadiusByMemberStore(
    key: String,
    member: String,
    radius: Double,
    radiusUnit: RadiusUnit,
    store: StoreOptions,
    withCoord: Option[WithCoord] = None,
    withDist: Option[WithDist] = None,
    withHash: Option[WithHash] = None,
    count: Option[Count] = None,
    order: Option[Order] = None
  ): ZIO[RedisExecutor, RedisError, Long] =
    GeoRadiusByMemberStore.run(
      (key, member, radius, radiusUnit, withCoord, withDist, withHash, count, order, store.store, store.storeDist)
    )
}

private[redis] object Geo {
  final val GeoAdd: RedisCommand[(String, ((LongLat, String), List[(LongLat, String)])), Long] =
    RedisCommand("GEOADD", Tuple2(StringInput, NonEmptyList(Tuple2(LongLatInput, StringInput))), LongOutput)

  final val GeoDist: RedisCommand[(String, String, String, Option[RadiusUnit]), Option[Double]] =
    RedisCommand(
      "GEODIST",
      Tuple4(StringInput, StringInput, StringInput, OptionalInput(RadiusUnitInput)),
      OptionalOutput(DoubleOutput)
    )

  final val GeoHash: RedisCommand[(String, (String, List[String])), Chunk[Option[String]]] =
    RedisCommand("GEOHASH", Tuple2(StringInput, NonEmptyList(StringInput)), ChunkOptionalMultiStringOutput)

  final val GeoPos: RedisCommand[(String, (String, List[String])), Chunk[Option[LongLat]]] =
    RedisCommand("GEOPOS", Tuple2(StringInput, NonEmptyList(StringInput)), GeoOutput)

  final val GeoRadius: RedisCommand[
    (
      String,
      LongLat,
      Double,
      RadiusUnit,
      Option[WithCoord],
      Option[WithDist],
      Option[WithHash],
      Option[Count],
      Option[Order]
    ),
    Chunk[GeoView]
  ] =
    RedisCommand(
      "GEORADIUS",
      Tuple9(
        StringInput,
        LongLatInput,
        DoubleInput,
        RadiusUnitInput,
        OptionalInput(WithCoordInput),
        OptionalInput(WithDistInput),
        OptionalInput(WithHashInput),
        OptionalInput(CountInput),
        OptionalInput(OrderInput)
      ),
      GeoRadiusOutput
    )

  // We expect at least one of Option[Store] and Option[StoreDist] to be passed here.
  final val GeoRadiusStore: RedisCommand[
    (
      String,
      LongLat,
      Double,
      RadiusUnit,
      Option[WithCoord],
      Option[WithDist],
      Option[WithHash],
      Option[Count],
      Option[Order],
      Option[Store],
      Option[StoreDist]
    ),
    Long
  ] =
    RedisCommand(
      "GEORADIUS",
      Tuple11(
        StringInput,
        LongLatInput,
        DoubleInput,
        RadiusUnitInput,
        OptionalInput(WithCoordInput),
        OptionalInput(WithDistInput),
        OptionalInput(WithHashInput),
        OptionalInput(CountInput),
        OptionalInput(OrderInput),
        OptionalInput(StoreInput),
        OptionalInput(StoreDistInput)
      ),
      LongOutput
    )

  final val GeoRadiusByMember: RedisCommand[
    (
      String,
      String,
      Double,
      RadiusUnit,
      Option[WithCoord],
      Option[WithDist],
      Option[WithHash],
      Option[Count],
      Option[Order]
    ),
    Chunk[GeoView]
  ] =
    RedisCommand(
      "GEORADIUSBYMEMBER",
      Tuple9(
        StringInput,
        StringInput,
        DoubleInput,
        RadiusUnitInput,
        OptionalInput(WithCoordInput),
        OptionalInput(WithDistInput),
        OptionalInput(WithHashInput),
        OptionalInput(CountInput),
        OptionalInput(OrderInput)
      ),
      GeoRadiusOutput
    )

  // We expect at least one of Option[Store] and Option[StoreDist] to be passed here.
  final val GeoRadiusByMemberStore: RedisCommand[
    (
      String,
      String,
      Double,
      RadiusUnit,
      Option[WithCoord],
      Option[WithDist],
      Option[WithHash],
      Option[Count],
      Option[Order],
      Option[Store],
      Option[StoreDist]
    ),
    Long
  ] =
    RedisCommand(
      "GEORADIUSBYMEMBER",
      Tuple11(
        StringInput,
        StringInput,
        DoubleInput,
        RadiusUnitInput,
        OptionalInput(WithCoordInput),
        OptionalInput(WithDistInput),
        OptionalInput(WithHashInput),
        OptionalInput(CountInput),
        OptionalInput(OrderInput),
        OptionalInput(StoreInput),
        OptionalInput(StoreDistInput)
      ),
      LongOutput
    )
}
