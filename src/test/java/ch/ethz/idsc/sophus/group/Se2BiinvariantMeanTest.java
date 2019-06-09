// code by ob
package ch.ethz.idsc.sophus.group;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.io.Primitives;
import ch.ethz.idsc.tensor.lie.Permutations;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Sqrt;
import junit.framework.TestCase;

public class Se2BiinvariantMeanTest extends TestCase {
  // This test is from the paper:
  // Source: "Bi-invariant Means in Lie Groups. Application toLeft-invariant Polyaffine Transformations." p38
  public void testArsignyPennec() {
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
    Double nom = Math.sqrt(2) - Math.PI / 4;
    Double denom = 1 + Math.PI / 4 * (Math.sqrt(2) / (2 - Math.sqrt(2)));
    Tensor expected = Tensors.vector(nom / denom, 0, 0);
    for (Se2BiinvariantMean se2BiinvariantMean : Se2BiinvariantMean.values()) {
      Tensor actual = se2BiinvariantMean.mean(sequence, weights);
      Tensor actualUnordered = se2BiinvariantMean.mean(sequenceUnordered, weights);
      // ---
      Chop._14.requireClose(expected, actual);
      Chop._14.requireClose(actualUnordered, actual);
    }
  }

  public void testTrivial() {
    Tensor p = Tensors.of(Tensors.vector(1, 9, -1));
    Tensor weights = Tensors.vector(1);
    for (Se2BiinvariantMean se2BiinvariantMean : Se2BiinvariantMean.values()) {
      Tensor actual = se2BiinvariantMean.mean(p, weights);
      Chop._14.requireClose(p.get(0), actual);
    }
  }

  public void testTranslation() {
    Tensor sequence = Tensors.empty();
    Tensor p = Tensors.vector(1, 1, 0);
    for (int index = 0; index < 7; ++index)
      sequence.append(p.multiply(RealScalar.of(index)));
    Tensor weights = Tensors.vector(0.05, 0.1, 0.2, 0.3, 0.2, 0.1, 0.05);
    for (Se2BiinvariantMean se2BiinvariantMean : Se2BiinvariantMean.values()) {
      Tensor actual = se2BiinvariantMean.mean(sequence, weights);
      Chop._14.requireClose(Tensors.vector(3, 3, 0), actual);
    }
  }

  public void testRotation() {
    Tensor sequence = Tensors.empty();
    Tensor p = Tensors.vector(0, 0, 0.2);
    for (int index = 0; index < 7; ++index)
      sequence.append(p.multiply(RealScalar.of(index)));
    Tensor weights = Tensors.vector(0.05, 0.1, 0.2, 0.3, 0.2, 0.1, 0.05);
    for (Se2BiinvariantMean se2BiinvariantMean : Se2BiinvariantMean.values()) {
      Tensor actual = se2BiinvariantMean.mean(sequence, weights);
      Chop._14.requireClose(Tensors.vector(0, 0, 0.6), actual);
    }
  }

  public void testOrderInvariance() {
    Tensor p = Tensors.vector(4.9, 4.9, 0.9);
    Tensor q = Tensors.vector(5.0, 5.0, 1.0);
    Tensor r = Tensors.vector(5.1, 5.1, 1.1);
    Tensor sequence1 = Tensors.of(q, r, p);
    Tensor sequence2 = Tensors.of(r, p, q);
    Tensor sequence3 = Tensors.of(p, q, r);
    Tensor weights = Tensors.vector(1, 1, 1).divide(RealScalar.of(3));
    for (Se2BiinvariantMean se2BiinvariantMean : Se2BiinvariantMean.values()) {
      Tensor actual1 = se2BiinvariantMean.mean(sequence1, weights);
      Tensor actual2 = se2BiinvariantMean.mean(sequence2, weights);
      Tensor actual3 = se2BiinvariantMean.mean(sequence3, weights);
      Chop._14.requireClose(actual1, actual2);
      Chop._14.requireClose(actual1, actual3);
    }
  }

  public void testOrderInvariance2() {
    Tensor p = Tensors.vector(4.9, 4.9, 0.9);
    Tensor q = Tensors.vector(5.0, 5.0, 1.0);
    Tensor r = Tensors.vector(5.1, 5.1, 1.1);
    Tensor s = Tensors.vector(4.8, 5.2, 1.3);
    Tensor sequence = Tensors.of(p, q, r, s);
    Tensor weights = Tensors.vector(3, 2, 1, 4).divide(RealScalar.of(10));
    for (Se2BiinvariantMean se2BiinvariantMean : Se2BiinvariantMean.values()) {
      Tensor solution = se2BiinvariantMean.mean(sequence, weights);
      // Chop._12.requireClose(solution, Tensors.vector(4.911144632104387, 5.064995814659804, 1.1));
      for (Tensor perm : Permutations.of(Range.of(0, weights.length()))) {
        int[] index = Primitives.toIntArray(perm);
        Tensor result = se2BiinvariantMean.mean(TestHelper.order(sequence, index), TestHelper.order(weights, index));
        Chop._12.requireClose(result, solution);
      }
    }
  }

  public void testOrderInvariance3() {
    Tensor p = Tensors.fromString("{4.9[m], 4.9[m], 0.9}");
    Tensor q = Tensors.fromString("{5.0[m], 5.0[m], 1.0}");
    Tensor r = Tensors.fromString("{5.1[m], 5.1[m], 1.1}");
    Tensor s = Tensors.fromString("{4.8[m], 5.2[m], 1.3}");
    Tensor sequence = Tensors.of(p, q, r, s);
    Tensor weights = Tensors.vector(3, 2, 1, 4).divide(RealScalar.of(10));
    for (Se2BiinvariantMean se2BiinvariantMean : Se2BiinvariantMean.values()) {
      Tensor solution = se2BiinvariantMean.mean(sequence, weights);
      boolean close1 = Chop._12.close(solution, Tensors.fromString("{4.911144632104387[m], 5.064995814659804[m], 1.1}"));
      boolean close2 = Chop._12.close(solution, Tensors.fromString("{4.911658712738642[m], 5.064497410160735[m], 1.0998987355880372}"));
      assertTrue(close1 || close2);
      for (Tensor perm : Permutations.of(Range.of(0, weights.length()))) {
        int[] index = Primitives.toIntArray(perm);
        Tensor result = se2BiinvariantMean.mean(TestHelper.order(sequence, index), TestHelper.order(weights, index));
        Chop._12.requireClose(result, solution);
      }
    }
  }

  public void testOrderInvarianceNegative() {
    Tensor p = Tensors.vector(14.9, -4.9, 10.9);
    Tensor q = Tensors.vector(15.0, -5.0, 11.0);
    Tensor r = Tensors.vector(15.1, -5.1, 11.1);
    Tensor s = Tensors.vector(14.8, -5.2, 11.3);
    Tensor sequence = Tensors.of(p, q, r, s);
    Tensor mask = Tensors.vector(3, 2, -1, 4);
    Tensor weights = mask.divide(Total.ofVector(mask));
    for (Se2BiinvariantMean se2BiinvariantMean : Se2BiinvariantMean.values()) {
      Tensor solution = se2BiinvariantMean.mean(sequence, weights);
      // System.out.println(solution);
      // Chop._12.requireClose(solution, Tensors.vector(14.83619642851975, -5.043678108261259, -1.466370614359171));
      for (Tensor perm : Permutations.of(Range.of(0, weights.length()))) {
        int[] index = Primitives.toIntArray(perm);
        Tensor result = se2BiinvariantMean.mean(TestHelper.order(sequence, index), TestHelper.order(weights, index));
        Chop._12.requireClose(result, solution);
      }
    }
  }

  public void testPermutations() {
    for (int length = 1; length < 6; ++length) {
      Distribution distribution = UniformDistribution.of(Clips.absolute(Pi.VALUE));
      // here, we hope that no antipodal points are generated
      Tensor sequence = RandomVariate.of(distribution, length, 3);
      Tensor weights = RandomVariate.of(UniformDistribution.unit(), length);
      weights = weights.divide(Total.ofVector(weights));
      Tensor solution = Se2BiinvariantMean.GLOBAL.mean(sequence, weights);
      for (Tensor perm : Permutations.of(Range.of(0, weights.length()))) {
        int[] index = Primitives.toIntArray(perm);
        Tensor result = Se2BiinvariantMean.GLOBAL.mean(TestHelper.order(sequence, index), TestHelper.order(weights, index));
        Chop._12.requireClose(result, solution);
      }
    }
  }

  public void testPermutationsPiHalf() {
    for (int length = 1; length < 6; ++length) {
      Distribution distribution = UniformDistribution.of(Clips.absolute(Pi.HALF));
      // here, we hope that no antipodal points are generated
      Tensor sequence = RandomVariate.of(distribution, length, 3);
      Tensor weights = RandomVariate.of(UniformDistribution.unit(), length);
      weights = weights.divide(Total.ofVector(weights));
      for (Se2BiinvariantMean se2BiinvariantMean : Se2BiinvariantMean.values()) {
        Tensor solution = se2BiinvariantMean.mean(sequence, weights);
        for (Tensor perm : Permutations.of(Range.of(0, weights.length()))) {
          int[] index = Primitives.toIntArray(perm);
          Tensor result = se2BiinvariantMean.mean(TestHelper.order(sequence, index), TestHelper.order(weights, index));
          Chop._12.requireClose(result, solution);
        }
      }
    }
  }

  public void testCombined() {
    Tensor sequence = Tensors.empty();
    Tensor p = Tensors.vector(1, 1, 0.1);
    for (int index = 0; index < 7; ++index)
      sequence.append(p.multiply(RealScalar.of(index)));
    Tensor weights = Tensors.vector(0.05, 0.1, 0.2, 0.3, 0.2, 0.1, 0.05);
    Tensor actual = Se2BiinvariantMean.GLOBAL.mean(sequence, weights);
    Tensor expected = Tensors.fromString("{3.105184243650884, 2.8948157563491153, 0.3}");
    Chop._14.requireClose(expected, actual);
  }

  public void testFail() {
    Tensor p = Tensors.vector(0, 0, 0);
    Tensor sequence = Tensors.of(p, p, p);
    Tensor weights = Tensors.vector(1, 1, 1);
    for (Se2BiinvariantMean se2BiinvariantMean : Se2BiinvariantMean.values())
      try {
        // non-normalized weights fail
        se2BiinvariantMean.mean(sequence, weights);
        fail();
      } catch (Exception exception) {
        // ---
      }
  }
}