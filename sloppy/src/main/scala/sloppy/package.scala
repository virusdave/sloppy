import slick.lifted.{FlatShapeLevel, Rep, Shape}

//*********************************************************************************************************
// Types, utilities, aliases, etc for query DSL snippet composition.
//
// NOTA BENE: these are not finalized, so the shapes, names, and even location of these types and objects
// will probably change more than once in the near future.  Being that it's also unfinished, there are a
// bunch of TODOs floating around that may or may not be implemented, but are items worth remembering and
// considering for possible future enhancements.
//
// These types are meant to ease the pain of writing small, generic slick DSL snippets that do small
// "business-logic-y things" to generic queries, allowing simple, known-good implementations of application
// logic or functionality to be composed and reused in useful ways.
//
// A note on type parameters:
// Slick's use of type parameters is pretty opaque in many ways, and unfortunately that tends to leak
// through into these types as well.  As such, the following naming conventions are in use here to help
// the user remember what's what:
//
//  - A, B, C[_]: These are the type parameters of an arbitrary `Query` object.  They should never need
//    to be specified by the user; always inferred by compiler.
//  - Rx (for some letter `x`): A LIFTED type (should generally look like `Rep[Z]` for some Z, although
//    slick has other lifted types as well, such as the types representing fully-lifted whole-table-rows,
//    so this shape is a guideline, not a rule).  The `R` is meant to evoke the mental image "Rep" (== Lifted).
//  - E, RE: A type (base or lifted) being EXTRACTED from an arbitrary query that we're operating on.
//    Generally, we don't want to care what a query contains, so long as we know how to get values we DO
//    care about out of each row.  These types represent the types of the values we care about.  The `E`
//    is meant to evoke the mental image "Extracted Value".
//  - V, RV: A type (base or lifted) being PRODUCED for an arbitrary query's rows that we're somehow computing
//    from the extracted values for each row.  These might be the result of some join against a different
//    table, for instance, or a summary computation based upon the extracted value.  The `V` is meant to
//    evoke the mental image "Value".
//*********************************************************************************************************


// Reduce keystrokes, but this is probably too constrained and should be relaxed
// to general [[Shape]]s that are at `FlatShapeLevel` or below.
//
// NB: Using an existential type upper-bounded by FlatShapeLevel seems like it SHOULD be correct
// here, but triggers major headaches in implicit resolution.  Some of these seem to be solved
// in dotty: https://github.com/scala/bug/issues/10420
package object sloppy {
  type FlattishShape[RA, A] = Shape[/*_ <: */FlatShapeLevel, RA, A, RA]

  // Convenience type aliases where the connection between Lifted and Non-lifted types is trivial (Lift == Rep[Base])
  type QueryContentsLens[E, DestShape[_, _] <: DestinationShape[_, _]] = QueryContentsLensFull[Rep[E], E, DestShape]
  type QueryContentsAppender[E, RV, V] = QueryContentsAppenderFull[Rep[E], E, RV, V]
  type QueryContentsFilter[E] = QueryContentsFilterFull[Rep[E], E]
}

// Remaining items TODO:
// 1) Generic "drop LHS from a Tuple2" implementation. Requires acquiring the component types of the
//    non-lifted tuple of a query, which we know must exist because we have a tuple extractor already.
// 2) Generic optional lifter for appenders: (QA[RE, RA, A] => QA[Option[RE], Option[RA], Option[A]])
