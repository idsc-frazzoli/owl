// code by gjoel
package ch.ethz.idsc.owl.math.lane;

import java.io.Serializable;
import java.util.Random;

import ch.ethz.idsc.owl.math.region.ConeRegion;
import ch.ethz.idsc.owl.math.region.SphericalRegion;
import ch.ethz.idsc.sophus.lie.se2.Se2GroupElement;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.sophus.math.sample.BallRandomSample;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.sophus.math.sample.RegionRandomSample;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.lie.AngleVector;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.qty.Degree;
import ch.ethz.idsc.tensor.sca.Sign;

public class LaneRandomSample implements RandomSampleInterface, Serializable {
  // public static RandomSampleInterface along(SplitInterface geodesicInterface, Scalar width, Tensor... controlPoints) {
  // return along(geodesicInterface, width, Tensors.of(controlPoints));
  // }
  //
  // public static RandomSampleInterface along(SplitInterface geodesicInterface, Scalar width, Collection<Tensor> controlPoints) {
  // return along(geodesicInterface, width, Tensor.of(controlPoints.stream()));
  // }
  //
  // public static RandomSampleInterface along(SplitInterface geodesicInterface, Scalar width, Tensor controlPoints) {
  // return new LaneRandomSample(StableLane.of(geodesicInterface, controlPoints, width));
  // }
  public static RandomSampleInterface along(LaneInterface laneInterface) {
    return new LaneRandomSample(laneInterface);
  }

  public static RandomSampleInterface startSample(LaneInterface laneInterface) {
    return new LaneRandomSample(laneInterface).around(0);
  }

  public static RegionRandomSample endSample(LaneInterface laneInterface) {
    return RegionRandomSample.combine( //
        new LaneRandomSample(laneInterface).around(laneInterface.midLane().length() - 1), //
        new SphericalRegion(Extract2D.FUNCTION.apply(Last.of(laneInterface.midLane())), Last.of(laneInterface.margins()).Get()));
  }

  public static RegionRandomSample endSample(LaneInterface laneInterface, Scalar mu_r, Scalar semi) {
    Sign.requirePositive(semi);
    Tensor point = Last.of(laneInterface.midLane());
    RandomSampleInterface randomSampleInterface = new RandomSampleInterface() {
      @Override
      public Tensor randomSample(Random random) {
        Scalar r = RandomVariate.of(NormalDistribution.standard(), random).abs().multiply(mu_r);
        Scalar a1 = RandomVariate.of(UniformDistribution.of(semi.negate(), semi), random);
        Tensor xy = AngleVector.of(a1).multiply(r);
        Scalar a2 = RandomVariate.of(NormalDistribution.standard(), random).multiply(MU_A);
        return new Se2GroupElement(point).combine(xy.append(a2));
      }
    };
    return RegionRandomSample.combine(randomSampleInterface, new ConeRegion(point, semi));
  }

  protected RandomSampleInterface around(int index) {
    return around(laneInterface.midLane().get(index), laneInterface.margins().Get(index));
  }

  // ---
  // TODO GJOEL/JPH magic const
  private final static Scalar MU_A = Degree.of(18);
  // ---
  public final LaneInterface laneInterface;

  protected LaneRandomSample(LaneInterface lane) {
    this.laneInterface = lane;
  }

  @Override // from RandomSampleInterface
  public Tensor randomSample(Random random) {
    // RandomVariate.of(UniformDistribution.of(0, laneInterface.midLane().length() - 1)).number().intValue();
    int index = random.nextInt(laneInterface.midLane().length());
    return around(index).randomSample(random);
  }

  private static RandomSampleInterface around(Tensor point, Scalar radius) {
    return new RandomSampleInterface() {
      @Override // from RandomSampleInterface
      public Tensor randomSample(Random random) {
        Tensor xy = BallRandomSample.of(Extract2D.FUNCTION.apply(point), radius).randomSample(random);
        Scalar a = RandomVariate.of(NormalDistribution.of(point.Get(2), MU_A));
        return xy.append(a);
      }
    };
  }
}
