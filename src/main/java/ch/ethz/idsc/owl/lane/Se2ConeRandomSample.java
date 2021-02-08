// code by gjoel, jph
package ch.ethz.idsc.owl.lane;

import java.io.Serializable;
import java.util.Random;

import ch.ethz.idsc.owl.bot.se2.Se2ComboRegion;
import ch.ethz.idsc.sophus.lie.se2.Se2GroupElement;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.r2.AngleVector;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Clips;

/** @see Se2ComboRegion */
public class Se2ConeRandomSample implements RandomSampleInterface, Serializable {
  /** @param apex vector of the form {x, y, angle}
   * @param semi non-negative
   * @param heading non-negative
   * @param depth non-negative
   * @return */
  public static RandomSampleInterface of(Tensor apex, Scalar semi, Scalar heading, Scalar depth) {
    return new Se2ConeRandomSample( //
        apex, //
        UniformDistribution.of(Clips.absolute(semi)), //
        UniformDistribution.of(Clips.absolute(heading)), //
        UniformDistribution.of(Clips.positive(depth)));
  }

  /***************************************************/
  private final Se2GroupElement se2GroupElement;
  private final Distribution distributionDepth;
  private final Distribution distributionAngle;
  private final Distribution distributionHeading;

  /** @param apex vector of the form {x, y, angle}
   * @param distributionSemi values not outside the interval [-pi, pi]
   * @param distributionHeading values not outside the interval [-pi, pi]
   * @param distributionRadius non-negative
   * @return */
  public Se2ConeRandomSample( //
      Tensor apex, //
      Distribution distributionSemi, //
      Distribution distributionHeading, //
      Distribution distributionDepth) {
    se2GroupElement = new Se2GroupElement(apex);
    this.distributionAngle = distributionSemi;
    this.distributionHeading = distributionHeading;
    this.distributionDepth = distributionDepth;
  }

  @Override // from RandomSampleInterface
  public Tensor randomSample(Random random) {
    Tensor xy = AngleVector.of(RandomVariate.of(distributionAngle, random)).multiply(RandomVariate.of(distributionDepth, random));
    return se2GroupElement.combine(xy.append(RandomVariate.of(distributionHeading, random)));
  }
}
