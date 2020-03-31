// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.hs.BiinvariantMean;
import ch.ethz.idsc.sophus.hs.FlattenLogManifold;
import ch.ethz.idsc.sophus.hs.HsExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.lie.so2.CirclePoints;
import ch.ethz.idsc.sophus.lie.st.StBiinvariantMean;
import ch.ethz.idsc.sophus.lie.st.StExponential;
import ch.ethz.idsc.sophus.lie.st.StGeodesic;
import ch.ethz.idsc.sophus.lie.st.StGroup;
import ch.ethz.idsc.sophus.lie.st.StManifold;
import ch.ethz.idsc.sophus.math.Exponential;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public enum St1GeodesicDisplay implements GeodesicDisplay {
  INSTANCE;

  private static final Tensor PENTAGON = CirclePoints.of(5).multiply(RealScalar.of(0.2)).unmodifiable();
  // Fehlerhaft, aber zurzeit Probleme mit Ausnahme bei lambda = 0
  private static final ScalarUnaryOperator MAX_X = Max.function(RealScalar.of(0.001));

  @Override // from GeodesicDisplay
  public GeodesicInterface geodesicInterface() {
    return StGeodesic.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public int dimensions() {
    return 2;
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
    return Se2Matrix.translation(p);
  }

  @Override // from GeodesicDisplay
  public LieGroup lieGroup() {
    return StGroup.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public Exponential exponential() {
    return StExponential.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public HsExponential hsExponential() {
    return StManifold.HS_EXP;
  }

  @Override // from GeodesicDisplay
  public FlattenLogManifold flattenLogManifold() {
    return StManifold.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public TensorMetric parametricDistance() {
    throw new UnsupportedOperationException();
  }

  @Override // from GeodesicDisplay
  public BiinvariantMean biinvariantMean() {
    return StBiinvariantMean.INSTANCE;
  }

  @Override // from Object
  public String toString() {
    return "St1";
  }
}
