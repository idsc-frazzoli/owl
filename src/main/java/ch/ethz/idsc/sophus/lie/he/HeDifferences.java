// code by jph
package ch.ethz.idsc.sophus.lie.he;

import ch.ethz.idsc.sophus.lie.LieDifferences;

public class HeDifferences extends LieDifferences {
  public static final LieDifferences INSTANCE = new HeDifferences();

  // ---
  private HeDifferences() {
    super(HeGroup.INSTANCE, HeExponential.INSTANCE);
  }
}
