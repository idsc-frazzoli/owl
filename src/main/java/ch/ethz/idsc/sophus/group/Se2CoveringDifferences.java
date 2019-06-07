// code by jph
package ch.ethz.idsc.sophus.group;

public class Se2CoveringDifferences extends LieDifferences {
  public static final LieDifferences INSTANCE = new Se2CoveringDifferences();

  // ---
  private Se2CoveringDifferences() {
    super(Se2CoveringGroup.INSTANCE, Se2CoveringExponential.INSTANCE);
  }
}
