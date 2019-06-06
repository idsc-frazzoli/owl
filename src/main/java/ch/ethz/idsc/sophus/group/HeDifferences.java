// code by jph
package ch.ethz.idsc.sophus.group;

public class HeDifferences extends LieDifferences {
  public static final LieDifferences INSTANCE = new HeDifferences();

  // ---
  private HeDifferences() {
    super(HeGroup.INSTANCE, HeExponential.INSTANCE);
  }
}
