// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.clt.ClothoidBuilder;
import ch.ethz.idsc.sophus.clt.ClothoidBuilders;
import ch.ethz.idsc.sophus.lie.so2.So2;
import ch.ethz.idsc.tensor.Tensor;

public final class Se2ClothoidDisplay extends AbstractClothoidDisplay {
  public static final GeodesicDisplay INSTANCE = new Se2ClothoidDisplay();

  private Se2ClothoidDisplay() {
    // ---
  }

  @Override // from AbstractClothoidDisplay
  public ClothoidBuilder geodesicInterface() {
    return ClothoidBuilders.SE2;
  }

  @Override // from GeodesicDisplay
  public final Tensor project(Tensor xya) {
    Tensor xym = xya.copy();
    xym.set(So2.MOD, 2);
    return xym;
  }

  @Override // from Object
  public String toString() {
    return "Cl";
  }
}
