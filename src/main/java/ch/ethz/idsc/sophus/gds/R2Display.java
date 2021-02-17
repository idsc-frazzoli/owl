// code by jph
package ch.ethz.idsc.sophus.gds;

import java.util.Random;

import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;

public class R2Display extends RnDisplay {
  private static final Scalar RADIUS = RealScalar.of(1.0);
  // ---
  public static final ManifoldDisplay INSTANCE = new R2Display();

  private R2Display() {
    super(2);
  }

  @Override // from GeodesicDisplay
  public Tensor toPoint(Tensor p) {
    return VectorQ.requireLength(p, 2);
  }

  @Override // from GeodesicDisplay
  public Tensor matrixLift(Tensor p) {
    return Se2Matrix.translation(p);
  }

  @Override // from GeodesicDisplay
  public GeodesicArrayPlot geodesicArrayPlot() {
    return new R2ArrayPlot(RADIUS);
  }

  @Override // from GeodesicDisplay
  public RandomSampleInterface randomSampleInterface() {
    Distribution distribution = UniformDistribution.of(RADIUS.negate(), RADIUS);
    return new RandomSampleInterface() {
      @Override
      public Tensor randomSample(Random random) {
        return RandomVariate.of(distribution, random, 2).append(RealScalar.ZERO);
      }
    };
  }
}
