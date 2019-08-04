// code by jph
package ch.ethz.idsc.sophus.hs.sn;

import java.io.IOException;
import java.util.Random;

import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class SnRandomSampleTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    RandomSampleInterface randomSampleInterface = Serialization.copy(SnRandomSample.of(3));
    Tensor tensor = randomSampleInterface.randomSample(new Random());
    Chop._12.requireClose(Norm._2.ofVector(tensor), RealScalar.ONE);
  }
}
