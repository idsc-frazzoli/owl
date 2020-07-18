// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.io.Serializable;

import ch.ethz.idsc.sophus.crv.decim.LineDistance;
import ch.ethz.idsc.sophus.hs.BiinvariantMean;
import ch.ethz.idsc.sophus.hs.HsExponential;
import ch.ethz.idsc.sophus.hs.HsTransport;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.hs.hn.HnBiinvariantMean;
import ch.ethz.idsc.sophus.hs.hn.HnGeodesic;
import ch.ethz.idsc.sophus.hs.hn.HnManifold;
import ch.ethz.idsc.sophus.hs.hn.HnMetric;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.rn.RnTransport;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.sophus.ply.StarPoints;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** symmetric positive definite 2 x 2 matrices */
public abstract class HnGeodesicDisplay implements GeodesicDisplay, Serializable {
  private static final Tensor STAR_POINTS = StarPoints.of(6, 0.12, 0.04).unmodifiable();
  // ---
  private final int dimensions;

  protected HnGeodesicDisplay(int dimensions) {
    this.dimensions = dimensions;
  }

  @Override // from GeodesicDisplay
  public final GeodesicInterface geodesicInterface() {
    return HnGeodesic.INSTANCE;
  }

  @Override
  public final int dimensions() {
    return dimensions;
  }

  @Override // from GeodesicDisplay
  public final TensorUnaryOperator tangentProjection(Tensor xyz) {
    return null; // FIXME
  }

  @Override // from GeodesicDisplay
  public final Tensor shape() {
    return STAR_POINTS;
  }

  @Override // from GeodesicDisplay
  public final LieGroup lieGroup() {
    return null;
  }

  @Override
  public final HsExponential hsExponential() {
    return HnManifold.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final HsTransport hsTransport() {
    return RnTransport.INSTANCE; // FIXME
  }

  @Override // from GeodesicDisplay
  public final TensorMetric parametricDistance() {
    return HnMetric.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final boolean isMetricBiinvariant() {
    return true;
  }

  @Override // from GeodesicDisplay
  public final BiinvariantMean biinvariantMean() {
    return HnBiinvariantMean.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final VectorLogManifold vectorLogManifold() {
    return HnManifold.INSTANCE;
  }

  @Override
  public final LineDistance lineDistance() {
    return null; // TODO line distance
  }

  @Override
  public final String toString() {
    return "H" + dimensions();
  }
}
