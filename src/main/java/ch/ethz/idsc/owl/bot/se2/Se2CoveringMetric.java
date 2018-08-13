// code by jph
package ch.ethz.idsc.owl.bot.se2;

import ch.ethz.idsc.owl.math.TensorMetric;
import ch.ethz.idsc.owl.math.map.Se2CoveringExponential;
import ch.ethz.idsc.owl.math.map.Se2CoveringGroupAction;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm;

/** induced metric
 * simply ||log(p^-1.q)||
 * mixes units */
public enum Se2CoveringMetric implements TensorMetric {
  INSTANCE;
  // ---
  @Override // from TensorMetric
  public Scalar distance(Tensor p, Tensor q) {
    return Norm._2.of(Se2CoveringExponential.INSTANCE.log( //
        new Se2CoveringGroupAction(p).inverse().combine(q)));
  }
}
