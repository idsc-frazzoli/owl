// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.io.Serializable;
import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.red.ArgMin;

/** Hint:
 * does not implement {@link Serializable} because {@link Optional} does not implement {@link Serializable} */
/* package */ class ArgMinValue {
  public static ArgMinValue of(Tensor tensor) {
    int index = ArgMin.of(tensor);
    return new ArgMinValue(index, 0 <= index //
        ? Optional.of(tensor.Get(index))
        : Optional.empty());
  }

  // ---
  private final int index;
  private final Optional<Scalar> value;

  private ArgMinValue(int index, Optional<Scalar> value) {
    this.index = index;
    this.value = value;
  }

  public int index() {
    return index;
  }

  public Optional<Integer> index(Scalar threshold) {
    return value.isPresent() //
        && Scalars.lessEquals(value.get(), threshold) //
            ? Optional.of(index)
            : Optional.empty();
  }

  public Optional<Scalar> value() {
    return value;
  }

  public Optional<Scalar> value(Scalar threshold) {
    return value.isPresent() //
        && Scalars.lessEquals(value.get(), threshold) //
            ? value
            : Optional.empty();
  }
}
