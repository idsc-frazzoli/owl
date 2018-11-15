// code by jph
package ch.ethz.idsc.owl.math.group;

import java.util.Arrays;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.lie.Rodrigues;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import junit.framework.TestCase;

public class LieDifferencesTest extends TestCase {
  public void testSimple() {
    Distribution distribution = UniformDistribution.unit();
    Tensor tensor = RandomVariate.of(distribution, 10, 4);
    LieDifferences lieDifferences = //
        new LieDifferences(RnGroup.INSTANCE, RnExponential.INSTANCE);
    assertEquals(lieDifferences.apply(tensor), Differences.of(tensor));
  }

  public void testSe2() {
    Distribution distribution = UniformDistribution.unit();
    Tensor tensor = RandomVariate.of(distribution, 10, 3);
    LieDifferences lieDifferences = //
        new LieDifferences(Se2Group.INSTANCE, Se2CoveringExponential.INSTANCE);
    assertEquals(Dimensions.of(lieDifferences.apply(tensor)), Arrays.asList(9, 3));
  }

  public void testSe3() {
    Distribution distribution = NormalDistribution.of(0, .1);
    Tensor tensor = Tensors.empty();
    for (int index = 0; index < 10; ++index)
      tensor.append(Se3Utils.toMatrix4x4( //
          Rodrigues.exp(RandomVariate.of(distribution, 3)), RandomVariate.of(distribution, 3)));
    LieDifferences lieDifferences = //
        new LieDifferences(LinearGroup.INSTANCE, Se3Exponential.INSTANCE);
    assertEquals(Dimensions.of(lieDifferences.apply(tensor)), Arrays.asList(9, 2, 3));
  }
}
