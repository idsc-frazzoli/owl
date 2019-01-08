// code by jph
package ch.ethz.idsc.sophus.planar;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import junit.framework.TestCase;

public class Normal2DTest extends TestCase {
  public void testStringLength() {
    Distribution distribution = NormalDistribution.standard();
    for (int count = 0; count < 10; ++count) {
      Tensor tensor = RandomVariate.of(distribution, count, 2);
      Tensor string = Normal2D.string(tensor);
      assertEquals(string.length(), count);
    }
  }

  public void testStringZerosLength() {
    for (int count = 0; count < 10; ++count) {
      Tensor tensor = Array.zeros(count, 2);
      Tensor string = Normal2D.string(tensor);
      assertEquals(string.length(), count);
      assertEquals(tensor, string);
    }
  }
}
