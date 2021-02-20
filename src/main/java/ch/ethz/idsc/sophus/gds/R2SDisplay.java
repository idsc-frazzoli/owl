// code by jph
package ch.ethz.idsc.sophus.gds;

import ch.ethz.idsc.sophus.bm.BiinvariantMean;
import ch.ethz.idsc.sophus.decim.LineDistance;
import ch.ethz.idsc.sophus.hs.Biinvariant;
import ch.ethz.idsc.sophus.hs.HsManifold;
import ch.ethz.idsc.sophus.hs.HsTransport;
import ch.ethz.idsc.sophus.hs.MetricBiinvariant;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.LieTransport;
import ch.ethz.idsc.sophus.lie.r2s.R2SGeodesic;
import ch.ethz.idsc.sophus.lie.r2s.R2SGroup;
import ch.ethz.idsc.sophus.lie.r2s.R2SManifold;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.lie.so2.So2;
import ch.ethz.idsc.sophus.math.Geodesic;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.sophus.ply.Arrowhead;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;

public enum R2SDisplay implements ManifoldDisplay {
  INSTANCE;

  private static final Tensor ARROWHEAD = Arrowhead.of(0.2).unmodifiable();

  @Override // from GeodesicDisplay
  public Geodesic geodesicInterface() {
    return R2SGeodesic.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public int dimensions() {
    return 3;
  }

  @Override // from GeodesicDisplay
  public Tensor shape() {
    return ARROWHEAD;
  }

  @Override // from GeodesicDisplay
  public Tensor project(Tensor xya) {
    Tensor xym = xya.copy();
    xym.set(So2.MOD, 2);
    return xym;
  }

  @Override // from GeodesicDisplay
  public final TensorUnaryOperator tangentProjection(Tensor xyz) {
    return null;
  }

  @Override // from GeodesicDisplay
  public Tensor toPoint(Tensor p) {
    return p.extract(0, 2);
  }

  @Override // from GeodesicDisplay
  public Tensor matrixLift(Tensor p) {
    return Se2Matrix.of(p);
  }

  @Override // from GeodesicDisplay
  public LieGroup lieGroup() {
    return R2SGroup.INSTANCE;
  }

  @Override
  public LieExponential lieExponential() {
    return R2SManifold.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public HsManifold hsManifold() {
    return R2SManifold.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public HsTransport hsTransport() {
    return LieTransport.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public TensorMetric parametricDistance() {
    return null;
  }

  @Override // from GeodesicDisplay
  public Biinvariant metricBiinvariant() {
    return MetricBiinvariant.EUCLIDEAN;
  }

  @Override // from GeodesicDisplay
  public BiinvariantMean biinvariantMean() {
    return null;
  }

  @Override
  public LineDistance lineDistance() {
    return null;
  }

  @Override // from GeodesicDisplay
  public String toString() {
    return "R2S";
  }
}
