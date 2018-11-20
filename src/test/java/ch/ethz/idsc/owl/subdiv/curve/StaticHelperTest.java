// code by jph
package ch.ethz.idsc.owl.subdiv.curve;

import ch.ethz.idsc.owl.math.BinomialWeights;
import ch.ethz.idsc.owl.math.SmoothingKernel;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class StaticHelperTest extends TestCase {
  public void testSplitsMean() {
    {
      Tensor tensor = StaticHelper.splits(SmoothingKernel.DIRICHLET.apply(1));
      assertEquals(tensor, Tensors.fromString("{1/3}"));
    }
    {
      Tensor tensor = StaticHelper.splits(SmoothingKernel.DIRICHLET.apply(2));
      assertEquals(tensor, Tensors.fromString("{1/2, 1/5}"));
    }
    {
      Tensor tensor = StaticHelper.splits(SmoothingKernel.DIRICHLET.apply(3));
      assertEquals(tensor, Tensors.fromString("{1/2, 1/3, 1/7}"));
    }
  }

  public void testSplitsBinomial() {
    {
      Tensor tensor = StaticHelper.splits(BinomialWeights.INSTANCE.apply(1));
      assertEquals(tensor, Tensors.fromString("{1/2}"));
    }
    {
      Tensor tensor = StaticHelper.splits(BinomialWeights.INSTANCE.apply(2));
      assertEquals(tensor, Tensors.fromString("{4/5, 3/8}"));
    }
    {
      Tensor tensor = StaticHelper.splits(BinomialWeights.INSTANCE.apply(3));
      assertEquals(tensor, Tensors.fromString("{6/7, 15/22, 5/16}"));
    }
  }

  public void testFail() {
    try {
      StaticHelper.splits(Tensors.vector(1, 2));
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }

  public void testNonSymmetric() {
    try {
      StaticHelper.splits(Tensors.vector(1, 2, 2));
      assertTrue(false);
    } catch (Exception exception) {
      // ---
    }
  }
}
