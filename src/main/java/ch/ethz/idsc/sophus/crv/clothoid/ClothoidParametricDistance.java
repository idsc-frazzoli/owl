// code by gjoel
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.sophus.math.TensorNorm;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public enum ClothoidParametricDistance implements TensorMetric, TensorNorm {
  INSTANCE;
  // ---
  @Override // from TensorMetric
  public Scalar distance(Tensor p, Tensor q) {
    ClothoidCurve clothoidCurve = new ClothoidCurve3(p, q);
    return clothoidCurve.v().divide(clothoidCurve.il(RealScalar.ONE).abs());
  }

  @Override // from TensorNorm
  public Scalar norm(Tensor xya) {
    return distance(xya.map(Scalar::zero), xya);
  }
}
