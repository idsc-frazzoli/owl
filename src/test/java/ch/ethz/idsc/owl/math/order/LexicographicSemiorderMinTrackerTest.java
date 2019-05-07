// code by astoll
package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.lie.Permutations;
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
    LSMT2.digest(1, x);
    assertTrue(!LSMT1.getCandidateSet().isEmpty() && LSMT1.getCandidateSet().size() > 1);
    assertTrue(!LSMT2.getCandidateSet().isEmpty() && LSMT2.getCandidateSet().size() > 1);
  }
  
  public void testCandidateSet() {
    Tensor slackVector = Tensors.fromString("{2,2,2}");
    LexicographicSemiorderMinTracker<Integer> LSMT1 = LexicographicSemiorderMinTracker.withList(slackVector);
    Tensor x = Tensors.fromString("{1,4,4}");
    Tensor y = Tensors.fromString("{3,3,1}");
    Tensor z = Tensors.fromString("{1.5,4,4}");
    Tensor w = Tensors.fromString("{-1.5,10,10}");
    assertTrue(LSMT1.getCandidateSet().isEmpty());
    LSMT1.digest(1, x);
    assertTrue(LSMT1.getCandidateSet().size() == 1);
    assertTrue(LSMT1.getCandidateKeys().contains(1));
    assertTrue(LSMT1.getCandidateValues().contains(x));
    LSMT1.digest(2, y);
    assertTrue(LSMT1.getCandidateSet().size() == 2);
    assertTrue(LSMT1.getCandidateKeys().contains(2));
    assertTrue(LSMT1.getCandidateValues().contains(y));
    LSMT1.digest(3, z);
    assertTrue(LSMT1.getCandidateSet().size() == 3);
    assertTrue(LSMT1.getCandidateKeys().contains(1) && LSMT1.getCandidateKeys().contains(3));
    assertTrue(LSMT1.getCandidateValues().contains(x) && LSMT1.getCandidateValues().contains(y) && LSMT1.getCandidateValues().contains(z));
    LSMT1.digest(4, w);
    assertTrue(LSMT1.getCandidateSet().size() == 1);
    assertTrue(LSMT1.getCandidateKeys().contains(4));
    assertFalse(LSMT1.getCandidateKeys().contains(1) &&  LSMT1.getCandidateKeys().contains(1) && LSMT1.getCandidateKeys().contains(3));
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
    Tensor y = Tensors.fromString("{3,3,1}");
    Tensor z = Tensors.fromString("{1.5,4,4}");
    assertTrue(LSMT1.getMinElements().isEmpty());
    LSMT1.digest(1, x);
    assertTrue(LSMT1.getMinElements().size() == 1);
    LSMT1.digest(2, y);
    assertTrue(LSMT1.getMinElements().size() == 1);
    LSMT1.digest(3, z);
    assertTrue(LSMT1.getMinElements().size() == 2);
  }
  
  public void testGetMinKeys() {
    Tensor slackVector = Tensors.fromString("{1,1,1}");
    LexicographicSemiorderMinTracker<Integer> LSMT1 = LexicographicSemiorderMinTracker.withList(slackVector);
    Tensor x = Tensors.fromString("{1,4,4}");
    Tensor y = Tensors.fromString("{3,3,1}");
    Tensor z = Tensors.fromString("{1.5,4,4}");
    assertTrue(LSMT1.getMinKeys().isEmpty());
    LSMT1.digest(1, x);
    assertTrue(LSMT1.getMinKeys().contains(1));
    LSMT1.digest(2, y);
    assertFalse(LSMT1.getMinKeys().contains(2));
    LSMT1.digest(3, z);
    assertTrue(LSMT1.getMinKeys().contains(3) && LSMT1.getMinKeys().contains(1));
  }
  
  public void testGetMinValues() {
    Tensor slackVector = Tensors.fromString("{1,1,1}");
    LexicographicSemiorderMinTracker<Integer> LSMT1 = LexicographicSemiorderMinTracker.withList(slackVector);
    Tensor x = Tensors.fromString("{1,4,4}");
    Tensor y = Tensors.fromString("{3,3,1}");
    Tensor z = Tensors.fromString("{1.5,4,4}");
    assertTrue(LSMT1.getMinValues().isEmpty());
    LSMT1.digest(1, x);
    assertTrue(LSMT1.getMinValues().contains(x));
    LSMT1.digest(2, y);
    assertFalse(LSMT1.getMinValues().contains(y));
    LSMT1.digest(3, z);
    assertTrue(LSMT1.getMinValues().contains(x) && LSMT1.getMinValues().contains(z));
  }
  
   public void testReverseSequencewithList() {
   Tensor slackVector = Tensors.fromString("{1,1,1}");
   LexicographicSemiorderMinTracker<Integer> LSMT1 = LexicographicSemiorderMinTracker.withList(slackVector);
   LexicographicSemiorderMinTracker<Integer> LSMT2 = LexicographicSemiorderMinTracker.withList(slackVector);
   LexicographicSemiorderMinTracker<Integer> LSMT3 = LexicographicSemiorderMinTracker.withList(slackVector);
   Tensor x = Tensors.fromString("{1,0,4}");
   Tensor y = Tensors.fromString("{1,2,0}");
   Tensor z = Tensors.fromString("{2,1,4}");
   LSMT1.digest(1,x);
   assertTrue(LSMT1.getCandidateValues().contains(x) && LSMT1.getCandidateSet().size() == 1);
   LSMT1.digest(2, y);
   assertTrue(!LSMT1.getCandidateValues().contains(y) && LSMT1.getCandidateSet().size() == 1);
   LSMT1.digest(3, z);
   assertTrue(LSMT1.getCandidateValues().contains(z) && LSMT1.getCandidateSet().size() > 1);
   LSMT2.digest(2, y);
   assertTrue(LSMT2.getCandidateValues().contains(y) && LSMT2.getCandidateSet().size() == 1);
   LSMT2.digest(1, x);
   assertTrue(LSMT2.getCandidateValues().contains(x) && LSMT2.getCandidateSet().size() == 1);
   LSMT2.digest(3, z);
   assertTrue(LSMT2.getCandidateValues().contains(z) && LSMT2.getCandidateSet().size() > 1);
   LSMT3.digest(3, z);
   assertTrue(LSMT3.getCandidateValues().contains(z) && LSMT3.getCandidateSet().size() == 1);
   LSMT3.digest(2, y);
   assertTrue(LSMT3.getCandidateValues().contains(z) && LSMT3.getCandidateValues().contains(y) && LSMT3.getCandidateSet().size() > 1);
   LSMT3.digest(1, x);
   assertTrue(!LSMT3.getCandidateValues().contains(y) && LSMT3.getCandidateValues().contains(x) && LSMT3.getCandidateValues().contains(z)
   && LSMT2.getCandidateSet().size() > 1);
   assertTrue(LSMT1.getMinValues().contains(x) && LSMT1.getCandidateValues().contains(z) && LSMT1.getMinElements().size() > 1);
   assertTrue(LSMT2.getMinValues().contains(x) && LSMT2.getCandidateValues().contains(z) && LSMT2.getMinElements().size() > 1);
   assertTrue(LSMT3.getMinValues().contains(x) && LSMT3.getCandidateValues().contains(z) && LSMT3.getMinElements().size() > 1);
   assertTrue(LSMT1.getCandidateSet().containsAll(LSMT1.getMinElements()));
   assertTrue(LSMT2.getCandidateSet().containsAll(LSMT2.getMinElements()));
   assertTrue(LSMT3.getCandidateSet().containsAll(LSMT3.getMinElements()));
   }
  
   public void testPermutations() {
   Tensor slackVector = Tensors.fromString("{1,1,1,1}");
   LexicographicSemiorderMinTracker<Integer> LSMT1 = LexicographicSemiorderMinTracker.withList(slackVector);
   Tensor tensor = Permutations.of(Range.of(0, 4));
   int key = 1;
   for (Tensor v : tensor) {
   LSMT1.digest(key, v);
   ++key;
   }
   Tensor x = Tensors.fromString("{0,1,2,3}");
   Tensor y = Tensors.fromString("{0,1,3,2}");
   Tensor z = Tensors.fromString("{1,0,2,3}");
   Tensor w = Tensors.fromString("{0,2,1,3}");
   assertTrue(LSMT1.getCandidateValues().contains(w));
   assertTrue(LSMT1.getCandidateValues().contains(x) && LSMT1.getMinValues().contains(x));
   assertTrue(LSMT1.getCandidateValues().contains(y) && LSMT1.getMinValues().contains(z));
   assertTrue(LSMT1.getCandidateValues().contains(y) && LSMT1.getMinValues().contains(z));
   }
}