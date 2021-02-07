// code by jph
package ch.ethz.idsc.sophus.gds;

import ch.ethz.idsc.sophus.crv.decim.LineDistance;
import ch.ethz.idsc.sophus.hs.BiinvariantMean;
import ch.ethz.idsc.sophus.hs.HsExponential;
import ch.ethz.idsc.sophus.hs.HsTransport;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.dt.DtBiinvariantMean;
import ch.ethz.idsc.sophus.lie.dt.DtExponential;
import ch.ethz.idsc.sophus.lie.dt.DtGeodesic;
import ch.ethz.idsc.sophus.lie.dt.DtGroup;
import ch.ethz.idsc.sophus.lie.dt.DtManifold;
import ch.ethz.idsc.sophus.lie.rn.RnTransport;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.lie.r2.CirclePoints;
import ch.ethz.idsc.tensor.red.Max;

public enum Dt1GeodesicDisplay implements GeodesicDisplay {
  INSTANCE;

  private static final Tensor PENTAGON = CirclePoints.of(5).multiply(RealScalar.of(0.2)).unmodifiable();
  // Fehlerhaft, aber zurzeit Probleme mit Ausnahme bei lambda = 0
  private static final ScalarUnaryOperator MAX_X = Max.function(RealScalar.of(0.001));

  @Override // from GeodesicDisplay
  public GeodesicInterface geodesicInterface() {
    return DtGeodesic.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public int dimensions() {
    return 2;
  }

  @Override // from GeodesicDisplay
  public TensorUnaryOperator tangentProjection(Tensor p) {
    return null;
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
    return DtGroup.INSTANCE;
  }

  @Override
  public LieExponential lieExponential() {
    return LieExponential.of(DtGroup.INSTANCE, DtExponential.INSTANCE);
  }

  @Override // from GeodesicDisplay
  public HsExponential hsExponential() {
    return DtManifold.HS_EXP;
  }

  @Override // from GeodesicDisplay
  public final HsTransport hsTransport() {
    return RnTransport.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public VectorLogManifold vectorLogManifold() {
    return DtManifold.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public TensorMetric parametricDistance() {
    return null;
  }

  @Override // from GeodesicDisplay
  public boolean isMetricBiinvariant() {
    return false;
  }

  @Override // from GeodesicDisplay
  public BiinvariantMean biinvariantMean() {
    return DtBiinvariantMean.INSTANCE;
  }

  @Override
  public final LineDistance lineDistance() {
    return null;
  }

  @Override // from Object
  public String toString() {
    return "Dt1";
  }
}
