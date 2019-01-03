// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.owl.math.planar.Arrowhead;
import ch.ethz.idsc.sophus.group.LieGroup;
import ch.ethz.idsc.sophus.group.Se2Geodesic;
import ch.ethz.idsc.sophus.group.Se2Group;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;

public enum Se2GeodesicDisplay implements GeodesicDisplay {
  INSTANCE;
  // ---
  private static final Tensor ARROWHEAD = Arrowhead.of(RealScalar.of(0.4));

  @Override // from GeodesicDisplay
  public GeodesicInterface geodesicInterface() {
    return Se2Geodesic.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public Tensor shape() {
    return ARROWHEAD;
  }

  @Override // from GeodesicDisplay
  public Tensor project(Tensor xya) {
    return xya;
  }

  @Override
  public Tensor toPoint(Tensor p) {
    return p;
  }

  @Override // from GeodesicDisplay
  public Tensor matrixLift(Tensor p) {
    return Se2Utils.toSE2Matrix(p);
  }

  @Override // from GeodesicDisplay
  public LieGroup lieGroup() {
    return Se2Group.INSTANCE;
  }

  @Override // from Object
  public String toString() {
    return "SE2";
  }
}
