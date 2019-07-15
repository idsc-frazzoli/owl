// code by gjoel
package ch.ethz.idsc.owl.math.lane;

import java.util.Collection;
import java.util.Random;

import ch.ethz.idsc.owl.math.sample.RandomSampleInterface;
import ch.ethz.idsc.owl.math.sample.SphereRandomSample;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.sophus.math.SplitInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.qty.Degree;
import ch.ethz.idsc.tensor.red.Norm;

public class LaneRandomSample implements RandomSampleInterface {
  public static LaneRandomSample along(SplitInterface geodesicInterface, Scalar width, Tensor... controlPoints) {
    return along(geodesicInterface, width, Tensors.of(controlPoints));
  }

  public static LaneRandomSample along(SplitInterface geodesicInterface, Scalar width, Collection<Tensor> controlPoints) {
    return along(geodesicInterface, width, Tensor.of(controlPoints.stream()));
  }

  public static LaneRandomSample along(SplitInterface geodesicInterface, Scalar width, Tensor controlPoints) {
    return new LaneRandomSample(StableLane.of(geodesicInterface, controlPoints, width));
  }

  public static LaneRandomSample along(LaneInterface lane) {
    return new LaneRandomSample(lane);
  }

  // ---
  private final static Scalar MU_A = Degree.of(18);
  private final Scalar halfWidth;
  public final Tensor refined;

  private LaneRandomSample(LaneInterface lane) {
    refined = lane.midLane();
    Tensor orth = lane.leftBoundary().get(0).subtract(lane.rightBoundary().get(0));
    Scalar width = Norm._2.of(Extract2D.FUNCTION.apply(orth));
    halfWidth = width.multiply(RationalScalar.HALF);
  }

  @Override // from RandomSampleInterface
  public Tensor randomSample(Random random) {
    int index = RandomVariate.of(UniformDistribution.of(0, refined.length() - 1)).number().intValue();
    return around(refined.get(index)).randomSample(random);
  }

  public RandomSampleInterface startSample() {
    return around(refined.get(0));
  }

  public RandomSampleInterface endSample() {
    return around(Last.of(refined));
  }

  private RandomSampleInterface around(Tensor point) {
    return new RandomSampleInterface() {
      @Override // from RandomSampleInterface
      public Tensor randomSample(Random random) {
        Tensor xy = SphereRandomSample.of(Extract2D.FUNCTION.apply(point), halfWidth).randomSample(random);
        Scalar a = RandomVariate.of(NormalDistribution.of(point.Get(2), MU_A));
        return xy.append(a);
      }
    };
  }
}
