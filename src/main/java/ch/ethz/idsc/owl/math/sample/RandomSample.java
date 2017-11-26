// code by jph
package ch.ethz.idsc.owl.math.sample;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;

/** inspired by
 * <a href="https://reference.wolfram.com/language/ref/RandomSample.html">RandomSample</a> */
public enum RandomSample {
  ;
  public static Tensor of(RandomSampleInterface randomSampleInterface) {
    return randomSampleInterface.randomSample();
  }

  public static Tensor of(RandomSampleInterface randomSampleInterface, int length) {
    return Array.of(list -> randomSampleInterface.randomSample(), length);
  }
}
