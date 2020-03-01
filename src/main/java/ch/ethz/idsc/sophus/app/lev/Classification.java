// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.Primitives;
import ch.ethz.idsc.tensor.red.ArgMax;

/* package */ class Classification {
  private final int[] labels;
  private final int size;

  /** @param vector integer */
  public Classification(Tensor vector) {
    ExactTensorQ.require(vector);
    this.labels = Primitives.toIntArray(vector);
    size = IntStream.of(this.labels).reduce(Math::max).orElse(0) + 1;
  }

  /** @param weights
   * @return */
  public int getAccumulatedMax(Tensor weights) {
    Tensor arguments = Array.zeros(size);
    IntStream.range(0, labels.length) //
        .forEach(index -> arguments.set(weights.get(index)::add, labels[index]));
    return ArgMax.of(arguments);
  }

  public int getArgMax(Tensor weights) {
    return labels[ArgMax.of(weights)];
  }

  public int size() {
    return size;
  }

  public IntStream labelIndices(int label) {
    return IntStream.range(0, labels.length).filter(index -> labels[index] == label);
  }
}