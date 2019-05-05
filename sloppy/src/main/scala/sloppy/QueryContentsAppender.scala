package sloppy

import slick.lifted.Query

// Given an `RE` (which should be LIFTED) from a query's rows,
// produce an `(RA, RV)` (lifted, corresponding to a NON lifted (A, V))
// for each row in the final result set.  That is, this appends a
// computed value (RV) to the input value (RA) for each input row.
//
// This seems to be a common, useful pattern for capturing small
// but meaningful snippets.
//
// This roughly corresponds to:
//  `(Query[RA, A, C] => (RA => RE)) => Query[(RA, RV), (A, V), C]`

abstract class QueryContentsAppender[RE, RV, V](
    implicit private[QueryContentsAppender] val _vShape: FlattishShape[RV, V])
    extends QueryContentsLens[RE, QueryContentsAppender.Appending[RV, V]#From] { outer =>
  import QueryContentsAppender._
  type DeprecatedShape[X,Y] = QueryContentsAppender.Appending[RV, V]#From[X,Y]

  def appendTo[RA, A, C[_]](
      qwe: QueryWithExtractor[RA, A, C, RE])(
      implicit aShape: FlattishShape[RA, A])
  : Query[(RA, RV), (A, V), C]

  // Delegate to the above, which has a nicer type signature to implement.
  final override def transformTo[RA, A, C[_]](
      qwe: QueryWithExtractor[RA, A, C, RE])(
      implicit _aShape: FlattishShape[RA, A])
  : Query[Appending[RV, V]#From[RA, A]#RV, Appending[RV, V]#From[RA, A]#V, C] =
    appendTo(qwe)

  // Composition of appenders when the second depends only on values produced
  // by the first.
  def thenByUsing[RE2, RV2, V2](
      extract: RV => RE2)(
      that: QueryContentsAppender[RE2, RV2, V2])(
      implicit rv2Shape: FlattishShape[RV2, V2] = that._vShape)
  : QueryContentsAppender[RE, (RV, RV2), (V, V2)] =
    new QueryContentsAppender[RE, (RV, RV2), (V, V2)]() {
      override def appendTo[RA, A, C[_]](
          qwe: QueryWithExtractor[RA, A, C, RE])(
          implicit aShape: FlattishShape[RA, A])
      : Query[(RA, (RV, RV2)), (A, (V, V2)), C] = {
        import QueryCompositionOps._
        implicit val _vShapeFromOuter: FlattishShape[RV, V] = outer._vShape
        qwe.append(outer).byUsing(v => extract(v._2)).append(that).map { case ((a, rv), rv2) => (a, (rv, rv2)) }
      }
    }
}

object QueryContentsAppender {
  // Helper to produce the appropriate `DestinationShape`s needed.
  // This represents a transformation that maps into a tuple shape containing both the original
  // query contents and the computed value.
  final class Appending[_RV, _V] {
    trait From[RA, A] extends DestinationShape[RA, A] {
      type RV = (RA, _RV)
      type V = (A, _V)
    }
  }
}