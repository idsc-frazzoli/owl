// code by jph
package ch.ethz.idsc.owl.math.group;

import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.MatrixQ;
import junit.framework.TestCase;

public class Se3GeodesicTest extends TestCase {
  public void testSimple() {
    Tensor p = Se3Exponential.INSTANCE.exp(Tensors.of(Tensors.vector(1, 2, 3), Tensors.vector(.2, .3, .4)));
    Tensor q = Se3Exponential.INSTANCE.exp(Tensors.of(Tensors.vector(3, 4, 5), Tensors.vector(-.1, .2, .2)));
    Tensor split = Se3Geodesic.INSTANCE.split(p, q, RationalScalar.HALF);
    assertTrue(MatrixQ.ofSize(split, 4, 4));
  }
}
