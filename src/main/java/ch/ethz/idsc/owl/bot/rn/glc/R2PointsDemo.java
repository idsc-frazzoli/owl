// code by jph
package ch.ethz.idsc.owl.bot.rn.glc;

import ch.ethz.idsc.owl.bot.rn.RnPointcloudRegions;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ class R2PointsDemo extends R2BaseDemo {
  @Override // from R2BaseDemo
  protected Region<Tensor> region() {
    return RnPointcloudRegions.createRandomRegion(10, Tensors.vector(0, 0), Tensors.vector(4, 4), RealScalar.of(0.6));
  }

  @Override // from R2BaseDemo
  protected Tensor startState() {
    return Tensors.vector(-0.5, -0.5);
  }

  public static void main(String[] args) {
    new R2PointsDemo().start();
  }
}
