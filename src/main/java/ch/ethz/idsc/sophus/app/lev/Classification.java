// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.Primitives;
import ch.ethz.idsc.tensor.red.ArgMax;

/* package */ abstract class Classification implements LabelInterface {
  /** @param vector
   * @return */
  public static LabelInterface argMax(Tensor vector) {
    ExactTensorQ.require(vector);
    return new Classification(Primitives.toIntArray(vector)) {
      @Override
      public int label(Tensor weights) {
        return labels[ArgMax.of(weights)];
      }
    };
  }

  /** @param vector
   * @return */
  public static LabelInterface accMax(Tensor vector) {
    ExactTensorQ.require(vector);
    return new Classification(Primitives.toIntArray(vector)) {
      @Override
      public int label(Tensor weights) {
        Tensor arguments = Array.zeros(size);
        IntStream.range(0, labels.length) //
            .forEach(index -> arguments.set(weights.get(index)::add, labels[index]));
        return ArgMax.of(arguments);
      }
    };
  }

  /***************************************************/
  final int[] labels;
  final int size;

  /** @param vector integer */
  private Classification(int[] labels) {
    this.labels = labels;
    size = IntStream.of(this.labels).reduce(Math::max).orElse(0) + 1;
  }
}