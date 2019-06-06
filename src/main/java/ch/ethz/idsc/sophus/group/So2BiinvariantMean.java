// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.sophus.AffineQ;
import ch.ethz.idsc.sophus.math.BiinvariantMeanInterface;
import ch.ethz.idsc.sophus.planar.ArcTan2D;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.AngleVector;

public enum So2BiinvariantMean implements BiinvariantMeanInterface {
  INSTANCE;
  // ---
  @Override // from BiinvariantMeanInterface
  public Tensor mean(Tensor sequence, Tensor weights) {
    return ArcTan2D.of(AffineQ.require(weights).dot(sequence.map(AngleVector::of)));
  }
}
