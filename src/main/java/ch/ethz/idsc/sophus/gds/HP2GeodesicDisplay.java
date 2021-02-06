// code by jph
package ch.ethz.idsc.sophus.gds;

import ch.ethz.idsc.sophus.crv.decim.LineDistance;
import ch.ethz.idsc.sophus.hs.BiinvariantMean;
import ch.ethz.idsc.sophus.hs.HsExponential;
import ch.ethz.idsc.sophus.hs.HsTransport;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.hs.h2.H2Geodesic;
import ch.ethz.idsc.sophus.hs.h2.H2Metric;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.rn.RnTransport;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.SplitParametricCurve;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.api.ScalarUnaryOperator;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.lie.r2.CirclePoints;
import ch.ethz.idsc.tensor.red.Max;

public enum HP2GeodesicDisplay implements GeodesicDisplay {
  INSTANCE;

  private static final Tensor TRIANGLE = CirclePoints.of(3).multiply(RealScalar.of(0.2)).unmodifiable();
  private static final ScalarUnaryOperator MAX_Y = Max.function(RealScalar.of(0.01));

  @Override // from GeodesicDisplay
  public GeodesicInterface geodesicInterface() {
    return SplitParametricCurve.of(H2Geodesic.INSTANCE);
  }

  @Override // from GeodesicDisplay
  public int dimensions() {
    return 2;
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

  @Override // from GeodesicDisplay
  public final TensorUnaryOperator tangentProjection(Tensor xyz) {
    return null;
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
    return null;
  }

  @Override // from GeodesicDisplay
  public HsExponential hsExponential() {
    return null;
  }

  @Override // from GeodesicDisplay
  public HsTransport hsTransport() {
    return RnTransport.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public VectorLogManifold vectorLogManifold() {
    return null;
  }

  @Override // from GeodesicDisplay
  public TensorMetric parametricDistance() {
    return H2Metric.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final boolean isMetricBiinvariant() {
    return true;
  }

  @Override // from GeodesicDisplay
  public BiinvariantMean biinvariantMean() {
    return null;
  }

  @Override
  public final LineDistance lineDistance() {
    return null;
  }

  @Override // from Object
  public String toString() {
    return "HP2";
  }
}