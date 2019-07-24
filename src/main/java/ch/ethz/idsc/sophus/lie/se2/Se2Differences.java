// code by jph
package ch.ethz.idsc.sophus.lie.se2;

import ch.ethz.idsc.sophus.lie.LieDifferences;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringExponential;

public enum Se2Differences {
  ;
  public static final LieDifferences INSTANCE = new LieDifferences( //
      Se2Group.INSTANCE, //
      Se2CoveringExponential.INSTANCE);
}
