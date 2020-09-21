// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.Primitives;
import ch.ethz.idsc.tensor.red.ArgMax;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ class AccMaxClassifier extends Classifier {
  /** @param labels */
  public AccMaxClassifier(Tensor labels) {
    super(Primitives.toIntArray(labels));
  }

  @Override // from Classification
  public ClassificationResult result(Tensor weights) {
    Tensor arguments = Array.zeros(size);
    IntStream.range(0, labels.length) //
        .forEach(index -> arguments.set(weights.get(index)::add, labels[index]));
    int label = ArgMax.of(arguments);
    Scalar confidence = //
        Clips.unit().apply(RealScalar.TWO.subtract(Total.ofVector(arguments).divide(arguments.Get(label))));
    return new ClassificationResult(label, confidence);
  }
}