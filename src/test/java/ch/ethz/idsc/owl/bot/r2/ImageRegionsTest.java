// code by jph
package ch.ethz.idsc.owl.bot.r2;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.io.ResourceData;
import junit.framework.TestCase;

public class ImageRegionsTest extends TestCase {
  public void testSimple() {
    Tensor tensor = ResourceData.of("/io/track0_100.png");
    System.out.println(Dimensions.of(tensor));
  }
}
