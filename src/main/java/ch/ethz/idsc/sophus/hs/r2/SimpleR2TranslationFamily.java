// code by jph
package ch.ethz.idsc.sophus.hs.r2;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

/** the term "family" conveys the meaning that the translation
 * depends on a single parameter, for instance time */
public final class SimpleR2TranslationFamily extends R2TranslationFamily {
  private final ScalarTensorFunction function;

  /** @param function maps a scalar to a vector in R^n */
  public SimpleR2TranslationFamily(ScalarTensorFunction function) {
    this.function = function;
  }

  @Override // from TranslationFamily
  public Tensor function_apply(Scalar scalar) {
    return function.apply(scalar);
  }
}
