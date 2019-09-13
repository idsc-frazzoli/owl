// code by jph
package ch.ethz.idsc.owl.math.lane;

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

public class Se2ConeRandomSampleTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    Tensor apex = Tensors.vector(-10, -92, -3);
    Scalar semi = RealScalar.of(.3);
    Scalar heading = RealScalar.ONE;
    Scalar depth = RationalScalar.HALF;
    RandomSampleInterface randomSampleInterface = Serialization.copy(Se2ConeRandomSample.of(apex, semi, heading, depth));
    Region<Tensor> region = Se2ComboRegion.cone(apex, semi, heading);
    Random random = new Random();
    for (int index = 0; index < 20; ++index) {
      Tensor randomSample = randomSampleInterface.randomSample(random);
      assertTrue(region.isMember(randomSample));
    }
  }
}
