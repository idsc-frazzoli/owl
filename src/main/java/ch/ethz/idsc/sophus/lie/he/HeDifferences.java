// code by jph
package ch.ethz.idsc.sophus.lie.he;

import ch.ethz.idsc.sophus.lie.LieDifferences;

public enum HeDifferences {
  ;
  public static final LieDifferences INSTANCE = new LieDifferences( //
      HeGroup.INSTANCE, //
      HeExponential.INSTANCE);
}
