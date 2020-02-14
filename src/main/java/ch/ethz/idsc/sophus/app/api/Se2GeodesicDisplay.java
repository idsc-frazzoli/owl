// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.hs.r2.Se2ParametricDistance;
import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.se2.Se2BiinvariantMean;
import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.sophus.lie.se2.Se2Group;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringExponential;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringInverseDistanceCoordinate;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.win.BarycentricCoordinate;
import ch.ethz.idsc.sophus.ply.Arrowhead;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public enum Se2GeodesicDisplay implements GeodesicDisplay {
  INSTANCE;

  private static final Tensor ARROWHEAD = Arrowhead.of(0.4);

  @Override // from GeodesicDisplay
  public GeodesicInterface geodesicInterface() {
    return Se2Geodesic.INSTANCE;
  }

  @Override
  public int dimensions() {
    return 3;
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
    return Se2Matrix.of(p);
  }

  @Override // from GeodesicDisplay
  public LieGroup lieGroup() {
    return Se2Group.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public LieExponential lieExponential() {
    return Se2CoveringExponential.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public Scalar parametricDistance(Tensor p, Tensor q) {
    return Se2ParametricDistance.INSTANCE.distance(p, q);
  }

  @Override // from GeodesicDisplay
  public BiinvariantMean biinvariantMean() {
    return Se2BiinvariantMean.FILTER;
  }

  @Override
  public BarycentricCoordinate barycentricCoordinate() {
    return Se2CoveringInverseDistanceCoordinate.INSTANCE;
  }

  @Override // from Object
  public String toString() {
    return "SE2";
  }
}
