// code by astoll, jph
package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class DMLexSemiMinTrackerTest extends TestCase {
  private static void _checkSimple(AbstractLexSemiMinTracker<Integer> LSMT1) {
    Tensor x = Tensors.fromString("{1,2,2}");
    LSMT1.digest(1, x);
    assertFalse(LSMT1.getCandidateSet().isEmpty());
  }

  public void testDigestSimple() {
    Tensor slacks = Tensors.vector(1, 1, 1);
    _checkSimple((AbstractLexSemiMinTracker<Integer>) DMLexSemiMinTracker.<Integer>withList(slacks));
    _checkSimple((AbstractLexSemiMinTracker<Integer>) DMLexSemiMinTracker.<Integer>withSet(slacks));
  }

  /***************************************************/
  private static void _checkDigest(AbstractLexSemiMinTracker<Integer> LSMT1) {
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

  public void testDigest() {
    Tensor slacks = Tensors.fromString("{2}");
    _checkDigest((AbstractLexSemiMinTracker<Integer>) DMLexSemiMinTracker.<Integer>withList(slacks));
    _checkDigest((AbstractLexSemiMinTracker<Integer>) DMLexSemiMinTracker.<Integer>withSet(slacks));
  }

  /***************************************************/
  private static void _checkCS(AbstractLexSemiMinTracker<Integer> lexSemiMinTracker) {
    Tensor x = Tensors.fromString("{1,4,4}");
    Tensor y = Tensors.fromString("{3,3,1}");
    Tensor z = Tensors.fromString("{1.5,4,4}");
    Tensor w = Tensors.fromString("{0.9,2.9,1}");
    assertTrue(lexSemiMinTracker.getCandidateSet().isEmpty());
    lexSemiMinTracker.digest(1, x);
    assertTrue(lexSemiMinTracker.getCandidateSet().size() == 1);
    assertTrue(lexSemiMinTracker.getCandidateKeys().contains(1));
    assertTrue(lexSemiMinTracker.getCandidateValues().contains(x));
    lexSemiMinTracker.digest(2, y);
    assertTrue(lexSemiMinTracker.getCandidateSet().size() == 2);
    assertTrue(lexSemiMinTracker.getCandidateKeys().contains(2));
    assertTrue(lexSemiMinTracker.getCandidateValues().contains(y));
    lexSemiMinTracker.digest(3, z);
    assertTrue(lexSemiMinTracker.getCandidateSet().size() == 2);
    assertTrue(!lexSemiMinTracker.getCandidateKeys().contains(3));
    assertTrue(lexSemiMinTracker.getCandidateValues().contains(x) && lexSemiMinTracker.getCandidateValues().contains(y)
        && !lexSemiMinTracker.getCandidateValues().contains(z));
    lexSemiMinTracker.digest(4, w);
    assertTrue(lexSemiMinTracker.getCandidateSet().size() == 1);
    assertTrue(lexSemiMinTracker.getCandidateKeys().contains(4));
    assertFalse(lexSemiMinTracker.getCandidateKeys().contains(1) && lexSemiMinTracker.getCandidateKeys().contains(1)
        && lexSemiMinTracker.getCandidateKeys().contains(3));
  }

  public void testCandidateSet() {
    Tensor slacks = Tensors.vector(2, 2, 2);
    _checkCS((AbstractLexSemiMinTracker<Integer>) DMLexSemiMinTracker.<Integer>withList(slacks));
    _checkCS((AbstractLexSemiMinTracker<Integer>) DMLexSemiMinTracker.<Integer>withSet(slacks));
  }

  public void testFailNull() {
    try {
      DMLexSemiMinTracker.withList(null);
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      DMLexSemiMinTracker.withSet(null);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
