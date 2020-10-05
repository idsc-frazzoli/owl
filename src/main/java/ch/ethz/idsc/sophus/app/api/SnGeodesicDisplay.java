// code by jph
package ch.ethz.idsc.sophus.app.api;

import java.io.Serializable;
import java.util.Random;

import ch.ethz.idsc.sophus.crv.decim.LineDistance;
import ch.ethz.idsc.sophus.hs.BiinvariantMean;
import ch.ethz.idsc.sophus.hs.HsExponential;
import ch.ethz.idsc.sophus.hs.HsTransport;
import ch.ethz.idsc.sophus.hs.sn.SnFastMean;
import ch.ethz.idsc.sophus.hs.sn.SnGeodesic;
import ch.ethz.idsc.sophus.hs.sn.SnLineDistance;
import ch.ethz.idsc.sophus.hs.sn.SnManifold;
import ch.ethz.idsc.sophus.hs.sn.SnMetric;
import ch.ethz.idsc.sophus.hs.sn.SnRandomSample;
import ch.ethz.idsc.sophus.hs.sn.SnTransport;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.r2.CirclePoints;
import ch.ethz.idsc.tensor.sca.Abs;

/** symmetric positive definite 2 x 2 matrices */
public abstract class SnGeodesicDisplay implements GeodesicDisplay, Serializable {
  private static final long serialVersionUID = -7565465166604802848L;
  private static final Tensor CIRCLE = CirclePoints.of(15).multiply(RealScalar.of(0.05)).unmodifiable();
  // ---
  private final int dimensions;

  protected SnGeodesicDisplay(int dimensions) {
    this.dimensions = dimensions;
  }

  @Override // from GeodesicDisplay
  public final GeodesicInterface geodesicInterface() {
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
  public final HsExponential hsExponential() {
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
  public final boolean isMetricBiinvariant() {
    return true;
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
