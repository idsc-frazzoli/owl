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
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Sign;

public class LaneRandomSample implements RandomSampleInterface, Serializable {
  // public static RandomSampleInterface along(SplitInterface geodesicInterface, Scalar width, Tensor... controlPoints, Distribution rotDist) {
  // return along(geodesicInterface, width, Tensors.of(controlPoints), rotDist);
  // }
  //
  // public static RandomSampleInterface along(SplitInterface geodesicInterface, Scalar width, Collection<Tensor> controlPoints, Distribution rotDist) {
  // return along(geodesicInterface, width, Tensor.of(controlPoints.stream()), rotDist);
  // }
  //
  // public static RandomSampleInterface along(SplitInterface geodesicInterface, Scalar width, Tensor controlPoints, Distribution rotDist) {
  // return new LaneRandomSample(StableLane.of(geodesicInterface, controlPoints, width), rotDist);
  // }
  public static RandomSampleInterface along(LaneInterface laneInterface, Distribution rotDist) {
    return new LaneRandomSample(laneInterface, rotDist);
  }

  public static RandomSampleInterface startSample(LaneInterface laneInterface, Distribution rotDist) {
    return new LaneRandomSample(laneInterface, rotDist).around(0);
  }

  public static RegionRandomSample endSample(LaneInterface laneInterface, Distribution rotDist) {
    return RegionRandomSample.combine( //
        new LaneRandomSample(laneInterface, rotDist).around(laneInterface.midLane().length() - 1), //
        new SphericalRegion(Extract2D.FUNCTION.apply(Last.of(laneInterface.midLane())), (Scalar) Last.of(laneInterface.margins())));
  }

  public static RegionRandomSample endSample(LaneInterface laneInterface, Distribution rotDist, Scalar mu_r, Scalar semi) {
    Sign.requirePositive(semi);
    Tensor point = Last.of(laneInterface.midLane());
    RandomSampleInterface randomSampleInterface = new RandomSampleInterface() {
      @Override
      public Tensor randomSample(Random random) {
        Scalar r = RandomVariate.of(NormalDistribution.standard(), random).abs().multiply(mu_r);
        Scalar a1 = RandomVariate.of(UniformDistribution.of(semi.negate(), semi), random);
        Tensor xy = AngleVector.of(a1).multiply(r);
        Scalar a2 = RandomVariate.of(rotDist, random);
        return new Se2GroupElement(point).combine(xy.append(a2));
      }
    };
    return RegionRandomSample.combine(randomSampleInterface, new ConeRegion(point, semi));
  }

  private RandomSampleInterface around(int index) {
    return around(laneInterface.midLane().get(index), laneInterface.margins().Get(index));
  }

  // ---
  public final LaneInterface laneInterface;
  private final Distribution rotDist;

  private LaneRandomSample(LaneInterface lane, Distribution rotDist) {
    this.laneInterface = lane;
    this.rotDist = rotDist;
  }

  @Override // from RandomSampleInterface
  public Tensor randomSample(Random random) {
    // RandomVariate.of(UniformDistribution.of(0, laneInterface.midLane().length() - 1)).number().intValue();
    int index = random.nextInt(laneInterface.midLane().length());
    return around(index).randomSample(random);
  }

  private RandomSampleInterface around(Tensor point, Scalar radius) {
    return new RandomSampleInterface() {
      @Override // from RandomSampleInterface
      public Tensor randomSample(Random random) {
        Tensor trans = BallRandomSample.of(Extract2D.FUNCTION.apply(point).map(Scalar::zero), radius).randomSample(random);
        Scalar rot = RandomVariate.of(rotDist, random);
        return new Se2GroupElement(point).combine(trans.append(rot));
      }
    };
  }
}
