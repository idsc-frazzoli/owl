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
import ch.ethz.idsc.sophus.lie.he.HeBiinvariantMean;
import ch.ethz.idsc.sophus.lie.he.HeGeodesic;
import ch.ethz.idsc.sophus.lie.he.HeGroup;
import ch.ethz.idsc.sophus.lie.he.HeManifold;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.math.Geodesic;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.lie.r2.CirclePoints;

public enum He1Display implements ManifoldDisplay {
  INSTANCE;

  private static final Tensor SQUARE = CirclePoints.of(4).multiply(RealScalar.of(0.2)).unmodifiable();

  @Override // from GeodesicDisplay
  public Geodesic geodesicInterface() {
    return HeGeodesic.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public int dimensions() {
    return 3;
  }

  @Override // from GeodesicDisplay
  public Tensor shape() {
    return SQUARE;
  }

  @Override // from GeodesicDisplay
  public Tensor project(Tensor xya) {
    return Tensors.of(xya.extract(0, 1), xya.extract(1, 2), xya.Get(2));
  }

  @Override // from GeodesicDisplay
  public final TensorUnaryOperator tangentProjection(Tensor xyz) {
    return null;
  }

  @Override // from GeodesicDisplay
  public Tensor toPoint(Tensor p) {
    if (VectorQ.of(p))
      throw new RuntimeException();
    return Tensors.of(p.Get(0, 0), p.Get(1, 0));
  }

  @Override // from GeodesicDisplay
  public Tensor matrixLift(Tensor p) {
    return Se2Matrix.translation(toPoint(p));
  }

  @Override // from GeodesicDisplay
  public LieGroup lieGroup() {
    return HeGroup.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public LieExponential lieExponential() {
    return HeManifold.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public HsManifold hsManifold() {
    return HeManifold.INSTANCE;
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
    return null;
  }

  @Override // from GeodesicDisplay
  public BiinvariantMean biinvariantMean() {
    return HeBiinvariantMean.INSTANCE;
  }

  @Override
  public LineDistance lineDistance() {
    return null;
  }

  @Override // from GeodesicDisplay
  public String toString() {
    return "He1";
  }
}
