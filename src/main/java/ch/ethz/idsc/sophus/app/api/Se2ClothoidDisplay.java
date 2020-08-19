// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.clt.ClothoidBuilder;
import ch.ethz.idsc.sophus.clt.ClothoidBuilders;
import ch.ethz.idsc.sophus.lie.so2.So2;
import ch.ethz.idsc.tensor.Tensor;

public final class Se2ClothoidDisplay extends AbstractClothoidDisplay {
  public static final String ANALYTIC_STRING = "ClA";
  public static final String LEGENDRE_STRING = "Cl3";
  public static final GeodesicDisplay ANALYTIC = //
      new Se2ClothoidDisplay(ClothoidBuilders.SE2_ANALYTIC, ANALYTIC_STRING);
  public static final GeodesicDisplay LEGENDRE = //
      new Se2ClothoidDisplay(ClothoidBuilders.SE2_LEGENDRE, LEGENDRE_STRING);
  // ---
  private final ClothoidBuilder clothoidBuilder;
  private final String string;

  private Se2ClothoidDisplay(ClothoidBuilder clothoidBuilder, String string) {
    this.clothoidBuilder = clothoidBuilder;
    this.string = string;
  }

  @Override // from AbstractClothoidDisplay
  public ClothoidBuilder geodesicInterface() {
    return clothoidBuilder;
  }

  @Override // from GeodesicDisplay
  public Tensor project(Tensor xya) {
    Tensor xym = xya.copy();
    xym.set(So2.MOD, 2);
    return xym;
  }

  @Override // from Object
  public String toString() {
    return string;
  }
}
