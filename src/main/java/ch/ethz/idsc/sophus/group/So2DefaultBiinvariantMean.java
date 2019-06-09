// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.sophus.AffineQ;
import ch.ethz.idsc.sophus.planar.ArcTan2D;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.AngleVector;

/** biinvariant mean defined globally on SO(2) for arbitrary weights
 * 
 * invariant under simultaneous permutation of control point sequence and weight vector
 * 
 * elements of SO(2) are represented as scalars */
public enum So2DefaultBiinvariantMean implements So2BiinvariantMean {
  INSTANCE;
  // ---
  @Override // from So2BiinvariantMean
  public Scalar mean(Tensor sequence, Tensor weights) {
    return ArcTan2D.of(AffineQ.require(weights).dot(sequence.map(AngleVector::of)));
  }
}
