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
    Tensor sequenceUnordered = Tensors.of(p, r, q);
    Tensor weights = Tensors.vector(1, 1, 1).divide(RealScalar.of(3));
    // ---
    Double nom = Math.sqrt(2) - Math.PI / 4;
    Double denom = 1 + Math.PI / 4 * (Math.sqrt(2) / (2 - Math.sqrt(2)));
    Tensor expected = Tensors.vector(nom / denom, 0, 0);
    Tensor actual = Se2BiinvariantMean.INSTANCE.mean(sequence, weights);
    Tensor actualUnordered = Se2BiinvariantMean.INSTANCE.mean(sequenceUnordered, weights);
    // ---
    Chop._14.requireClose(expected, actual);
    Chop._14.requireClose(actualUnordered, actual);
  }

  public void testTrivial() {
    Tensor p = Tensors.of(Tensors.vector(1, 9, -1));
    Tensor weights = Tensors.vector(1);
    Tensor actual = Se2BiinvariantMean.INSTANCE.mean(p, weights);
    Chop._14.requireClose(p.get(0), actual);
  }

  public void testTranslation() {
    Tensor sequence = Tensors.empty();
    Tensor p = Tensors.vector(1, 1, 0);
    for (int index = 0; index < 7; ++index)
      sequence.append(p.multiply(RealScalar.of(index)));
    Tensor weights = Tensors.vector(0.05, 0.1, 0.2, 0.3, 0.2, 0.1, 0.05);
    Tensor actual = Se2BiinvariantMean.INSTANCE.mean(sequence, weights);
    Chop._14.requireClose(Tensors.vector(3, 3, 0), actual);
  }

  public void testRotation() {
    Tensor sequence = Tensors.empty();
    Tensor p = Tensors.vector(0, 0, 0.2);
    for (int index = 0; index < 7; ++index)
      sequence.append(p.multiply(RealScalar.of(index)));
    Tensor weights = Tensors.vector(0.05, 0.1, 0.2, 0.3, 0.2, 0.1, 0.05);
    Tensor actual = Se2BiinvariantMean.INSTANCE.mean(sequence, weights);
    Chop._14.requireClose(Tensors.vector(0, 0, 0.6), actual);
  }

  //
  public void testOrderInvariance() {
    Tensor p = Tensors.vector(4.9, 4.9, 0.9);
    Tensor q = Tensors.vector(5.0, 5.0, 1.0);
    Tensor r = Tensors.vector(5.1, 5.1, 1.1);
    Tensor sequence1 = Tensors.of(q, r, p);
    Tensor sequence2 = Tensors.of(r, p, q);
    Tensor sequence3 = Tensors.of(p, q, r);
    Tensor weights = Tensors.vector(1, 1, 1).divide(RealScalar.of(3));
    Tensor actual1 = Se2BiinvariantMean.INSTANCE.mean(sequence1, weights);
    Tensor actual2 = Se2BiinvariantMean.INSTANCE.mean(sequence2, weights);
    Tensor actual3 = Se2BiinvariantMean.INSTANCE.mean(sequence3, weights);
    Chop._14.requireClose(actual1, actual2);
    Chop._14.requireClose(actual1, actual3);
  }

  public void testCombined() {
    Tensor sequence = Tensors.empty();
    Tensor p = Tensors.vector(1, 1, 0.1);
    for (int index = 0; index < 7; ++index)
      sequence.append(p.multiply(RealScalar.of(index)));
    Tensor weights = Tensors.vector(0.05, 0.1, 0.2, 0.3, 0.2, 0.1, 0.05);
    Tensor actual = Se2BiinvariantMean.INSTANCE.mean(sequence, weights);
    Tensor expected = Tensors.fromString("{3.105184243650884, 2.8948157563491153, 0.3}");
    Chop._14.requireClose(expected, actual);
  }

  public void testFail() {
    Tensor p = Tensors.vector(0, 0, 0);
    Tensor sequence = Tensors.of(p, p, p);
    try {
      // non-normalized weights fail
      Tensor weights = Tensors.vector(1, 1, 1);
      Se2BiinvariantMean.INSTANCE.mean(sequence, weights);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      // non-positive weights fail
      Tensor weights = Tensors.vector(-0.2, 1.4, -0.2);
      Se2BiinvariantMean.INSTANCE.mean(sequence, weights);
      fail();
    } catch (Exception exception) {
      // ---
    }
    // TODO OB: revise this test as soon as a nice se2biinvariantmean exists
    // try {
    // // antipodal/cut locus check: max angle difference >= pi-C
    // Tensor q = Tensors.vector(1, 1, Math.PI / 2);
    // Tensor r = Tensors.vector(2, 2, Math.PI);
    // sequence = Tensors.of(p, q, r);
    // Tensor weights = Tensors.vector(1, 1, 1).divide(RealScalar.of(3));
    // Se2BiinvariantMean.INSTANCE.mean(sequence, weights);
    // fail();
    // } catch (Exception exception) {
    // }
  }
}