// code by gjoel, jph
package ch.ethz.idsc.owl.lane;

import java.io.Serializable;
import java.util.Random;

import ch.ethz.idsc.owl.bot.se2.Se2ComboRegion;
import ch.ethz.idsc.sophus.lie.se2.Se2GroupElement;
import ch.ethz.idsc.sophus.math.d2.Extract2D;
import ch.ethz.idsc.sophus.math.sample.BallRandomSample;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Clips;

/** @see Se2ComboRegion */
public class Se2SphereRandomSample implements RandomSampleInterface, Serializable {
  /** @param apex
   * @param radius non-negative
   * @param heading non-negative
   * @return */
  public static RandomSampleInterface of(Tensor apex, Scalar radius, Scalar heading) {
    return new Se2SphereRandomSample(apex, radius, UniformDistribution.of(Clips.absolute(heading)));
  }

  /***************************************************/
  private final Se2GroupElement se2GroupElement;
  private final RandomSampleInterface randomSampleInterface;
  private final Distribution distribution;

  /** @param apex vector of the form {x, y, angle}
   * @param radius non-negative
   * @param distribution for heading */
  public Se2SphereRandomSample(Tensor apex, Scalar radius, Distribution distribution) {
    se2GroupElement = new Se2GroupElement(apex);
    randomSampleInterface = BallRandomSample.of(Extract2D.FUNCTION.apply(apex).map(Scalar::zero), radius);
    this.distribution = distribution;
  }

  @Override // from RandomSampleInterface
  public Tensor randomSample(Random random) {
    Tensor xy = randomSampleInterface.randomSample(random);
    return se2GroupElement.combine(xy.append(RandomVariate.of(distribution, random)));
  }
}
