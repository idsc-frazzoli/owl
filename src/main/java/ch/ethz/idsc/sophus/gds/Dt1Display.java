// code by jph
package ch.ethz.idsc.sophus.gds;

import ch.ethz.idsc.sophus.bm.BiinvariantMean;
import ch.ethz.idsc.sophus.decim.LineDistance;
import ch.ethz.idsc.sophus.hs.Biinvariant;
import ch.ethz.idsc.sophus.hs.HsManifold;
import ch.ethz.idsc.sophus.hs.HsTransport;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.LieTransport;
import ch.ethz.idsc.sophus.lie.dt.DtBiinvariantMean;
import ch.ethz.idsc.sophus.lie.dt.DtExponential;
import ch.ethz.idsc.sophus.lie.dt.DtGeodesic;
import ch.ethz.idsc.sophus.lie.dt.DtGroup;
import ch.ethz.idsc.sophus.lie.dt.DtManifold;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.math.Geodesic;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.lie.r2.CirclePoints;
import ch.ethz.idsc.tensor.red.Max;

public enum Dt1Display implements ManifoldDisplay {
  INSTANCE;

  private static final Tensor PENTAGON = CirclePoints.of(5).multiply(RealScalar.of(0.2)).unmodifiable();
  // Fehlerhaft, aber zurzeit Probleme mit Ausnahme bei lambda = 0
  private static final ScalarUnaryOperator MAX_X = Max.function(RealScalar.of(0.001));

  @Override // from GeodesicDisplay
  public Geodesic geodesicInterface() {
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
  public HsManifold hsManifold() {
    return DtManifold.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final HsTransport hsTransport() {
    return LieTransport.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public TensorMetric parametricDistance() {
    return null;
  }

  @Override // from GeodesicDisplay
  public Biinvariant metricBiinvariant() {
    return null;
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
