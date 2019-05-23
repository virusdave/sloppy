package sloppy

import slick.lifted.Query

//*********************************************************************************************************
// Utilities for monkey-patching arbitrary query types, often in preparation for transforming them in
// useful ways.
//*********************************************************************************************************
object QueryCompositionOps {
  implicit class GenericQueryCompositionOps[RA, A, C[_]](val query: Query[RA, A, C]) {
    def byUsing[RE, E](
        extract: RA => RE)(
        implicit aShape: FlattishShape[RA, A],
        eShape: FlattishShape[RE, E])
    : QueryWithExtractor[RA, A, C, RE, E] =
      new QueryWithBaseExtractor[RA, A, C, RE, E](query, extract)

    def asIs(implicit aShape: FlattishShape[RA, A]): QueryWithExtractor[RA, A, C, RA, A] = byUsing(identity)
  }

  // TODO(dave): Do we ever want a non-lifted extractor?
  private class QueryWithBaseExtractor[RA, A, C[_], RE, E](
      override val query: Query[RA, A, C],
      override val extract: RA => RE)(
      implicit override val _aShape: FlattishShape[RA, A],
      override val _eShape: FlattishShape[RE, E])
    extends QueryWithExtractor[RA, A, C, RE, E]
}
