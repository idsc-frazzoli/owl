// code by jph
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Series;
import ch.ethz.idsc.tensor.sca.Sqrt;

/** Not entirely consistent with Mathematica for the case
 * Mathematica::Roots[a == 0, x] == false
 * Tensor::Roots[a == 0, x] == {}
 * 
 * <p>inspired by
 * <a href="https://reference.wolfram.com/language/ref/Roots.html">Roots</a> */
public enum Roots {
  ;
  private static final Scalar N1_2 = RationalScalar.HALF.negate();

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
    case 1:
      return Tensors.empty();
    case 2: // a + b*x == 0
      return linear(coeffs);
    case 3: // a + b*x + c*x^2 == 0
      return quadratic(coeffs);
    }
    throw TensorRuntimeException.of(coeffs);
  }

  /** @param coeffs {a, b} representing a + b*x == 0
   * @return vector of length 1 */
  private static Tensor linear(Tensor coeffs) {
    return Tensors.of(coeffs.Get(0).divide(coeffs.Get(1)).negate());
  }

  /** @param coeffs {a, b, c} representing a + b*x + c*x^2 == 0
   * @return vector of length 2 with the roots as entries */
  private static Tensor quadratic(Tensor coeffs) {
    Scalar c = coeffs.Get(2);
    Scalar p = coeffs.Get(1).divide(c).multiply(N1_2);
    Scalar p2 = p.multiply(p);
    Scalar q = coeffs.Get(0).divide(c);
    Scalar discr = Sqrt.of(p2.subtract(q));
    return Tensors.of(p.add(discr), p.subtract(discr));
  }
}
