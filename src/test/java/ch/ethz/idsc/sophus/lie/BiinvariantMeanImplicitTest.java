// code by ob /jph
package ch.ethz.idsc.sophus.lie;

import ch.ethz.idsc.sophus.app.api.Se2GeodesicDisplay;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Sqrt;
import junit.framework.TestCase;

public class BiinvariantMeanImplicitTest extends TestCase {
  public void testSE2() {
    Scalar TWO = RealScalar.of(2);
    Scalar ZERO = RealScalar.ZERO;
    Scalar rootOfTwo = Sqrt.of(TWO);
    Scalar rootOfTwoHalf = rootOfTwo.reciprocal();
    Scalar piFourth = Pi.HALF.divide(TWO);
    // ---
    Tensor p = Tensors.of(rootOfTwoHalf.negate(), rootOfTwoHalf, piFourth);
    Tensor q = Tensors.of(rootOfTwo, ZERO, ZERO);
    Tensor r = Tensors.of(rootOfTwoHalf.negate(), rootOfTwoHalf.negate(), piFourth.negate());
    // ---
    Tensor sequence = Tensors.of(p, q, r);
    Tensor sequenceUnordered = Tensors.of(p, r, q);
    Tensor weights = Tensors.vector(1, 1, 1).divide(RealScalar.of(3));
    // ---
    double nom = Math.sqrt(2) - Math.PI / 4;
    double denom = 1 + Math.PI / 4 * (Math.sqrt(2) / (2 - Math.sqrt(2)));
    Tensor expected = Tensors.vector(nom / denom, 0, 0);
    BiinvariantMeanImplicit bMI = new BiinvariantMeanImplicit(Se2GeodesicDisplay.INSTANCE);
    Tensor actual = bMI.apply(sequenceUnordered, weights);
    Chop._12.requireClose(actual, expected);
  }
  // Tests form more groups however i think that e.g. HE1 could cause problems due to tensor of tensor structure.
}
