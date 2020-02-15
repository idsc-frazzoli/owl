// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.io.Serializable;

import ch.ethz.idsc.sophus.hs.sn.SnGeodesic;
import ch.ethz.idsc.sophus.hs.sn.SnInverseDistanceCoordinate;
import ch.ethz.idsc.sophus.hs.sn.SnMean;
import ch.ethz.idsc.sophus.hs.sn.SnMetric;
import ch.ethz.idsc.sophus.lie.BiinvariantMean;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.so2.CirclePoints;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.win.BarycentricCoordinate;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.sca.Chop;

/** symmetric positive definite 2 x 2 matrices */
public abstract class SnGeodesicDisplay implements GeodesicDisplay, Serializable {
  private static final Tensor CIRCLE = CirclePoints.of(15).multiply(RealScalar.of(0.2));
  protected static final Scalar RADIUS = RealScalar.of(6);
  /***************************************************/
  private final Scalar radius;

  public SnGeodesicDisplay(Scalar radius) {
    this.radius = radius;
  }

  @Override // from GeodesicDisplay
  public final GeodesicInterface geodesicInterface() {
    return SnGeodesic.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final Tensor shape() {
    return CIRCLE;
  }

  @Override // from GeodesicDisplay
  public final LieGroup lieGroup() {
    throw new UnsupportedOperationException();
  }

  @Override // from GeodesicDisplay
  public final LieExponential lieExponential() {
    throw new UnsupportedOperationException();
  }

  @Override // from GeodesicDisplay
  public final Scalar parametricDistance(Tensor p, Tensor q) {
    return SnMetric.INSTANCE.distance(p, q);
  }

  @Override // from GeodesicDisplay
  public final BiinvariantMean biinvariantMean() {
    return new SnMean(Chop._05);
  }

  @Override
  public final BarycentricCoordinate barycentricCoordinate() {
    return SnInverseDistanceCoordinate.INSTANCE;
  }

  @Override
  public final String toString() {
    return "S" + dimensions();
  }

  public final Scalar getRadius() {
    return radius;
  }
}
