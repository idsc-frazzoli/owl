// code by ob
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Power;
import junit.framework.TestCase;

public class Se2BiinvariantMeanTest extends TestCase {
  // This test is from the paper:
  // Source: "Bi-invariant Means in Lie Groups. Application toLeft-invariant Polyaffine Transformations." p38
  public void testArsignyPennec() {
    Scalar TWO = RealScalar.of(2);
    Scalar ZERO = RealScalar.ZERO;
    Scalar rootOfTwo = Power.of(2, 0.5);
    Scalar rootOfTwoHalf = Power.of(2, -0.5);
    Scalar piFourth = Pi.HALF.divide(TWO);
    // ---
    Tensor p = Tensors.of(//
        rootOfTwoHalf.negate(), //
        rootOfTwoHalf, //
        piFourth);
    // ---
    Tensor q = Tensors.of(//
        rootOfTwo, //
        ZERO, //
        ZERO);
    // ---
    Tensor r = Tensors.of(//
        rootOfTwoHalf.negate(), //
        rootOfTwoHalf.negate(), //
        piFourth.negate());
    Tensor sequence = Tensors.of(p, q, r);
    Tensor weights = Tensors.vector(1, 1, 1).divide(RealScalar.of(3));
    // ---
    Double nom = Math.sqrt(2) - Math.PI / 4;
    Double denom = 1 + Math.PI / 4 * (Math.sqrt(2) / (2 - Math.sqrt(2)));
    Tensor expected = Tensors.vector(nom / denom, 0, 0);
    Tensor actual = Se2BiinvariantMean.INSTANCE.mean(sequence, weights);
    Chop._14.requireClose(expected, actual);
  }
}
