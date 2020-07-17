// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.io.Serializable;
import java.util.Optional;
import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.Primitives;
import ch.ethz.idsc.tensor.red.ArgMax;
import ch.ethz.idsc.tensor.red.ArgMin;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ abstract class Classifier implements Classification, Serializable {
  /** @param vector
   * @return */
  public static Classification argMin(Tensor vector) {
    ExactTensorQ.require(vector);
    return new Classifier(Primitives.toIntArray(vector)) {
      @Override // from Classification
      public ClassificationResult result(Tensor weights) {
        if (weights.length() != vector.length())
          throw TensorRuntimeException.of(weights);
        // ---
        int index = ArgMin.of(weights);
        int label = labels[index];
        Optional<Scalar> optional = IntStream.range(0, vector.length()) //
            .filter(i -> label != labels[i]) //
            .mapToObj(weights::Get) //
            .reduce(Min::of);
        Scalar confidence = optional.isPresent() //
            ? Clips.unit().apply(RealScalar.ONE.subtract(weights.Get(index).divide(optional.get())))
            : RealScalar.ONE;
        return new ClassificationResult(label, confidence);
      }
    };
  }

  /** @param vector
   * @return */
  public static Classification argMax(Tensor vector) {
    ExactTensorQ.require(vector);
    return new Classifier(Primitives.toIntArray(vector)) {
      @Override // from Classification
      public ClassificationResult result(Tensor weights) {
        if (weights.length() != vector.length())
          throw TensorRuntimeException.of(weights);
        // ---
        int index = ArgMax.of(weights);
        int label = labels[index];
        Optional<Scalar> optional = IntStream.range(0, vector.length()) //
            .filter(i -> label != labels[i]) //
            .mapToObj(weights::Get) //
            .reduce(Max::of);
        // clip shouldn't be necessary but exists for negative
        // weights and to correct numerical imprecision
        Scalar confidence = optional.isPresent() //
            ? Clips.unit().apply(RealScalar.ONE.subtract(optional.get().divide(weights.Get(index))))
            : RealScalar.ONE;
        return new ClassificationResult(label, confidence);
      }
    };
  }

  private static final Scalar TWO = RealScalar.of(2);

  /** @param vector
   * @return */
  public static Classification accMax(Tensor vector) {
    ExactTensorQ.require(vector);
    return new Classifier(Primitives.toIntArray(vector)) {
      @Override // from Classification
      public ClassificationResult result(Tensor weights) {
        Tensor arguments = Array.zeros(size);
        IntStream.range(0, labels.length) //
            .forEach(index -> arguments.set(weights.get(index)::add, labels[index]));
        int label = ArgMax.of(arguments);
        Scalar confidence = //
            Clips.unit().apply(TWO.subtract(Total.ofVector(arguments).divide(arguments.Get(label))));
        return new ClassificationResult(label, confidence);
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