// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.io.Serializable;
import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.Tensor;

public abstract class Classifier implements Classification, Serializable {
  private static final long serialVersionUID = 9128362149180675054L;

  /** @param labels
   * @return */
  public static Classification argMin(Tensor labels) {
    return new ArgMinClassifier(ExactTensorQ.require(labels));
  }

  /** @param labels
   * @return */
  public static Classification argMax(Tensor labels) {
    return new ArgMaxClassifier(ExactTensorQ.require(labels));
  }

  /** @param labels
   * @return */
  public static Classification accMax(Tensor labels) {
    return new AccMaxClassifier(ExactTensorQ.require(labels));
  }

  /***************************************************/
  protected final int[] labels;
  protected final int size;

  /** @param labels */
  protected Classifier(int[] labels) {
    this.labels = labels;
    size = IntStream.of(this.labels).reduce(Math::max).orElse(0) + 1;
  }
}