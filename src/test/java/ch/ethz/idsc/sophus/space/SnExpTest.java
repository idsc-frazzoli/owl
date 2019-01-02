// code by jph
package ch.ethz.idsc.sophus.space;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Normalize;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class SnExpTest extends TestCase {
  public void testSimple() {
    Tensor tensor = new SnExp(UnitVector.of(3, 0)).apply(UnitVector.of(3, 1).multiply(RealScalar.of(Math.PI / 2)));
    Chop._12.requireClose(tensor, UnitVector.of(3, 1));
  }

  public void test4D() {
    Tensor tensor = new SnExp(UnitVector.of(4, 0)).apply(UnitVector.of(4, 1).multiply(RealScalar.of(Math.PI)));
    Chop._12.requireClose(tensor, UnitVector.of(4, 0).negate());
  }

  public void testId() {
    for (int dim = 2; dim < 6; ++dim)
      for (int count = 0; count < 20; ++count) {
        Tensor point = Normalize.with(Norm._2).apply(RandomVariate.of(NormalDistribution.standard(), dim));
        Tensor apply = new SnExp(point).apply(point.map(Scalar::zero));
        assertEquals(point, apply);
      }
  }

  public void test0Fail() {
    try {
      new SnExp(Tensors.empty());
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void test1Fail() {
    try {
      new SnExp(UnitVector.of(1, 0));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
