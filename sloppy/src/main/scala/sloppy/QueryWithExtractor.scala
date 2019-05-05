package sloppy

import slick.lifted.Query

//*********************************************************
// This type is meant to represent "An arbitrary query,
// combined with a way to extract particular values
// we care about from each row", in preparation for
// feeding that pair into some generic query transformer.
//*********************************************************
trait QueryWithExtractor[RA, A, C[_], RE] {
  def query: Query[RA, A, C]
  def extract: RA => RE

  implicit def _aShape: FlattishShape[RA, A]

  // Helper methods for composition of query transformations, and chaining calls at call sites.
  def extracted[E](implicit _eShape: FlattishShape[RE, E]): Query[RE, E, C] = query.map(extract)

  def transform[S[_,_] <: DestinationShape[_, _]](
      qa: QueryContentsLens[RE, S])
  : Query[S[RA, A]#RV, S[RA, A]#V, C] =
    qa.transformTo[
      RA, A,
      C](this)

  // Convenience methods for easier reading of code
  final def append[RV, V](qa: QueryContentsAppender[RE, RV, V]): Query[(RA, RV), (A, V), C] = transform(qa)
  final def filter(qa: QueryContentsFilter[RE]): Query[RA, A, C] = transform(qa)
}

