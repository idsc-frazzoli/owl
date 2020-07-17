// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Clips;

public class ClassificationResult implements Serializable {
  private final int label;
  private final Scalar confidence;

  /** @param label
   * @param confidence inside [0, 1] */
  public ClassificationResult(int label, Scalar confidence) {
    this.label = label;
    this.confidence = Clips.unit().requireInside(confidence);
  }

  /** @return */
  public int getLabel() {
    return label;
  }

  /** @return scalar in the interval [0, 1] */
  public Scalar getConfidence() {
    return confidence;
  }
}
