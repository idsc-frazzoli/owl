// code by yn, jph
package ch.ethz.idsc.owl.glc.rl2;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.VectorScalars;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;

/* package */ enum StaticHelper {
  ;
  private static final Scalar MERIT_EPS = RationalScalar.of(1, 100);
  // FIXME ASTOLL make as small as possible

  /** Checks whether the merit of node next is numerically close any merit of any other node within the domain queue.
   * This shall not be confused with the slack margin.
   * 
   * @param next
   * @param domainQueue
   * @return True if any match for closeness */
  static boolean isNonBeneficial(GlcNode next, RelaxedPriorityQueue domainQueue) {
    Tensor nextMerit = VectorScalars.vector(next.merit());
    return domainQueue.collection().stream() //
        .anyMatch(a -> VectorScalars.vector(a.merit()).subtract(nextMerit).stream() //
            .map(Scalar.class::cast).allMatch(v -> Scalars.lessThan(v, MERIT_EPS)));
  }

  /** Returns number of nodes with similar merits to best merit within domain queue.
   * 
   * @param domainQueue
   * @return long */
  static long numberEquals(RelaxedPriorityQueue domainQueue) {
    Tensor bestMerit = VectorScalars.vector(domainQueue.peekBest().merit());
    return domainQueue.collection().stream().filter(a -> VectorScalars.vector(a.merit()).subtract(bestMerit).stream() //
        .map(Scalar.class::cast).allMatch(v -> Scalars.lessThan(v.abs(), MERIT_EPS))).count();
  }
}
