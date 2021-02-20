// code by jph
package ch.ethz.idsc.sophus.gds;

import java.util.Random;

import ch.ethz.idsc.sophus.bm.BiinvariantMean;
import ch.ethz.idsc.sophus.hs.HsManifold;
import ch.ethz.idsc.sophus.hs.r2.Se2Parametric;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.se2.Se2BiinvariantMeans;
import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.sophus.lie.se2.Se2Group;
import ch.ethz.idsc.sophus.lie.se2.Se2Manifold;
import ch.ethz.idsc.sophus.lie.so2.So2;
import ch.ethz.idsc.sophus.math.Geodesic;
import ch.ethz.idsc.sophus.math.TensorMetric;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.num.Pi;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;

public class Se2Display extends Se2AbstractDisplay {
  public static final ManifoldDisplay INSTANCE = new Se2Display();

  /***************************************************/
  private Se2Display() {
    // ---
  }

  @Override // from GeodesicDisplay
  public Geodesic geodesicInterface() {
    return Se2Geodesic.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public Tensor project(Tensor xya) {
    Tensor xym = xya.copy();
    xym.set(So2.MOD, 2);
    return xym;
  }

  @Override // from GeodesicDisplay
  public LieGroup lieGroup() {
    return Se2Group.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public LieExponential lieExponential() {
    return Se2Manifold.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public HsManifold hsManifold() {
    return Se2Manifold.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public TensorMetric parametricDistance() {
    return Se2Parametric.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public BiinvariantMean biinvariantMean() {
    return Se2BiinvariantMeans.FILTER;
  }

  @Override // from GeodesicDisplay
  public RandomSampleInterface randomSampleInterface() {
    double lim = 3;
    Distribution distribution = UniformDistribution.of(-lim, lim);
    return new RandomSampleInterface() {
      @Override
      public Tensor randomSample(Random random) {
        return RandomVariate.of(distribution, random, 2).append( //
            RandomVariate.of(UniformDistribution.of(Pi.VALUE.negate(), Pi.VALUE), random));
      }
    };
  }

  @Override // from Object
  public String toString() {
    return "SE2";
  }
}
