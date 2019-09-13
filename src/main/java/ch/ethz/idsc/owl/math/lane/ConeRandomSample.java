// code by jph
package ch.ethz.idsc.owl.math.lane;

import java.util.Random;

import ch.ethz.idsc.owl.math.region.ConeRegion;
import ch.ethz.idsc.sophus.lie.se2.Se2GroupElement;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.AngleVector;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Clips;

/** @see ConeRegion */
public class ConeRandomSample implements RandomSampleInterface {
  /** @param apex vector of the form {x, y, angle}
   * @param radius non-negative
   * @param semi non-negative
   * @param heading non-negative
   * @return */
  public static RandomSampleInterface of(Tensor apex, Scalar radius, Scalar semi, Scalar heading) {
    return new ConeRandomSample( //
        apex, //
        UniformDistribution.of(Clips.positive(radius)), //
        UniformDistribution.of(Clips.absolute(semi)), //
        UniformDistribution.of(Clips.absolute(heading)));
  }

  // ---
  private final Se2GroupElement se2GroupElement;
  private final Distribution distributionRadius;
  private final Distribution distributionAngle;
  private final Distribution distributionHeading;

  /** @param apex vector of the form {x, y, angle}
   * @param distributionRadius non-negative
   * @param distributionAngle values not outside the interval [-pi, pi]
   * @param distributionHeading values not outside the interval [-pi, pi]
   * @return */
  public ConeRandomSample( //
      Tensor apex, //
      Distribution distributionRadius, //
      Distribution distributionAngle, //
      Distribution distributionHeading) {
    se2GroupElement = new Se2GroupElement(apex);
    this.distributionRadius = distributionRadius;
    this.distributionAngle = distributionAngle;
    this.distributionHeading = distributionHeading;
  }

  @Override // from RandomSampleInterface
  public Tensor randomSample(Random random) {
    Tensor xy = AngleVector.of(RandomVariate.of(distributionAngle, random)).multiply(RandomVariate.of(distributionRadius, random));
    return se2GroupElement.combine(xy.append(RandomVariate.of(distributionHeading, random)));
  }
}
