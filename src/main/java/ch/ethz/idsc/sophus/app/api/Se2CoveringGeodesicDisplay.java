// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.group.LieExponential;
import ch.ethz.idsc.sophus.group.LieGroup;
import ch.ethz.idsc.sophus.group.Se2BiinvariantMean;
import ch.ethz.idsc.sophus.group.Se2CoveringExponential;
import ch.ethz.idsc.sophus.group.Se2CoveringGeodesic;
import ch.ethz.idsc.sophus.group.Se2CoveringGroup;
import ch.ethz.idsc.sophus.group.Se2CoveringParametricDistance;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.sophus.math.BiinvariantMeanInterface;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.planar.Arrowhead;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public enum Se2CoveringGeodesicDisplay implements GeodesicDisplay {
  INSTANCE;
  // ---
  private static final Tensor ARROWHEAD = Arrowhead.of(0.4);

  @Override // from GeodesicDisplay
  public GeodesicInterface geodesicInterface() {
    return Se2CoveringGeodesic.INSTANCE;
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
    return p.extract(0, 2);
  }

  @Override // from GeodesicDisplay
  public Tensor matrixLift(Tensor p) {
    return Se2Utils.toSE2Matrix(p);
  }

  @Override // from GeodesicDisplay
  public LieGroup lieGroup() {
    return Se2CoveringGroup.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public LieExponential lieExponential() {
    return Se2CoveringExponential.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public Scalar parametricDistance(Tensor p, Tensor q) {
    return Se2CoveringParametricDistance.INSTANCE.distance(p, q);
  }

  @Override // from GeodesicDisplay
  public BiinvariantMeanInterface biinvariantMeanInterface() {
    return Se2BiinvariantMean.DEFAULT;
  }

  @Override // from Object
  public String toString() {
    return "SE2C";
  }
}
