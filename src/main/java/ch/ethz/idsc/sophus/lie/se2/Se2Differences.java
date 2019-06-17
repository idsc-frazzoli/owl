// code by jph
package ch.ethz.idsc.sophus.lie.se2;

import ch.ethz.idsc.sophus.lie.LieDifferences;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringExponential;

public class Se2Differences extends LieDifferences {
  public static final LieDifferences INSTANCE = new Se2Differences();

  // ---
  private Se2Differences() {
    super(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE);
  }
}
