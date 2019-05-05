// code by jph
package ch.ethz.idsc.owl.bot.rn.glc;

import ch.ethz.idsc.owl.bot.r2.R2ExamplePolygons;
import ch.ethz.idsc.owl.math.region.PolygonRegions;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;

/* package */ class R2PolygonDemo extends R2BaseDemo {
  @Override // from R2BaseDemo
  protected Region<Tensor> region() {
    return PolygonRegions.numeric(R2ExamplePolygons.BULKY_TOP_LEFT);
  }

  @Override // from R2BaseDemo
  protected Tensor startState() {
    return Array.zeros(2);
  }

  public static void main(String[] args) {
    new R2PolygonDemo().start();
  }
}
