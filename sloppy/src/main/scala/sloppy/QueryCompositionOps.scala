package sloppy

import slick.lifted.Query

//*********************************************************************************************************
// Utilities for monkey-patching arbitrary query types, often in preparation for transforming them in
// useful ways.
//*********************************************************************************************************
object QueryCompositionOps {
  implicit class GenericQueryCompositionOps[A, B, C[_]](val query: Query[A, B, C]) {
    def byUsing[RE](
        extract: A => RE)(
        implicit aShape: FlattishShape[A, B])
    : QueryWithExtractor[A, B, C, RE] =
      new QueryWithBaseExtractor[A, B, C, RE](query, extract)

    def asIs(implicit aShape: FlattishShape[A, B]): QueryWithExtractor[A, B, C, A] = byUsing(identity)
  }

  // TODO(dave): Do we ever want a non-lifted extractor?
  private class QueryWithBaseExtractor[RA, A, C[_], RE](
      override val query: Query[RA, A, C],
      override val extract: RA => RE)(
      implicit override val _aShape: FlattishShape[RA, A])
    extends QueryWithExtractor[RA, A, C, RE]
}
