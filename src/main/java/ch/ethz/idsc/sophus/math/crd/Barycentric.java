// code by jph
package ch.ethz.idsc.sophus.math.crd;

import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.Norm2Squared;
import ch.ethz.idsc.tensor.sca.Sqrt;

public enum Barycentric implements TensorMetric {
  WACHSPRESS() {
    private final Scalar TWO = RealScalar.of(2);

    @Override
    public Scalar distance(Tensor p, Tensor q) {
      return Norm2Squared.between(p, q).subtract(TWO);
    }
  }, //
  MEAN_VALUE() {
    @Override
    public Scalar distance(Tensor p, Tensor q) {
      Scalar norm2 = Norm2Squared.between(p, q);
      Scalar norm = Sqrt.FUNCTION.apply(norm2);
      return norm2.subtract(norm.add(norm));
    }
  }, //
  DISCRETE_HARMONIC() {
    @Override
    public Scalar distance(Tensor p, Tensor q) {
      return RealScalar.ZERO;
    }
  }, //
  ;
}
