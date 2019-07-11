// code by jph
package ch.ethz.idsc.sophus.crv.clothoid;

import ch.ethz.idsc.tensor.NumberQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import junit.framework.TestCase;

public class Clothoid1Test extends TestCase {
  public void testSimple() {
    ScalarTensorFunction scalarTensorFunction = Clothoid1.INSTANCE.curve(Tensors.vector(1, 2, 3), Array.zeros(3));
    assertTrue(scalarTensorFunction instanceof ClothoidCurve1);
  }

  public void testSingular() {
    Tensor beg = Tensors.vector(1, 2, 3);
    Tensor end = Tensors.vector(1, 2, -1);
    ScalarTensorFunction scalarTensorFunction = Clothoid1.INSTANCE.curve(beg, end);
    assertEquals(beg, scalarTensorFunction.apply(RealScalar.ZERO));
    assertEquals(end, scalarTensorFunction.apply(RealScalar.ONE));
    Tensor curve = Subdivide.of(0.0, 1.0, 50).map(scalarTensorFunction);
    assertTrue(NumberQ.all(curve));
    // System.out.println(Pretty.of(curve.map(Round._5)));
  }
}
