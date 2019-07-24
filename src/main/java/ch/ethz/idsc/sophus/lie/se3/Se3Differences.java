// code by jph
package ch.ethz.idsc.sophus.lie.se3;

import ch.ethz.idsc.sophus.lie.LieDifferences;
import ch.ethz.idsc.sophus.lie.gl.LinearGroup;

public enum Se3Differences {
  ;
  public static final LieDifferences INSTANCE = new LieDifferences( //
      LinearGroup.INSTANCE, //
      Se3Exponential.INSTANCE);
}
