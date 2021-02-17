// code by jph
package ch.ethz.idsc.sophus.gds;

import java.util.Arrays;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.ext.Serialization;
import junit.framework.TestCase;

public class GeodesicDisplaysTest extends TestCase {
  public void testSimple() {
    assertTrue(12 <= GeodesicDisplays.ALL.size());
  }

  public void testToPoint() {
    for (ManifoldDisplay geodesicDisplay : GeodesicDisplays.ALL)
      try {
        Tensor xya = Tensors.vector(1, 2, 3);
        Tensor p = Serialization.copy(geodesicDisplay).project(xya);
        VectorQ.requireLength(geodesicDisplay.toPoint(p), 2);
        Tensor matrix = geodesicDisplay.matrixLift(p);
        assertEquals(Dimensions.of(matrix), Arrays.asList(3, 3));
      } catch (Exception exception) {
        System.out.println(geodesicDisplay);
        exception.printStackTrace();
        fail();
      }
  }
}
