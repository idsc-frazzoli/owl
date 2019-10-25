// code by jph
package ch.ethz.idsc.sophus.crv.subdiv;

import ch.ethz.idsc.sophus.math.TensorIteration;
import ch.ethz.idsc.tensor.ExactTensorQ;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Reverse;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class RnHermite1SubdivisionTest extends TestCase {
  public void testString() {
    Tensor control = Tensors.fromString("{{0, 0}, {1, 0}, {0, -1}, {0, 0}}");
    TensorIteration hermiteSubdivision = RnHermite1Subdivisions.instance().string(RealScalar.ONE, control);
    Tensor iterate = hermiteSubdivision.iterate();
    Tensor expect = Tensors.fromString("{{0, 0}, {1/2, 3/2}, {1, 0}, {5/8, -5/4}, {0, -1}, {-1/8, 1/4}, {0, 0}}");
    assertEquals(iterate, expect);
    ExactTensorQ.require(iterate);
    iterate = hermiteSubdivision.iterate();
    // System.out.println(iterate);
    String string = "{{0, 0}, {5/32, 9/8}, {1/2, 3/2}, {27/32, 9/8}, {1, 0}, {57/64, -13/16}, {5/8, -5/4}, {19/64, -21/16}, {0, -1}, {-9/64, -3/16}, {-1/8, 1/4}, {-3/64, 5/16}, {0, 0}}";
    assertEquals(iterate, Tensors.fromString(string));
    ExactTensorQ.require(iterate);
  }

  public void testStringReverse() {
    Tensor cp1 = RandomVariate.of(NormalDistribution.standard(), 7, 2, 3);
    Tensor cp2 = cp1.copy();
    cp2.set(Tensor::negate, Tensor.ALL, 1);
    TensorIteration hs1 = RnHermite1Subdivisions.instance().string(RealScalar.ONE, cp1);
    TensorIteration hs2 = RnHermite1Subdivisions.instance().string(RealScalar.ONE, Reverse.of(cp2));
    for (int count = 0; count < 3; ++count) {
      Tensor result1 = hs1.iterate();
      Tensor result2 = Reverse.of(hs2.iterate());
      result2.set(Tensor::negate, Tensor.ALL, 1);
      Chop._12.requireClose(result1, result2);
    }
  }

  public void testCyclic() {
    Tensor control = Tensors.fromString("{{0, 0}, {1, 0}, {0, -1}, {-1/2, 1}}");
    TensorIteration tensorIteration = RnHermite1Subdivisions.instance().cyclic(RealScalar.ONE, control);
    Tensor iterate = tensorIteration.iterate();
    Tensor expect = Tensors.fromString("{{0, 0}, {1/2, 3/2}, {1, 0}, {5/8, -5/4}, {0, -1}, {-1/2, -3/4}, {-1/2, 1}, {-1/8, 1/2}}");
    assertEquals(iterate, expect);
    ExactTensorQ.require(iterate);
  }

  public void testPolynomialReproduction() {
    TestHelper.checkP(3, RnHermite1Subdivisions.instance());
  }
}
