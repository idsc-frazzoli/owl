// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.owl.math.planar.Arrowhead;
import ch.ethz.idsc.sophus.group.Se2CoveringGeodesic;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;

public enum Se2CoveringGeodesicDisplay implements GeodesicDisplay {
  INSTANCE;
  // ---
  private static final Tensor ARROWHEAD = Arrowhead.of(RealScalar.of(0.4));

  @Override // from GeodesicDisplay
  public GeodesicInterface geodesicInterface() {
    return Se2CoveringGeodesic.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public Tensor pointShape() {
    return ARROWHEAD;
  }

  @Override // from GeodesicDisplay
  public Tensor project(Tensor xya) {
    return xya;
  }
}
