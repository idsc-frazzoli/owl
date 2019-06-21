// code by jph
package ch.ethz.idsc.tensor;

import java.util.OptionalInt;
import java.util.stream.IntStream;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/FirstPosition.html">FirstPosition</a> */
// TODO JPH TENSOR 074 obsolete
public enum FirstPosition {
  ;
  /** @param tensor non-null
   * @param element non-null
   * @return index with tensor.get(index).equals(element) or OptionalInt.empty() */
  public static OptionalInt of(Tensor tensor, Tensor element) {
    ScalarQ.thenThrow(tensor);
    return IntStream.range(0, tensor.length()) //
        .filter(index -> element.equals(tensor.get(index))) //
        .findFirst();
  }
}
