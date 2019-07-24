// code by jph
package ch.ethz.idsc.sophus.lie.se2c;

import ch.ethz.idsc.sophus.lie.LieDifferences;

public enum Se2CoveringDifferences {
  ;
  public static final LieDifferences INSTANCE = //
      new LieDifferences(Se2CoveringGroup.INSTANCE, Se2CoveringExponential.INSTANCE);
}
