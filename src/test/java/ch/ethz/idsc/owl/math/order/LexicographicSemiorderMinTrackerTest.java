// code by astoll
package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class LexicographicSemiorderMinTrackerTest extends TestCase {
  public void testDigestSimple() {
    Tensor slackVector = Tensors.fromString("{1,1,1}");
    LexicographicSemiorderMinTracker<Integer> LSMT1 = LexicographicSemiorderMinTracker.withList(slackVector);
    LexicographicSemiorderMinTracker<Integer> LSMT2 = LexicographicSemiorderMinTracker.withSet(slackVector);
    Tensor x = Tensors.fromString("{1,2,2}");
    LSMT1.digest(1, x);
    LSMT1.digest(2, x);
    LSMT2.digest(1, x);
    LSMT2.digest(2, x);
    assertTrue(!LSMT1.getCandidateSet().isEmpty() && LSMT1.getCandidateSet().size() > 1);
    assertTrue(!LSMT2.getCandidateSet().isEmpty() && LSMT2.getCandidateSet().size() > 1);
  }

  public void testDigestFalseDim() {
    Tensor slackVector = Tensors.fromString("{1,1,1}");
    LexicographicSemiorderMinTracker<Integer> LSMT1 = LexicographicSemiorderMinTracker.withList(slackVector);
    Tensor x = Tensors.fromString("{1,2,2,3}");
    try {
      LSMT1.digest(1, x);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testGetMinElements() {
    Tensor slackVector = Tensors.fromString("{1,1,1}");
    LexicographicSemiorderMinTracker<Integer> LSMT1 = LexicographicSemiorderMinTracker.withList(slackVector);
    Tensor x = Tensors.fromString("{1,4,4}");
    Tensor y = Tensors.fromString("{2,3,1}");
    Tensor z = Tensors.fromString("{3,2,2}");
    Tensor w = Tensors.fromString("{4,1,3}");
    Tensor u = Tensors.fromString("{1,1,1}");
    LSMT1.digest(1, x);
    System.out.println(LSMT1.getMinElements());
  }
  //
  // public void testReverseSequencewithList() {
  // Tensor slackVector = Tensors.fromString("{1,1,1}");
  // LexicographicSemiorderMinTracker LSMT1 = LexicographicSemiorderMinTracker.withList(slackVector);
  // LexicographicSemiorderMinTracker LSMT2 = LexicographicSemiorderMinTracker.withList(slackVector);
  // LexicographicSemiorderMinTracker LSMT3 = LexicographicSemiorderMinTracker.withList(slackVector);
  // Tensor x = Tensors.fromString("{0,-1,1}");
  // Tensor y = Tensors.fromString("{-1,1,0}");
  // Tensor z = Tensors.fromString("{1,0,1}");
  // LSMT1.digest(x);
  // assertTrue(LSMT1.getCandidateSet().contains(x) && LSMT1.getCandidateSet().size() == 1);
  // LSMT1.digest(y);
  // assertTrue(LSMT1.getCandidateSet().contains(y) && LSMT1.getCandidateSet().size() > 1);
  // LSMT1.digest(z);
  // assertTrue(!LSMT1.getCandidateSet().contains(z) && LSMT1.getCandidateSet().size() > 1);
  // LSMT2.digest(y);
  // assertTrue(LSMT2.getCandidateSet().contains(y) && LSMT2.getCandidateSet().size() == 1);
  // LSMT2.digest(x);
  // assertTrue(LSMT2.getCandidateSet().contains(x) && LSMT2.getCandidateSet().size() > 1);
  // LSMT2.digest(z);
  // assertTrue(!LSMT2.getCandidateSet().contains(z) && LSMT2.getCandidateSet().size() > 1);
  // LSMT3.digest(z);
  // assertTrue(LSMT3.getCandidateSet().contains(z) && LSMT3.getCandidateSet().size() == 1);
  // LSMT3.digest(x);
  // assertTrue(LSMT3.getCandidateSet().contains(z) && LSMT3.getCandidateSet().contains(x) && LSMT3.getCandidateSet().size() > 1);
  // LSMT3.digest(y);
  // assertTrue(LSMT3.getCandidateSet().contains(y) && !LSMT3.getCandidateSet().contains(z) && LSMT2.getCandidateSet().size() > 1);
  // assertTrue(LSMT1.getMinElements().contains(x) && LSMT1.getMinElements().size() == 1);
  // assertTrue(LSMT2.getMinElements().contains(x) && LSMT2.getMinElements().size() == 1);
  // assertTrue(LSMT3.getMinElements().contains(x) && LSMT3.getMinElements().size() == 1);
  // assertTrue(LSMT1.getCandidateSet().containsAll(LSMT1.getMinElements()));
  // assertTrue(LSMT2.getCandidateSet().containsAll(LSMT2.getMinElements()));
  // assertTrue(LSMT3.getCandidateSet().containsAll(LSMT3.getMinElements()));
  // }
  //
  // public void testReverseSequencewithSet() {
  // Tensor slackVector = Tensors.fromString("{1,1,1}");
  // LexicographicSemiorderMinTracker LSMT1 = LexicographicSemiorderMinTracker.withSet(slackVector);
  // LexicographicSemiorderMinTracker LSMT2 = LexicographicSemiorderMinTracker.withSet(slackVector);
  // LexicographicSemiorderMinTracker LSMT3 = LexicographicSemiorderMinTracker.withSet(slackVector);
  // Tensor x = Tensors.fromString("{0,-1,1}");
  // Tensor y = Tensors.fromString("{-1,1,0}");
  // Tensor z = Tensors.fromString("{1,0,1}");
  // LSMT1.digest(x);
  // assertTrue(LSMT1.getCandidateSet().contains(x) && LSMT1.getCandidateSet().size() == 1);
  // LSMT1.digest(y);
  // assertTrue(LSMT1.getCandidateSet().contains(y) && LSMT1.getCandidateSet().size() > 1);
  // LSMT1.digest(z);
  // assertTrue(!LSMT1.getCandidateSet().contains(z) && LSMT1.getCandidateSet().size() > 1);
  // LSMT2.digest(y);
  // assertTrue(LSMT2.getCandidateSet().contains(y) && LSMT2.getCandidateSet().size() == 1);
  // LSMT2.digest(x);
  // assertTrue(LSMT2.getCandidateSet().contains(x) && LSMT2.getCandidateSet().size() > 1);
  // LSMT2.digest(z);
  // assertTrue(!LSMT2.getCandidateSet().contains(z) && LSMT2.getCandidateSet().size() > 1);
  // LSMT3.digest(z);
  // assertTrue(LSMT3.getCandidateSet().contains(z) && LSMT3.getCandidateSet().size() == 1);
  // LSMT3.digest(x);
  // assertTrue(LSMT3.getCandidateSet().contains(z) && LSMT3.getCandidateSet().contains(x) && LSMT3.getCandidateSet().size() > 1);
  // LSMT3.digest(y);
  // assertTrue(LSMT3.getCandidateSet().contains(y) && !LSMT3.getCandidateSet().contains(z) && LSMT2.getCandidateSet().size() > 1);
  // assertTrue(LSMT1.getMinElements().contains(x) && LSMT1.getMinElements().size() == 1);
  // assertTrue(LSMT2.getMinElements().contains(x) && LSMT2.getMinElements().size() == 1);
  // assertTrue(LSMT3.getMinElements().contains(x) && LSMT3.getMinElements().size() == 1);
  // assertTrue(LSMT1.getCandidateSet().containsAll(LSMT1.getMinElements()));
  // assertTrue(LSMT2.getCandidateSet().containsAll(LSMT2.getMinElements()));
  // assertTrue(LSMT3.getCandidateSet().containsAll(LSMT3.getMinElements()));
  // }
  //
  // public void testReverseSequencewithList2() {
  // Tensor slackVector = Tensors.fromString("{1,1,1}");
  // LexicographicSemiorderMinTracker LSMT1 = LexicographicSemiorderMinTracker.withList(slackVector);
  // LexicographicSemiorderMinTracker LSMT2 = LexicographicSemiorderMinTracker.withList(slackVector);
  // LexicographicSemiorderMinTracker LSMT3 = LexicographicSemiorderMinTracker.withList(slackVector);
  // Tensor x = Tensors.fromString("{1,0,4}");
  // Tensor y = Tensors.fromString("{1,2,0}");
  // Tensor z = Tensors.fromString("{2,1,4}");
  // LSMT1.digest(x);
  // assertTrue(LSMT1.getCandidateSet().contains(x) && LSMT1.getCandidateSet().size() == 1);
  // LSMT1.digest(y);
  // assertTrue(!LSMT1.getCandidateSet().contains(y) && LSMT1.getCandidateSet().size() == 1);
  // LSMT1.digest(z);
  // assertTrue(LSMT1.getCandidateSet().contains(z) && LSMT1.getCandidateSet().size() > 1);
  // LSMT2.digest(y);
  // assertTrue(LSMT2.getCandidateSet().contains(y) && LSMT2.getCandidateSet().size() == 1);
  // LSMT2.digest(x);
  // assertTrue(LSMT2.getCandidateSet().contains(x) && LSMT2.getCandidateSet().size() == 1);
  // LSMT2.digest(z);
  // assertTrue(LSMT2.getCandidateSet().contains(z) && LSMT2.getCandidateSet().size() > 1);
  // LSMT3.digest(z);
  // assertTrue(LSMT3.getCandidateSet().contains(z) && LSMT3.getCandidateSet().size() == 1);
  // LSMT3.digest(y);
  // assertTrue(LSMT3.getCandidateSet().contains(z) && LSMT3.getCandidateSet().contains(y) && LSMT3.getCandidateSet().size() > 1);
  // LSMT3.digest(x);
  // assertTrue(!LSMT3.getCandidateSet().contains(y) && LSMT3.getCandidateSet().contains(x) && LSMT3.getCandidateSet().contains(z)
  // && LSMT2.getCandidateSet().size() > 1);
  // assertTrue(LSMT1.getMinElements().contains(x) && LSMT1.getCandidateSet().contains(z) && LSMT1.getMinElements().size() > 1);
  // assertTrue(LSMT2.getMinElements().contains(x) && LSMT2.getCandidateSet().contains(z) && LSMT2.getMinElements().size() > 1);
  // assertTrue(LSMT3.getMinElements().contains(x) && LSMT3.getCandidateSet().contains(z) && LSMT3.getMinElements().size() > 1);
  // assertTrue(LSMT1.getCandidateSet().containsAll(LSMT1.getMinElements()));
  // assertTrue(LSMT2.getCandidateSet().containsAll(LSMT2.getMinElements()));
  // assertTrue(LSMT3.getCandidateSet().containsAll(LSMT3.getMinElements()));
  // }
  //
  // public void testPermutations() {
  // Tensor slackVector = Tensors.fromString("{1,1,1,1}");
  // LexicographicSemiorderMinTracker LSMT1 = LexicographicSemiorderMinTracker.withList(slackVector);
  // Tensor tensor = Permutations.of(Range.of(0, 4));
  // for (Tensor v : tensor) {
  // LSMT1.digest(v);
  // }
  // Tensor x = Tensors.fromString("{0,1,2,3}");
  // Tensor y = Tensors.fromString("{0,1,3,2}");
  // Tensor z = Tensors.fromString("{1,0,2,3}");
  // Tensor w = Tensors.fromString("{0,2,1,3}");
  // assertTrue(LSMT1.getCandidateSet().contains(w));
  // assertTrue(LSMT1.getCandidateSet().contains(x) && LSMT1.getMinElements().contains(x));
  // assertTrue(LSMT1.getCandidateSet().contains(y) && LSMT1.getMinElements().contains(z));
  // assertTrue(LSMT1.getCandidateSet().contains(y) && LSMT1.getMinElements().contains(z));
  // }
}