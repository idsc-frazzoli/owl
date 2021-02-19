// code by gjoel
package ch.ethz.idsc.owl.lane;

import java.io.Serializable;
import java.util.Random;

import ch.ethz.idsc.sophus.lie.se2.Se2GroupElement;
import ch.ethz.idsc.sophus.math.d2.Extract2D;
import ch.ethz.idsc.sophus.math.sample.BallRandomSample;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;

public class LaneRandomSample implements RandomSampleInterface, Serializable {
  /** @param laneInterface
   * @param distribution
   * @return */
  public static LaneRandomSample of(LaneInterface laneInterface, Distribution distribution) {
    return new LaneRandomSample(laneInterface, distribution);
  }

  /***************************************************/
  private final LaneInterface laneInterface;
  private final Distribution distribution;

  private LaneRandomSample(LaneInterface laneInterface, Distribution distribution) {
    this.laneInterface = laneInterface;
    this.distribution = distribution;
  }

  @Override // from RandomSampleInterface
  public Tensor randomSample(Random random) {
    return around(random.nextInt(laneInterface.midLane().length())).randomSample(random);
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
        Scalar rot = RandomVariate.of(distribution, random);
        return se2GroupElement.combine(trans.append(rot));
      }
    };
  }
}
