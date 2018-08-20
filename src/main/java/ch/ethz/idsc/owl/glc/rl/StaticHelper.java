// code by yn, jph
package ch.ethz.idsc.owl.glc.rl;

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

  // TODO magic const
  private static final Scalar MERIT_EPS = RationalScalar.of(1, 100);

  static boolean isEqual(GlcNode next, RLDomainQueue domainQueue) {
    // TODO check if close to existing nodes / assert if this is helpful
    Tensor nextMerit = VectorScalars.vector(next.merit());
    return domainQueue.stream() //
        .anyMatch(a -> VectorScalars.vector(a.merit()).subtract(nextMerit) //
            .stream().map(Tensor::Get).allMatch(v -> Scalars.lessThan(v, MERIT_EPS)));
  }
}
