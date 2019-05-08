// code by yn, jph
package ch.ethz.idsc.owl.glc.rl2;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.stream.Stream;

import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.math.VectorScalar;
import ch.ethz.idsc.owl.math.VectorScalars;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Entrywise;

/* package */ enum StaticHelper {
  ;
  /** @param collection
   * @return tensor with lowest costs entrywise */
  static Optional<Tensor> entrywiseMin(Stream<GlcNode> collection) {
    return collection //
        .map(GlcNode::merit) //
        .map(VectorScalar.class::cast) //
        .map(VectorScalar::vector) //
        .reduce(Entrywise.min());
  }

  // FIXME YN magic const
  private static final Scalar MERIT_EPS = RationalScalar.of(1, 100);

  static boolean isEqual(GlcNode next, RelaxedDomainQueue domainQueue) {
    // TODO YN check if close to existing nodes / assert if this is helpful
    Tensor nextMerit = VectorScalars.vector(next.merit());
    return domainQueue.stream() //
        .anyMatch(a -> VectorScalars.vector(a.merit()).subtract(nextMerit).stream() //
            .map(Scalar.class::cast).allMatch(v -> Scalars.lessThan(v, MERIT_EPS)));
  }

  /** @param collection
   * @param dimension
   * @return best node in collection along given dimension
   * @throws Exception if collection is empty */
  static GlcNode getMin(Collection<GlcNode> collection, int dimension) {
    return Collections.min(collection, new Comparator<GlcNode>() {
      @Override
      public int compare(GlcNode first, GlcNode second) {
        return Scalars.compare( //
            VectorScalars.at(first.merit(), dimension), //
            VectorScalars.at(second.merit(), dimension));
      }
    });
  }
}
