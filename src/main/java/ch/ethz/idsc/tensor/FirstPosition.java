// code by jph
package ch.ethz.idsc.tensor;

import java.util.stream.IntStream;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/FirstPosition.html">FirstPosition</a> */
public enum FirstPosition {
  ;
  /** @param tensor
   * @param elem
   * @return index of tensor */
  public static int of(Tensor tensor, Tensor elem) {
    return IntStream.range(0, tensor.length()) //
        .filter(index -> tensor.get(index).equals(elem)) //
        .findFirst().getAsInt();
  }
}
