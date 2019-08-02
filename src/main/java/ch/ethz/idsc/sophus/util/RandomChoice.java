// code by jph
package ch.ethz.idsc.sophus.util;

import java.util.List;
import java.util.Random;

import ch.ethz.idsc.tensor.Tensor;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/RandomChoice.html">RandomChoice</a> */
// TODO JPH SUBARE 033 obsolete
public enum RandomChoice {
  ;
  private static final Random RANDOM = new Random();

  /** @param list
   * @return */
  public static <T> T of(List<T> list) {
    return list.get(RANDOM.nextInt(list.size()));
  }

  /** @param tensor
   * @return */
  @SuppressWarnings("unchecked")
  public static <T extends Tensor> T of(Tensor tensor) {
    return (T) tensor.get(RANDOM.nextInt(tensor.length()));
  }
}
