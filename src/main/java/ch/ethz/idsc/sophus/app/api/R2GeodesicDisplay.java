// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.group.LieExponential;
import ch.ethz.idsc.sophus.group.LieGroup;
import ch.ethz.idsc.sophus.group.RnExponential;
import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.sophus.group.RnGroup;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.lie.CirclePoints;

public enum R2GeodesicDisplay implements GeodesicDisplay {
  INSTANCE;
  // ---
  private static final Tensor CIRCLE = CirclePoints.of(15).multiply(RealScalar.of(0.1));

  @Override // from GeodesicDisplay
  public GeodesicInterface geodesicInterface() {
    return RnGeodesic.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public Tensor shape() {
    return CIRCLE;
  }

  @Override // from GeodesicDisplay
  public Tensor project(Tensor xya) {
    return xya.extract(0, 2);
  }

  @Override
  public Tensor toPoint(Tensor p) {
    return VectorQ.requireLength(p, 2);
  }

  @Override // from GeodesicDisplay
  public Tensor matrixLift(Tensor p) {
    return Se2Utils.toSE2Translation(p);
  }

  @Override // from GeodesicDisplay
  public LieGroup lieGroup() {
    return RnGroup.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public LieExponential lieExponential() {
    return RnExponential.INSTANCE;
  }

  @Override // from Object
  public String toString() {
    return "R2";
  }
}
