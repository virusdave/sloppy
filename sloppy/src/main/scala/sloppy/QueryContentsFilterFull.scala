package sloppy

import slick.lifted.{Query, Rep}

// Given an `RE` (which should be LIFTED) from a query's rows, produce
// a query of the same shape (`RA`, `A`, `C`) that represents a filtering
// (or expansion, perhaps) of a query.
//
// This seems to be a common, useful pattern for capturing small
// but meaningful snippets.  An example might be filtering an input
// query's result set based upon entitlements or access permissions.
//
// This roughly corresponds to:
//  `(Query[RA, A, C], (RA => RE)) => Query[RA, A, C]`
abstract class QueryContentsFilterFull[RE, E] extends QueryContentsLensFull[RE, E, QueryContentsFilterFull.Filter] {
  def filter[RA, A, C[_]](
      qwe: QWE[RA, A, C])(
      implicit _aShape: FlattishShape[RA, A])
  : Query[RA, A, C]

  final override def transformTo[RA, A, C[_]](
      qwe: QWE[RA, A, C])(
      implicit _aShape: FlattishShape[RA, A])
  : Query[RA, A, C] = filter(qwe)
}

object QueryContentsFilterFull {
  // Helper to produce the appropriate `DestinationShape`s needed.
  // This represents a transformation that filters rows without changing
  // the row shape.
  trait Filter[RA, A] extends DestinationShape[RA, A] {
    type RV = RA
    type V = A
  }
}