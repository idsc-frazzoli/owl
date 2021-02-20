// code by jph
package ch.ethz.idsc.sophus.gds;

import java.io.Serializable;

import ch.ethz.idsc.sophus.bm.BiinvariantMean;
import ch.ethz.idsc.sophus.decim.LineDistance;
import ch.ethz.idsc.sophus.hs.Biinvariant;
import ch.ethz.idsc.sophus.hs.HsManifold;
import ch.ethz.idsc.sophus.hs.HsTransport;
import ch.ethz.idsc.sophus.hs.MetricBiinvariant;
import ch.ethz.idsc.sophus.hs.rpn.RpnManifold;
import ch.ethz.idsc.sophus.hs.rpn.RpnMetric;
import ch.ethz.idsc.sophus.hs.rpn.RpnRandomSample;
import ch.ethz.idsc.sophus.hs.sn.SnFastMean;
import ch.ethz.idsc.sophus.hs.sn.SnGeodesic;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.math.Geodesic;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.r2.CirclePoints;

/** symmetric positive definite 2 x 2 matrices */
public abstract class RpnDisplay implements ManifoldDisplay, Serializable {
  private static final Tensor CIRCLE = CirclePoints.of(15).multiply(RealScalar.of(0.05)).unmodifiable();
  // ---
  private final int dimensions;

  protected RpnDisplay(int dimensions) {
    this.dimensions = dimensions;
  }

  @Override // from GeodesicDisplay
  public final Geodesic geodesicInterface() {
    return SnGeodesic.INSTANCE; // TODO
  }

  @Override
  public final int dimensions() {
    return dimensions;
  }

  @Override // from GeodesicDisplay
  public final Tensor shape() {
    return CIRCLE;
  }

  @Override // from GeodesicDisplay
  public final LieGroup lieGroup() {
    return null;
  }

  @Override // from GeodesicDisplay
  public LieExponential lieExponential() {
    return null;
  }

  @Override
  public final HsManifold hsManifold() {
    return RpnManifold.INSTANCE;
  }

  @Override
  public final HsTransport hsTransport() {
    return null;
  }

  @Override // from GeodesicDisplay
  public final TensorMetric parametricDistance() {
    return RpnMetric.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final Biinvariant metricBiinvariant() {
    return MetricBiinvariant.EUCLIDEAN;
  }

  @Override // from GeodesicDisplay
  public final BiinvariantMean biinvariantMean() {
    return SnFastMean.INSTANCE; // TODO
  }

  @Override
  public final LineDistance lineDistance() {
    return null;
  }

  @Override
  public final String toString() {
    return "RP" + dimensions();
  }

  @Override
  public final RandomSampleInterface randomSampleInterface() {
    return RpnRandomSample.of(dimensions());
  }
}
