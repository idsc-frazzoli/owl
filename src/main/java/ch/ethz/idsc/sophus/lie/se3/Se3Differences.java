// code by jph
package ch.ethz.idsc.sophus.lie.se3;

import ch.ethz.idsc.sophus.lie.LieDifferences;
import ch.ethz.idsc.sophus.lie.gl.LinearGroup;

public class Se3Differences extends LieDifferences {
  public static final LieDifferences INSTANCE = new Se3Differences();

  // ---
  private Se3Differences() {
    super(LinearGroup.INSTANCE, Se3Exponential.INSTANCE);
  }
}
