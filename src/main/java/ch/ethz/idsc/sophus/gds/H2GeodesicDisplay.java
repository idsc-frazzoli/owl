// code by jph
package ch.ethz.idsc.sophus.gds;

import java.util.Random;

import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.hs.h2.H2Manifold;
import ch.ethz.idsc.sophus.hs.hn.HnWeierstrassCoordinate;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;

public class H2GeodesicDisplay extends HnGeodesicDisplay {
  private static final long serialVersionUID = -4027571410920102690L;
  public static final GeodesicDisplay INSTANCE = new H2GeodesicDisplay();
  private static final Scalar RADIUS = RealScalar.of(2.5);

  /***************************************************/
  private H2GeodesicDisplay() {
    super(2);
  }

  @Override // from GeodesicDisplay
  public Tensor project(Tensor xya) {
    return HnWeierstrassCoordinate.toPoint(xya.extract(0, 2));
  }

  @Override // from GeodesicDisplay
  public VectorLogManifold vectorLogManifold() {
    return H2Manifold.INSTANCE;
  }

  @Override // from GeodesicDisplay
  public Tensor toPoint(Tensor p) {
    return p.extract(0, 2);
  }

  @Override // from GeodesicDisplay
  public Tensor matrixLift(Tensor p) {
    return Se2Matrix.translation(p);
  }

  @Override // from GeodesicDisplay
  public GeodesicArrayPlot geodesicArrayPlot() {
    return new H2ArrayPlot(RADIUS);
  }

  @Override // from GeodesicDisplay
  public RandomSampleInterface randomSampleInterface() {
    Distribution distribution = UniformDistribution.of(RADIUS.negate(), RADIUS);
    return new RandomSampleInterface() {
      @Override
      public Tensor randomSample(Random random) {
        return VectorQ.requireLength(RandomVariate.of(distribution, random, 2).append(RealScalar.ZERO), 3);
      }
    };
  }
}
