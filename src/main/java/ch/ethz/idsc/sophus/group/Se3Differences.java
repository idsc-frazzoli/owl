// code by jph
package ch.ethz.idsc.sophus.group;

public class Se3Differences extends LieDifferences {
  public static final LieDifferences INSTANCE = new Se3Differences();

  // ---
  private Se3Differences() {
    super(LinearGroup.INSTANCE, Se3Exponential.INSTANCE);
  }
}
