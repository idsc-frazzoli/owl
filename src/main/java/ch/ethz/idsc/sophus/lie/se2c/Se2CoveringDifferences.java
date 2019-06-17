// code by jph
package ch.ethz.idsc.sophus.lie.se2c;

import ch.ethz.idsc.sophus.lie.LieDifferences;

public class Se2CoveringDifferences extends LieDifferences {
  public static final LieDifferences INSTANCE = new Se2CoveringDifferences();

  // ---
  private Se2CoveringDifferences() {
    super(Se2CoveringGroup.INSTANCE, Se2CoveringExponential.INSTANCE);
  }
}
