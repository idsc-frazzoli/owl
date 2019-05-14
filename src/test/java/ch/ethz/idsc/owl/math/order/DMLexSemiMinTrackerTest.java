// code by astoll
package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class DMLexSemiMinTrackerTest extends TestCase {
  public void testDigestSimple() {
    Tensor slackVector = Tensors.fromString("{1,1,1}");
    DMLexSemiMinTracker<Integer> LSMT1 = DMLexSemiMinTracker.withList(slackVector);
    DMLexSemiMinTracker<Integer> LSMT2 = DMLexSemiMinTracker.withSet(slackVector);
    Tensor x = Tensors.fromString("{1,2,2}");
    LSMT1.digest(1, x);
    LSMT2.digest(1, x);
    assertFalse(LSMT1.getCandidateSet().isEmpty());
    assertFalse(LSMT2.getCandidateSet().isEmpty());
  }

  public void testDigest() {
    Tensor slackVector = Tensors.fromString("{2}");
    DMLexSemiMinTracker<Integer> LSMT1 = DMLexSemiMinTracker.withList(slackVector);
    Tensor x = Tensors.fromString("{1}");
    Tensor y = Tensors.fromString("{3.5}");
    Tensor z = Tensors.fromString("{1.5}");
    Tensor w = Tensors.fromString("{-1.5}");
    assertTrue(LSMT1.getCandidateSet().isEmpty());
    assertTrue(LSMT1.digest(1, x).isEmpty());
    assertTrue(LSMT1.getCandidateSet().size() == 1);
    assertTrue(LSMT1.digest(2, y).contains(2));
    assertTrue(LSMT1.digest(3, z).contains(3));
    assertTrue(LSMT1.digest(4, w).contains(1));
    assertTrue(LSMT1.digest(5, w).contains(5));
  }

  public void testCandidateSet() {
    Tensor slackVector = Tensors.fromString("{2,2,2}");
    DMLexSemiMinTracker<Integer> LSMT1 = DMLexSemiMinTracker.withList(slackVector);
    Tensor x = Tensors.fromString("{1,4,4}");
    Tensor y = Tensors.fromString("{3,3,1}");
    Tensor z = Tensors.fromString("{1.5,4,4}");
    Tensor w = Tensors.fromString("{0.9,2.9,1}");
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
    assertTrue(LSMT1.getCandidateSet().size() == 2);
    assertTrue(!LSMT1.getCandidateKeys().contains(3));
    assertTrue(LSMT1.getCandidateValues().contains(x) && LSMT1.getCandidateValues().contains(y) && !LSMT1.getCandidateValues().contains(z));
    LSMT1.digest(4, w);
    assertTrue(LSMT1.getCandidateSet().size() == 1);
    assertTrue(LSMT1.getCandidateKeys().contains(4));
    assertFalse(LSMT1.getCandidateKeys().contains(1) && LSMT1.getCandidateKeys().contains(1) && LSMT1.getCandidateKeys().contains(3));
  }
}
