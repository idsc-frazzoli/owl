// code by jph
package ch.ethz.idsc.owl.math;

import ch.ethz.idsc.tensor.ExactScalarQ;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Series;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class RootsTest extends TestCase {
  public void testLinearUniform() {
    Distribution distribution = UniformDistribution.of(-10, 10);
    for (int index = 0; index < 200; ++index) {
      Tensor coeffs = RandomVariate.of(distribution, 2);
      if (Scalars.nonZero(coeffs.Get(1))) {
        Tensor roots = Roots.of(coeffs);
        Tensor check = roots.map(Series.of(coeffs));
        assertTrue(Chop._12.allZero(check));
      } else
        System.out.println("skip " + coeffs);
    }
  }

  public void testQuadraticUniform() {
    Distribution distribution = UniformDistribution.of(-10, 10);
    for (int index = 0; index < 200; ++index) {
      Tensor coeffs = RandomVariate.of(distribution, 3);
      if (Scalars.nonZero(coeffs.Get(2))) {
        Tensor roots = Roots.of(coeffs);
        Tensor check = roots.map(Series.of(coeffs));
        assertTrue(Chop._12.allZero(check));
      } else
        System.out.println("skip " + coeffs);
    }
  }

  public void testQuadraticNormal() {
    Distribution distribution = NormalDistribution.standard();
    for (int index = 0; index < 200; ++index) {
      Tensor coeffs = RandomVariate.of(distribution, 3);
      Tensor roots = Roots.of(coeffs);
      Tensor check = roots.map(Series.of(coeffs));
      assertTrue(Chop._12.allZero(check));
    }
  }

  public void testQuadraticQuantity() {
    Tensor coeffs = Tensors.fromString("{21, - 10 [s^-1], +1 [s^-2]}");
    Tensor roots = Roots.of(coeffs);
    assertEquals(roots, Tensors.fromString("{3[s], 7[s]}"));
    assertTrue(ExactScalarQ.all(roots));
  }
}
