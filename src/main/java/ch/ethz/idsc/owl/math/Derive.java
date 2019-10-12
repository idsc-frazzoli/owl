// code by jph
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;

// TODO TENSOR 080 obsolete
public enum Derive {
  ;
  /** @param coeffs
   * @return coefficients of polynomial that is the derivative of the polynomial defined by given coeffs */
  public static Tensor of(Tensor coeffs) {
    int length = coeffs.length();
    return length == 0 //
        ? Tensors.empty()
        : Range.of(1, length).pmul(coeffs.extract(1, length));
  }
}
