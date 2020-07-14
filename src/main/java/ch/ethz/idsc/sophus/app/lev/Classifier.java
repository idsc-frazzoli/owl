// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.io.Serializable;
import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.Primitives;
import ch.ethz.idsc.tensor.red.ArgMax;
import ch.ethz.idsc.tensor.red.ArgMin;
import ch.ethz.idsc.tensor.red.Total;

/* package */ abstract class Classifier implements Classification, Serializable {
  /** @param vector
   * @return */
  public static Classification argMin(Tensor vector) {
    ExactTensorQ.require(vector);
    return new Classifier(Primitives.toIntArray(vector)) {
      @Override
      public ClassificationResult result(Tensor weights) {
        int index = ArgMin.of(weights);
        return new ClassificationResult(labels[index], RealScalar.ONE.subtract(weights.Get(index).divide(Total.ofVector(weights))));
      }
    };
  }

  /** @param vector
   * @return */
  public static Classification argMax(Tensor vector) {
    ExactTensorQ.require(vector);
    return new Classifier(Primitives.toIntArray(vector)) {
      @Override
      public ClassificationResult result(Tensor weights) {
        int index = ArgMax.of(weights);
        return new ClassificationResult(labels[index], weights.Get(index).divide(Total.ofVector(weights)));
      }
    };
  }

  /** @param vector
   * @return */
  public static Classification accMax(Tensor vector) {
    ExactTensorQ.require(vector);
    return new Classifier(Primitives.toIntArray(vector)) {
      @Override
      public ClassificationResult result(Tensor weights) {
        Tensor arguments = Array.zeros(size);
        IntStream.range(0, labels.length) //
            .forEach(index -> arguments.set(weights.get(index)::add, labels[index]));
        int index = ArgMax.of(arguments);
        return new ClassificationResult(index, arguments.Get(index).divide(Total.ofVector(arguments)));
      }
    };
  }

  /***************************************************/
  final int[] labels;
  final int size;

  /** @param vector integer */
  private Classifier(int[] labels) {
    this.labels = labels;
    size = IntStream.of(this.labels).reduce(Math::max).orElse(0) + 1;
  }
}