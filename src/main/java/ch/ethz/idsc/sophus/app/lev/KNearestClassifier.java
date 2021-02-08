// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.util.Map;
import java.util.Map.Entry;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.alg.Ordering;
import ch.ethz.idsc.tensor.io.Primitives;
import ch.ethz.idsc.tensor.red.Tally;

/* package */ class KNearestClassifier extends Classifier {
  private final int k;

  /** @param labels
   * @param k */
  public KNearestClassifier(Tensor labels, int k) {
    super(Primitives.toIntArray(labels));
    this.k = k;
  }

  @Override // from Classification
  public ClassificationResult result(Tensor weights) {
    if (weights.length() != labels.length)
      throw TensorRuntimeException.of(weights);
    // ---
    // TODO this is not finished yet!
    Map<Tensor, Long> map = Tally.of(Ordering.INCREASING.stream(weights) //
        .limit(k) //
        .map(i -> labels[i]) //
        .map(RealScalar::of));
    Scalar lab = null;
    int cmp = 0;
    for (Entry<Tensor, Long> entry : map.entrySet())
      if (cmp < entry.getValue())
        lab = (Scalar) entry.getKey();
    return new ClassificationResult(lab.number().intValue(), RealScalar.of(0.5));
  }
}