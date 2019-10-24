// code by jph
package ch.ethz.idsc.sophus.math;

import java.util.function.Supplier;

import ch.ethz.idsc.tensor.Integers;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/Do.html">Do</a> */
public enum Do {
  ;
  /** @param supplier
   * @param n */
  public static <T> T of(Supplier<T> supplier, final int n) {
    Integers.requirePositive(n);
    T value = null;
    for (int index = 0; index < n; ++index)
      value = supplier.get();
    return value;
  }
}
