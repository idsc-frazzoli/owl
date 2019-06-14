// code by jph
package ch.ethz.idsc.owl.math.sample;

import java.util.Random;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.AngleVector;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;

/** sample from unit circle S2 */
/* package */ enum CircleRandomSample implements RandomSampleInterface {
  INSTANCE;
  // ---
  private static final Distribution DISTRIBUTION = UniformDistribution.of(Pi.VALUE.negate(), Pi.VALUE);

  @Override // from RandomSampleInterface
  public Tensor randomSample(Random random) {
    return AngleVector.of(RandomVariate.of(DISTRIBUTION));
  }
}
