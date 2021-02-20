// code by jph
package ch.ethz.idsc.sophus.gds;

import java.io.Serializable;
import java.util.Random;

import ch.ethz.idsc.sophus.bm.BiinvariantMean;
import ch.ethz.idsc.sophus.decim.LineDistance;
import ch.ethz.idsc.sophus.hs.Biinvariant;
import ch.ethz.idsc.sophus.hs.HsManifold;
import ch.ethz.idsc.sophus.hs.HsTransport;
import ch.ethz.idsc.sophus.hs.hn.HnBiinvariantMean;
import ch.ethz.idsc.sophus.hs.hn.HnGeodesic;
import ch.ethz.idsc.sophus.hs.hn.HnManifold;
import ch.ethz.idsc.sophus.hs.hn.HnMetric;
import ch.ethz.idsc.sophus.hs.hn.HnMetricBiinvariant;
import ch.ethz.idsc.sophus.hs.hn.HnTransport;
import ch.ethz.idsc.sophus.hs.hn.HnWeierstrassCoordinate;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.math.Geodesic;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.sophus.ply.StarPoints;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Chop;

/** symmetric positive definite 2 x 2 matrices */
public abstract class HnDisplay implements ManifoldDisplay, Serializable {
  private static final Tensor STAR_POINTS = StarPoints.of(6, 0.12, 0.04).unmodifiable();
  protected static final Scalar RADIUS = RealScalar.of(2.5);
  // ---
  private final int dimensions;

  protected HnDisplay(int dimensions) {
    this.dimensions = dimensions;
  }

  @Override // from GeodesicDisplay
  public final Geodesic geodesicInterface() {
    return HnGeodesic.INSTANCE;
  }

  @Override
  public final int dimensions() {
    return dimensions;
  }

  @Override // from GeodesicDisplay
  public final Tensor project(Tensor xya) {
    return HnWeierstrassCoordinate.toPoint(xya.extract(0, dimensions));
  }

  @Override // from GeodesicDisplay
  public final TensorUnaryOperator tangentProjection(Tensor xyz) {
    return null;
  }

  @Override // from GeodesicDisplay
  public final Tensor matrixLift(Tensor p) {
    return Se2Matrix.translation(p);
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
  public final LieExponential lieExponential() {
    return null;
  }

  @Override
  public final HsManifold hsManifold() {
    return HnManifold.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final HsTransport hsTransport() {
    return HnTransport.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final TensorMetric parametricDistance() {
    return HnMetric.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final Biinvariant metricBiinvariant() {
    return HnMetricBiinvariant.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final BiinvariantMean biinvariantMean() {
    return HnBiinvariantMean.of(Chop._08);
  }

  @Override
  public final LineDistance lineDistance() {
    return null;
  }

  @Override // from GeodesicDisplay
  public final RandomSampleInterface randomSampleInterface() {
    Distribution distribution = UniformDistribution.of(RADIUS.negate(), RADIUS);
    return new RandomSampleInterface() {
      @Override
      public Tensor randomSample(Random random) {
        // return VectorQ.requireLength(RandomVariate.of(distribution, random, 2).append(RealScalar.ZERO), 3);
        return HnWeierstrassCoordinate.toPoint(RandomVariate.of(distribution, random, dimensions));
      }
    };
  }

  @Override
  public final String toString() {
    return "H" + dimensions();
  }
}
