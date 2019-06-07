// code by jph
package ch.ethz.idsc.sophus.group;

public class Se2Differences extends LieDifferences {
  public static final LieDifferences INSTANCE = new Se2Differences();

  // ---
  private Se2Differences() {
    super(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE);
  }
}
