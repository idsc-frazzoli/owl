// code by jph
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Series;
import ch.ethz.idsc.tensor.sca.Sqrt;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Roots.html">Roots</a> */
public enum Roots {
  ;
  private static final Scalar _2 = RealScalar.of(2);
  private static final Scalar _4 = RealScalar.of(4);

  /** attempts to find all roots of a polynomial
   * 
   * <pre>
   * Roots.of(coeffs).map(Series.of(coeffs)) == {0, 0, ...}
   * </pre>
   * 
   * @param coeffs of polynomial, for instance {a, b, c, d} represents
   * cubic polynomial a + b*x + c*x^2 + d*x^3
   * @return roots of polynomial as vector with length of that of coeffs minus one
   * @throws Exception if roots cannot be determined
   * @see Series */
  public static Tensor of(Tensor coeffs) {
    if (Scalars.isZero(coeffs.Get(0))) {
      Tensor roots = of(coeffs.extract(1, coeffs.length()));
      return roots.append(roots.Get(0).zero());
    }
    int last = coeffs.length() - 1;
    if (Scalars.isZero(coeffs.Get(last)))
      return of(coeffs.extract(0, last));
    switch (coeffs.length()) {
    case 2:
      return linear(coeffs);
    case 3:
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
    return Tensors.of(b.add(discr).divide(den), b.subtract(discr).divide(den));
  }
}
