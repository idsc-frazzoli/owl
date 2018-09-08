// code by jph
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Sqrt;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Roots.html">Roots</a> */
public enum Roots {
  ;
  private static final Scalar _2 = RealScalar.of(2);
  private static final Scalar _4 = RealScalar.of(4);

  public static Tensor of(Tensor coeffs) {
    switch (coeffs.length()) {
    case 2:
      return linear(coeffs);
    case 3:
      if (Scalars.isZero(coeffs.Get(0)))
        return of(coeffs.extract(0, 2));
      return quadratic(coeffs);
    }
    throw TensorRuntimeException.of(coeffs);
  }

  private static Tensor linear(Tensor coeffs) {
    Scalar a = coeffs.Get(0);
    Scalar b = coeffs.Get(1);
    return a.divide(b).negate();
  }

  private static Tensor quadratic(Tensor coeffs) {
    Scalar a = coeffs.Get(0);
    Scalar b = coeffs.Get(1);
    Scalar c = coeffs.Get(2);
    Scalar discr = Sqrt.of(b.multiply(b).subtract(a.multiply(c).multiply(_4)));
    Scalar den = c.multiply(_2).negate();
    return Tensors.of( //
        b.add(discr).divide(den), //
        b.subtract(discr).divide(den));
  }
}
