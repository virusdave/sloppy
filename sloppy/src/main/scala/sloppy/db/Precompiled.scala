package sloppy.db

import scala.util.Try
import slick.lifted.{Compilable, Compiled, CompilersMixin}
import slick.basic.BasicProfile

object Precompiled {
  /**
    * Create a new `Compiled` value for a raw value that is `Compilable`.
    * This will also precompute (not just lazily cache) the compiled version of all statement types.
    */
  @inline def apply[V, C <: Compiled[V]](raw: V)(implicit compilable: Compilable[V, C], driver: BasicProfile): C = {
    val precompiled = Compiled(raw)

    // Attempt to force each `lazy val` to be evaluated, which forces compilation of the underlying SQL
    // We don't care about the actual values here, and the computations can throw in certain
    // circumstances (for instance, and insert on a multi-table join query), so we just discard the
    // error if so.
    Try { precompiled.asInstanceOf[CompilersMixin] } map { q =>
      Try(q.compiledQuery)
      Try(q.compiledDelete)
      Try(q.compiledInsert)
      Try(q.compiledUpdate)
    }

    precompiled
  }
}