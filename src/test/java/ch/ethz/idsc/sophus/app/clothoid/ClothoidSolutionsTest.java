// code by jph
package ch.ethz.idsc.sophus.app.clothoid;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Sort;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Mod;
import junit.framework.TestCase;

public class ClothoidSolutionsTest extends TestCase {
  public void testS1Odd() {
    ClothoidSolutions cs1 = ClothoidSolutions.of(+0.1, 0.3);
    ClothoidSolutions cs2 = ClothoidSolutions.of(-0.1, 0.3);
    Tensor sol1 = cs1.lambdas();
    Tensor sol2 = Sort.of(cs2.lambdas().negate());
    Chop._04.requireClose(sol1, sol2);
  }

  public void testS2Even() {
    ClothoidSolutions cs1 = ClothoidSolutions.of(+0.1, +0.3);
    ClothoidSolutions cs2 = ClothoidSolutions.of(+0.1, -0.3);
    Tensor sol1 = cs1.lambdas();
    Tensor sol2 = cs2.lambdas();
    Chop._04.requireClose(sol1, sol2);
  }

  public void testRandomS1Odd() {
    Distribution distribution = UniformDistribution.of(-20, 20);
    for (int count = 0; count < 10; ++count) {
      Scalar s1 = RandomVariate.of(distribution);
      Scalar s2 = RandomVariate.of(distribution);
      ClothoidSolutions cs1 = ClothoidSolutions.of(s1, s2);
      ClothoidSolutions cs2 = ClothoidSolutions.of(s1.negate(), s2);
      Tensor sol1 = cs1.lambdas();
      Tensor sol2 = Sort.of(cs2.lambdas().negate());
      Chop._04.requireClose(sol1, sol2);
    }
  }

  public void testRandomS2Even() {
    Distribution distribution = UniformDistribution.of(-20, 20);
    for (int count = 0; count < 10; ++count) {
      Scalar s1 = RandomVariate.of(distribution);
      Scalar s2 = RandomVariate.of(distribution);
      ClothoidSolutions cs1 = ClothoidSolutions.of(s1, s2);
      ClothoidSolutions cs2 = ClothoidSolutions.of(s1, s2.negate());
      Tensor sol1 = cs1.lambdas();
      Tensor sol2 = cs2.lambdas();
      Chop._04.requireClose(sol1, sol2);
    }
  }

  public void testS1Period2Pi() {
    Distribution distribution = UniformDistribution.of(-20, 20);
    for (int count = 0; count < 10; ++count) {
      Scalar s1 = RandomVariate.of(distribution);
      Scalar s2 = RandomVariate.of(distribution);
      ClothoidSolutions cs1 = ClothoidSolutions.of(s1, s2);
      ClothoidSolutions cs2 = ClothoidSolutions.of(s1.add(Pi.TWO), s2);
      Tensor sol1 = cs1.lambdas();
      Tensor sol2 = cs2.lambdas();
      Chop._04.requireClose(sol1, sol2);
    }
  }

  public void testS1Period2PiMod() {
    Distribution distribution = UniformDistribution.of(-20, 20);
    for (int count = 0; count < 10; ++count) {
      Scalar s1 = RandomVariate.of(distribution);
      Scalar s2 = RandomVariate.of(distribution);
      ClothoidSolutions cs1 = ClothoidSolutions.of(s1, s2);
      ClothoidSolutions cs2 = ClothoidSolutions.of(Mod.function(Pi.TWO).apply(s1), s2);
      Tensor sol1 = cs1.lambdas();
      Tensor sol2 = cs2.lambdas();
      Chop._04.requireClose(sol1, sol2);
    }
  }

  public void testS1Period2PiMirror() {
    Distribution distribution = UniformDistribution.of(-20, 20);
    for (int count = 0; count < 10; ++count) {
      Scalar s1 = RandomVariate.of(distribution);
      Scalar s2 = RandomVariate.of(distribution);
      ClothoidSolutions cs1 = ClothoidSolutions.of(s1, s2);
      Scalar r1 = Mod.function(Pi.TWO).apply(s1);
      boolean mirror = Scalars.lessThan(Pi.VALUE, r1);
      if (mirror)
        r1 = Pi.TWO.subtract(r1);
      assertTrue(Scalars.lessThan(r1, Pi.VALUE));
      ClothoidSolutions cs2 = ClothoidSolutions.of(r1, s2);
      Tensor sol1 = cs1.lambdas();
      if (mirror)
        Chop._04.requireClose(sol1, Sort.of(cs2.lambdas().negate()));
      else
        Chop._04.requireClose(sol1, cs2.lambdas());
    }
  }
}
