// code by jph
package ch.ethz.idsc.sophus.group;

import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.Tensor;

/* package */ enum TestHelper {
  ;
  static Tensor order(Tensor tensor, int[] index) {
    return Tensor.of(IntStream.of(index).mapToObj(tensor::get));
  }
}
