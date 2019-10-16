// code by jph
package ch.ethz.idsc.owl.lane;

import java.io.IOException;
import java.util.Random;

import ch.ethz.idsc.owl.bot.se2.Se2ComboRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class Se2SphereRandomSampleTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    Tensor apex = Tensors.vector(10, 20, 3);
    Scalar radius = RationalScalar.HALF;
    Scalar heading = RealScalar.ONE;
    RandomSampleInterface randomSampleInterface = Serialization.copy(Se2SphereRandomSample.of(apex, radius, heading));
    Region<Tensor> region = Se2ComboRegion.ball(apex, Tensors.of(radius, radius, heading));
    Random random = new Random();
    for (int index = 0; index < 20; ++index)
      assertTrue(region.isMember(randomSampleInterface.randomSample(random)));
  }
}
