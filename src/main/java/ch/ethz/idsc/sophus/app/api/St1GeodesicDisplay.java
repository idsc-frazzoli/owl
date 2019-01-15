// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.group.LieGroup;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.sophus.group.StGeodesic;
import ch.ethz.idsc.sophus.group.StGroup;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.CirclePoints;

public enum St1GeodesicDisplay implements GeodesicDisplay {
  INSTANCE;
  // ---
  private static final Tensor TRIANGLE = CirclePoints.of(3).multiply(RealScalar.of(0.2));

  @Override // from GeodesicDisplay
  public GeodesicInterface geodesicInterface() {
    return StGeodesic.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public Tensor shape() {
    return TRIANGLE;
  }

  @Override // from GeodesicDisplay
  public Tensor project(Tensor xya) {
    return Tensors.of(xya.extract(0, 1), xya.extract(1, 2));
  }

  @Override
  public Tensor toPoint(Tensor p) {
    return p;
  }

  @Override // from GeodesicDisplay
  public Tensor matrixLift(Tensor p) {
    // return Se2Utils.toSE2Translation(p);
    return Se2Utils.toSE2Translation(toPoint(p));
  }

  @Override // from GeodesicDisplay
  public LieGroup lieGroup() {
    return StGroup.INSTANCE;
  }

  @Override // from Object
  public String toString() {
    return "St1";
  }
}
