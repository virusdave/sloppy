package sloppy

import slick.lifted.{Query, Rep}

// A query transformer that knows how to act on any query which
// contains a type that we can extract an `RE` (Lifted) from.
// The implementation `transformTo` will get an arbitrary input
// query and a means of extracting this `RE` from each row, and
// needs to turn it into a new query of `RV`s.  This is the most
// general form, and `RV` can be a type-level function of the
// input row type `RA`.
//
// This roughly corresponds to a profunctor lens container of
//  `(Query[RA, A, C], (RA => RE)) => Query[RV, V, C]`

abstract class QueryContentsLensFull[RE, E, DestShape[_, _] <: DestinationShape[_, _]] {
  // The query transformation API.  Concrete implementations must supply this.
  def transformTo[RA, A, C[_]](
      qwe: QWE[RA, A, C])(
      implicit _aShape: FlattishShape[RA, A])
  : Query[DestShape[RA, A]#RV, DestShape[RA, A]#V, C]

  // Type alias for a relevant [[QueryWithExtractor]]
  final type QWE[RA, A, C[_]] = QueryWithExtractor[RA, A, C, RE, E]
}

object QueryContentsLensFull {
  // Helper to produce the appropriate `DestinationShape`s needed.
  // This represents a transformation that maps into a constant row shape.
  final class To[_RV, _V] {
    trait From[RA, A] extends DestinationShape[RA, A] {
      type RV = _RV
      type V = _V
    }
  }
}