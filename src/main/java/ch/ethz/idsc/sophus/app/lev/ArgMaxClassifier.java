// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.util.Optional;
import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.io.Primitives;
import ch.ethz.idsc.tensor.red.ArgMax;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ class ArgMaxClassifier extends Classifier {
  /** @param labels */
  public ArgMaxClassifier(Tensor labels) {
    super(Primitives.toIntArray(labels));
  }

  @Override // from Classification
  public ClassificationResult result(Tensor weights) {
    if (weights.length() != labels.length)
      throw TensorRuntimeException.of(weights);
    // ---
    int index = ArgMax.of(weights);
    int label = labels[index];
    Optional<Scalar> optional = IntStream.range(0, labels.length) //
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
}