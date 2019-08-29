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

public class LaneRandomSample implements RandomSampleInterface, Serializable {
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
    Distribution distribution = UniformDistribution.of(semi.negate(), semi);
    Tensor point = Last.of(laneInterface.midLane());
    Se2GroupElement se2GroupElement = new Se2GroupElement(point);
    RandomSampleInterface randomSampleInterface = new RandomSampleInterface() {
      @Override
      public Tensor randomSample(Random random) {
        Scalar r = RandomVariate.of(NormalDistribution.standard(), random).abs().multiply(mu_r);
        Scalar a1 = RandomVariate.of(distribution, random);
        Tensor xy = AngleVector.of(a1).multiply(r);
        Scalar a2 = RandomVariate.of(rotDist, random);
        return se2GroupElement.combine(xy.append(a2));
      }
    };
    return RegionRandomSample.combine(randomSampleInterface, new ConeRegion(point, semi));
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

  private RandomSampleInterface around(int index) {
    return around(laneInterface.midLane().get(index), laneInterface.margins().Get(index));
  }

  private RandomSampleInterface around(Tensor point, Scalar radius) {
    RandomSampleInterface randomSampleInterface = //
        BallRandomSample.of(Extract2D.FUNCTION.apply(point).map(Scalar::zero), radius);
    Se2GroupElement se2GroupElement = new Se2GroupElement(point);
    return new RandomSampleInterface() {
      @Override // from RandomSampleInterface
      public Tensor randomSample(Random random) {
        Tensor trans = randomSampleInterface.randomSample(random);
        Scalar rot = RandomVariate.of(rotDist, random);
        return se2GroupElement.combine(trans.append(rot));
      }
    };
  }
}
