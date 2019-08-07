// code by jph
package ch.ethz.idsc.sophus.lie.se2c;

import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm;

/** mixes units
 * 
 * ||log(p^-1.q)|| */
/* package */ enum Se2CoveringMetric implements TensorMetric {
  INSTANCE;
  // ---
  @Override // from TensorMetric
  public Scalar distance(Tensor p, Tensor q) {
    return Norm._2.of(Se2CoveringExponential.INSTANCE.log( //
        new Se2CoveringGroupElement(p).inverse().combine(q)));
  }
}
