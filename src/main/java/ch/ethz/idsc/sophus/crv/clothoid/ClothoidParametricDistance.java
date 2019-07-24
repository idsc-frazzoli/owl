// code by gjoel
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.sophus.math.TensorNorm;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Abs;

public enum ClothoidParametricDistance implements TensorMetric, TensorNorm {
  INSTANCE;

  @Override // from TensorMetric
  public Scalar distance(Tensor p, Tensor q) {
    Tensor diff = Extract2D.FUNCTION.apply(q.subtract(p));
    Scalar il = new ClothoidCurve3(p, q).il(RealScalar.ONE);
    return Norm._2.ofVector(diff).divide(Abs.of(il));
  }

  @Override // from TensorNorm
  public Scalar norm(Tensor xya) {
    return distance(xya.map(Scalar::zero), xya);
  }
}
