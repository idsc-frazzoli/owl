// code by jph
package ch.ethz.idsc.sophus.planar;

import ch.ethz.idsc.sophus.TensorMetric;
import ch.ethz.idsc.sophus.TensorNorm;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Mod;
import ch.ethz.idsc.tensor.sca.Sinc;

/** length of geodesic between p and q in SE(2) when projected to R^2
 * the projection is a circle segment */
public enum Se2ParametricDistance implements TensorMetric, TensorNorm {
  INSTANCE;
  // ---
  private static final Mod MOD_DISTANCE = Mod.function(Pi.TWO, Pi.VALUE.negate());
  private static final Scalar HALF = RealScalar.of(0.5);

  @Override // from TensorMetric
  public Scalar distance(Tensor p, Tensor q) {
    Scalar alpha = MOD_DISTANCE.apply(p.Get(2).subtract(q.get(2))).multiply(HALF);
    return Norm._2.between(p.extract(0, 2), q.extract(0, 2)).divide(Sinc.FUNCTION.apply(alpha));
  }

  @Override // from TensorNorm
  public Scalar norm(Tensor xya) {
    return distance(xya.map(Scalar::zero), xya);
  }
}