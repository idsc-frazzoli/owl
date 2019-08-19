// code by jph
package ch.ethz.idsc.sophus.lie;

import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.Tensor;

public enum BiinvariantMeanTestHelper {
  ;
  /** used to permute input to {@link BiinvariantMean}: sequence and weights */
  public static Tensor order(Tensor tensor, int[] index) {
    return Tensor.of(IntStream.of(index).mapToObj(tensor::get));
  }
}
