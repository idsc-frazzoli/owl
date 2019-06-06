// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.group.LieExponential;
import ch.ethz.idsc.sophus.group.LieGroup;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.sophus.group.StBiinvariantMean;
import ch.ethz.idsc.sophus.group.StExponential;
import ch.ethz.idsc.sophus.group.StGeodesic;
import ch.ethz.idsc.sophus.group.StGroup;
import ch.ethz.idsc.sophus.math.BiinvariantMeanInterface;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public enum St1GeodesicDisplay implements GeodesicDisplay {
  INSTANCE;
  // ---
  private static final Tensor PENTAGON = CirclePoints.of(5).multiply(RealScalar.of(0.2));
  // Fehlerhaft, aber zurzeit Probleme mit Ausnahme bei lambda = 0
  private static final ScalarUnaryOperator MAX_X = Max.function(RealScalar.of(0.001));

  @Override // from GeodesicDisplay
  public GeodesicInterface geodesicInterface() {
    return StGeodesic.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public Tensor shape() {
    return PENTAGON;
  }

  @Override // from GeodesicDisplay
  public Tensor project(Tensor xya) {
    Tensor point = xya.extract(0, 2);
    point.set(MAX_X, 0);
    return point;
  }

  @Override // from GeodesicDisplay
  public Tensor toPoint(Tensor p) {
    return VectorQ.requireLength(p, 2);
  }

  @Override // from GeodesicDisplay
  public Tensor matrixLift(Tensor p) {
    return Se2Utils.toSE2Translation(p);
  }

  @Override // from GeodesicDisplay
  public LieGroup lieGroup() {
    return StGroup.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public LieExponential lieExponential() {
    return StExponential.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public Scalar parametricDistance(Tensor p, Tensor q) {
    throw new UnsupportedOperationException();
  }

  @Override // from GeodesicDisplay
  public BiinvariantMeanInterface biinvariantMeanInterface() {
    return StBiinvariantMean.INSTANCE;
  }

  @Override // from Object
  public String toString() {
    return "St1";
  }
}
