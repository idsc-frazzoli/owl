// code by jph
package ch.ethz.idsc.sophus.gds;

import java.util.Random;

import ch.ethz.idsc.sophus.bm.BiinvariantMean;
import ch.ethz.idsc.sophus.hs.HsManifold;
import ch.ethz.idsc.sophus.hs.r2.Se2CoveringParametric;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringBiinvariantMean;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringGeodesic;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringGroup;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringManifold;
import ch.ethz.idsc.sophus.math.Geodesic;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.num.Pi;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;

public class Se2CoveringDisplay extends Se2AbstractDisplay {
  public static final ManifoldDisplay INSTANCE = new Se2CoveringDisplay();

  /***************************************************/
  private Se2CoveringDisplay() {
    // ---
  }

  @Override // from GeodesicDisplay
  public Geodesic geodesicInterface() {
    return Se2CoveringGeodesic.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public Tensor project(Tensor xya) {
    return xya.copy();
  }

  @Override // from GeodesicDisplay
  public LieGroup lieGroup() {
    return Se2CoveringGroup.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public LieExponential lieExponential() {
    return Se2CoveringManifold.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public HsManifold hsManifold() {
    return Se2CoveringManifold.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public TensorMetric parametricDistance() {
    return Se2CoveringParametric.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public BiinvariantMean biinvariantMean() {
    return Se2CoveringBiinvariantMean.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public RandomSampleInterface randomSampleInterface() {
    double lim = 3;
    Distribution distribution = UniformDistribution.of(-lim, lim);
    return new RandomSampleInterface() {
      @Override
      public Tensor randomSample(Random random) {
        return RandomVariate.of(distribution, random, 2).append( //
            RandomVariate.of(UniformDistribution.of(Pi.TWO.negate(), Pi.TWO), random));
      }
    };
  }

  @Override // from Object
  public String toString() {
    return "SE2C";
  }
}
