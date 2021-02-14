// code by jph
package ch.ethz.idsc.sophus.gds;

import java.io.Serializable;

import ch.ethz.idsc.sophus.crv.decim.LineDistance;
import ch.ethz.idsc.sophus.hs.Biinvariant;
import ch.ethz.idsc.sophus.hs.BiinvariantMean;
import ch.ethz.idsc.sophus.hs.HsExponential;
import ch.ethz.idsc.sophus.hs.HsTransport;
import ch.ethz.idsc.sophus.hs.MetricBiinvariant;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.LieTransport;
import ch.ethz.idsc.sophus.lie.rn.RnBiinvariantMean;
import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.sophus.lie.rn.RnGroup;
import ch.ethz.idsc.sophus.lie.rn.RnLineDistance;
import ch.ethz.idsc.sophus.lie.rn.RnManifold;
import ch.ethz.idsc.sophus.lie.rn.RnMetric;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.PadRight;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.lie.r2.CirclePoints;

public abstract class RnGeodesicDisplay implements GeodesicDisplay, Serializable {
  private static final Tensor CIRCLE = CirclePoints.of(15).multiply(RealScalar.of(0.04)).unmodifiable();
  private static final TensorUnaryOperator PAD = PadRight.zeros(2);
  // ---
  private final int dimensions;

  /* package */ RnGeodesicDisplay(int dimensions) {
    this.dimensions = dimensions;
  }

  @Override // from GeodesicDisplay
  public final GeodesicInterface geodesicInterface() {
    return RnGeodesic.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final int dimensions() {
    return dimensions;
  }

  @Override // from GeodesicDisplay
  public Tensor shape() {
    return CIRCLE;
  }

  @Override // from GeodesicDisplay
  public final Tensor project(Tensor xya) {
    return xya.extract(0, dimensions);
  }

  @Override // from GeodesicDisplay
  public final TensorUnaryOperator tangentProjection(Tensor p) {
    return PAD;
  }

  @Override // from GeodesicDisplay
  public final LieGroup lieGroup() {
    return RnGroup.INSTANCE;
  }

  @Override
  public final LieExponential lieExponential() {
    return RnManifold.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final HsExponential hsExponential() {
    return RnManifold.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final HsTransport hsTransport() {
    return LieTransport.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final TensorMetric parametricDistance() {
    return RnMetric.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final Biinvariant metricBiinvariant() {
    return MetricBiinvariant.RIEMANN;
  }

  @Override // from GeodesicDisplay
  public final BiinvariantMean biinvariantMean() {
    return RnBiinvariantMean.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final LineDistance lineDistance() {
    return RnLineDistance.INSTANCE;
  }

  @Override // from Object
  public final String toString() {
    return "R" + dimensions;
  }
}
