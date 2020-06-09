// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Scalar;

public class ClassificationResult implements Serializable {
  private final int label;
  private final Scalar confidence;

  /** @param label
   * @param confidence */
  public ClassificationResult(int label, Scalar confidence) {
    this.label = label;
    this.confidence = confidence;
  }

  public int getLabel() {
    return label;
  }

  /** @return scalar in the interval [0, 1] */
  public Scalar getConfidence() {
    return confidence;
  }
}
