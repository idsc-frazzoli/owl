// code by jph
package ch.ethz.idsc.sophus.gds;

import java.io.Serializable;
import java.util.Random;

import ch.ethz.idsc.sophus.bm.BiinvariantMean;
import ch.ethz.idsc.sophus.decim.LineDistance;
import ch.ethz.idsc.sophus.hs.Biinvariant;
import ch.ethz.idsc.sophus.hs.HsManifold;
import ch.ethz.idsc.sophus.hs.HsTransport;
import ch.ethz.idsc.sophus.hs.MetricBiinvariant;
import ch.ethz.idsc.sophus.hs.sn.SnFastMean;
import ch.ethz.idsc.sophus.hs.sn.SnGeodesic;
import ch.ethz.idsc.sophus.hs.sn.SnLineDistance;
import ch.ethz.idsc.sophus.hs.sn.SnManifold;
import ch.ethz.idsc.sophus.hs.sn.SnMetric;
import ch.ethz.idsc.sophus.hs.sn.SnRandomSample;
import ch.ethz.idsc.sophus.hs.sn.SnTransport;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.math.Geodesic;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.r2.CirclePoints;
import ch.ethz.idsc.tensor.sca.Abs;

/** symmetric positive definite 2 x 2 matrices */
public abstract class SnDisplay implements ManifoldDisplay, Serializable {
  private static final Tensor CIRCLE = CirclePoints.of(15).multiply(RealScalar.of(0.05)).unmodifiable();
  // ---
  private final int dimensions;

  protected SnDisplay(int dimensions) {
    this.dimensions = dimensions;
  }

  @Override // from GeodesicDisplay
  public final Geodesic geodesicInterface() {
    return SnGeodesic.INSTANCE;
  }

  @Override // from GeodesicDisplay
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
  public final LieExponential lieExponential() {
    return null;
  }

  @Override // from GeodesicDisplay
  public final HsManifold hsManifold() {
    return SnManifold.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final HsTransport hsTransport() {
    return SnTransport.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final TensorMetric parametricDistance() {
    return SnMetric.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final Biinvariant metricBiinvariant() {
    return MetricBiinvariant.EUCLIDEAN;
  }

  @Override // from GeodesicDisplay
  public final BiinvariantMean biinvariantMean() {
    return SnFastMean.INSTANCE; // SnBiinvariantMean.of(Chop._05); // SnFastMean.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final LineDistance lineDistance() {
    return SnLineDistance.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public final RandomSampleInterface randomSampleInterface() {
    RandomSampleInterface randomSampleInterface = SnRandomSample.of(dimensions);
    return new RandomSampleInterface() {
      @Override
      public Tensor randomSample(Random random) {
        Tensor xyz = randomSampleInterface.randomSample(random);
        xyz.set(Abs.FUNCTION, dimensions);
        return xyz;
      }
    };
  }

  @Override
  public final String toString() {
    return "S" + dimensions();
  }
}
