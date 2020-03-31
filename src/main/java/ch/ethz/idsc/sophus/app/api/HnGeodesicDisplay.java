// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.io.Serializable;

import ch.ethz.idsc.owl.bot.r2.StarPoints;
import ch.ethz.idsc.sophus.hs.BiinvariantMean;
import ch.ethz.idsc.sophus.hs.FlattenLogManifold;
import ch.ethz.idsc.sophus.hs.HsExponential;
import ch.ethz.idsc.sophus.hs.hn.HnBiinvariantMean;
import ch.ethz.idsc.sophus.hs.hn.HnGeodesic;
import ch.ethz.idsc.sophus.hs.hn.HnManifold;
import ch.ethz.idsc.sophus.hs.hn.HnMetric;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.math.Exponential;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.tensor.Tensor;

/** symmetric positive definite 2 x 2 matrices */
public abstract class HnGeodesicDisplay implements GeodesicDisplay, Serializable {
  private static final Tensor STAR_POINTS = StarPoints.of(6, 0.15, 0.05).unmodifiable();
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
  public final Tensor shape() {
    return STAR_POINTS;
  }

  @Override // from GeodesicDisplay
  public final LieGroup lieGroup() {
    return null;
  }

  @Override // from GeodesicDisplay
  public final Exponential exponential() {
    return null;
  }

  @Override
  public final HsExponential hsExponential() {
    return HnManifold.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final TensorMetric parametricDistance() {
    return HnMetric.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final BiinvariantMean biinvariantMean() {
    return HnBiinvariantMean.INSTANCE; // phong is too imprecise
  }

  @Override // from GeodesicDisplay
  public final FlattenLogManifold flattenLogManifold() {
    return HnManifold.INSTANCE;
  }

  @Override
  public final String toString() {
    return "H" + dimensions();
  }
}
