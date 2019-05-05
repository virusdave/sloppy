package sloppy

// A type-level function from `(RA, A) => (RV, V)`, used
// by Query optics to compute the result query shape after
// a transformation is made.
trait DestinationShape[RA, A] {
  type RV
  type V
}
