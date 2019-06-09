// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.group.LieExponential;
import ch.ethz.idsc.sophus.group.LieGroup;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.sophus.math.BiinvariantMean;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.planar.H2ParametricDistance;
import ch.ethz.idsc.sophus.space.H2Geodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public enum H2GeodesicDisplay implements GeodesicDisplay {
  INSTANCE;
  // ---
  private static final Tensor TRIANGLE = CirclePoints.of(3).multiply(RealScalar.of(0.2));
  private static final ScalarUnaryOperator MAX_Y = Max.function(RealScalar.of(0.01));

  @Override // from GeodesicDisplay
  public GeodesicInterface geodesicInterface() {
    return H2Geodesic.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public Tensor shape() {
    return TRIANGLE;
  }

  @Override // from GeodesicDisplay
  public Tensor project(Tensor xya) {
    Tensor point = xya.extract(0, 2);
    point.set(MAX_Y, 1);
    return point;
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
    return null;
  }

  @Override // from GeodesicDisplay
  public LieExponential lieExponential() {
    return null;
  }

  @Override // from GeodesicDisplay
  public Scalar parametricDistance(Tensor p, Tensor q) {
    return H2ParametricDistance.INSTANCE.distance(p, q);
  }

  @Override // from GeodesicDisplay
  public BiinvariantMean biinvariantMeanInterface() {
    throw new UnsupportedOperationException();
  }

  @Override // from Object
  public String toString() {
    return "H2";
  }
}
