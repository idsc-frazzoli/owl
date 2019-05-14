// code by yn, jph
package ch.ethz.idsc.owl.glc.rl2;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.VectorScalars;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Chop;

/* package */ enum StaticHelper {
  ;
  private static final Scalar MERIT_EPS = RationalScalar.of(1, 100);
  private static final Chop CHOP = Chop.below(0.01);
  // FIXME ASTOLL make as small as possible

  /** Checks whether the merit of node next is numerically close any merit of any other node within the domain queue.
   * This shall not be confused with the slack margin.
   * 
   * @param next
   * @param domainQueue
   * @return true if any match for closeness */
  static boolean isSimilar(GlcNode next, RelaxedPriorityQueue domainQueue) {
    return domainQueue.collection().stream() //
        .anyMatch(glcNode -> CHOP.close(glcNode.merit(), next.merit())); //
    // Tensor nextMerit = VectorScalars.vector(next.merit());
    // return domainQueue.collection().stream() //
    // .anyMatch(glcNode -> VectorScalars.vector(glcNode.merit()).subtract(nextMerit).stream() //
    // .map(Scalar.class::cast).allMatch(scalar -> Scalars.lessThan(scalar.abs(), MERIT_EPS)));
  }

  /** Returns number of nodes with similar merits to best merit within domain queue.
   * 
   * @param domainQueue
   * @return long */
  static long numberEquals(RelaxedPriorityQueue domainQueue) {
    Tensor bestMerit = VectorScalars.vector(domainQueue.peekBest().merit());
    // TODO use CHOP
    return domainQueue.collection().stream() //
        .filter(glcNode -> VectorScalars.vector(glcNode.merit()).subtract(bestMerit).stream() //
            .map(Scalar.class::cast).allMatch(scalar -> Scalars.lessThan(scalar.abs(), MERIT_EPS)))
        .count();
  }
}
