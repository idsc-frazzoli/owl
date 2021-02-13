// code by jph
package ch.ethz.idsc.owl.bot.psu;

import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.nrm.VectorNorm2;

/* package */ enum PsuMetric implements TensorMetric {
  INSTANCE;

  @Override // from TensorMetric
  public Scalar distance(Tensor p, Tensor q) {
    // mix of units [rad] and [rad/sec]
    return VectorNorm2.of(PsuWrap.INSTANCE.difference(p, q));
  }
}
