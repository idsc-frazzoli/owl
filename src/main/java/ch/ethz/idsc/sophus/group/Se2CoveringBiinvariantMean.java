// code by jph, ob
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.sophus.AffineQ;
import ch.ethz.idsc.sophus.math.BiinvariantMean;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.mat.Inverse;

// FIXME JPH implementation is not verified yet!
/* package */ enum Se2CoveringBiinvariantMean implements BiinvariantMean {
  INSTANCE;
  // ---
  private static final Scalar ZERO = RealScalar.ZERO;
  // ---

  @Override // from BiinvariantMeanInterface
  public Tensor mean(Tensor sequence, Tensor weights) {
    AffineQ.require(weights);
    Scalar amean = So2CoveringBiinvariantMean.INSTANCE.mean(sequence.get(Tensor.ALL, 2), weights);
    // make transformation s.t. mean rotation is zero and retransformation after taking mean
    Se2CoveringGroupElement transfer = new Se2CoveringGroupElement(Tensors.of(ZERO, ZERO, amean));
    Tensor transferred = Tensor.of(sequence.stream().map(transfer.inverse()::combine));
    Tensor invZ = Inverse.of(weights.dot(transferred.get(Tensor.ALL, 2).negate().map(So2Skew::of)));
    Tensor tmean = weights.dot(Tensor.of(transferred.stream().map( //
        xya -> invZ.dot(So2Skew.of(xya.Get(2).negate()).dot(RotationMatrix.of(xya.Get(2).negate()).dot(xya.extract(0, 2)))))));
    return transfer.combine(tmean.append(ZERO));
  }
}
