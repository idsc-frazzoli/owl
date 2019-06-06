// code by jph
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class RnBiinvariantMeanTest extends TestCase {
  public void testSimple() {
    Distribution distribution = UniformDistribution.of(Clips.absolute(3));
    int length = 10;
    Tensor sequence = RandomVariate.of(distribution, length, 3);
    RnBiinvariantMean.INSTANCE.mean(sequence, Array.of(l -> RationalScalar.of(1, length), length));
  }

  public void testFail() {
    Distribution distribution = UniformDistribution.of(Clips.absolute(3));
    int length = 10;
    Tensor sequence = RandomVariate.of(distribution, length, 3);
    try {
      RnBiinvariantMean.INSTANCE.mean(sequence, Array.of(l -> RealScalar.ONE, length));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
