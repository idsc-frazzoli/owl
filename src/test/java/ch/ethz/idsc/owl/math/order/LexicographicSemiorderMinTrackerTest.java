// code by astoll
package ch.ethz.idsc.owl.math.order;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class LexicographicSemiorderMinTrackerTest extends TestCase {
  public void testDigestSimple() {
    Tensor slackVector = Tensors.fromString("{1,1,1}");
    LexicographicSemiorderMinTracker LSMT1 = LexicographicSemiorderMinTracker.withList(slackVector);
    LexicographicSemiorderMinTracker LSMT2 = LexicographicSemiorderMinTracker.withSet(slackVector);
    Tensor x = Tensors.fromString("{1,2,2}");
    LSMT1.digest(x);
    LSMT1.digest(x);
    LSMT2.digest(x);
    LSMT2.digest(x);
    assertTrue(!LSMT1.getFeasibleInputs().isEmpty() && LSMT1.getFeasibleInputs().size() == 1);
    assertTrue(!LSMT2.getFeasibleInputs().isEmpty() && LSMT2.getFeasibleInputs().size() == 1);
  }

  public void testDigestFalseDim() {
    Tensor slackVector = Tensors.fromString("{1,1,1}");
    LexicographicSemiorderMinTracker LSMT1 = LexicographicSemiorderMinTracker.withList(slackVector);
    Tensor x = Tensors.fromString("{1,2,2,3}");
    try {
      LSMT1.digest(x);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testGetMinElements() {
    Tensor slackVector = Tensors.fromString("{1,1,1}");
    LexicographicSemiorderMinTracker LSMT1 = LexicographicSemiorderMinTracker.withList(slackVector);
    Tensor x = Tensors.fromString("{1,4,4}");
    Tensor y = Tensors.fromString("{2,3,1}");
    Tensor z = Tensors.fromString("{3,2,2}");
    Tensor w = Tensors.fromString("{4,1,3}");
    LSMT1.digest(x);
    assertTrue(LSMT1.getMinElements().contains(x));
    LSMT1.digest(z);
    assertFalse(LSMT1.getMinElements().contains(z));
    LSMT1.digest(y);
    LSMT1.digest(w);
    assertTrue(LSMT1.getMinElements().contains(y));
    Tensor u = Tensors.fromString("{1,1,1}");
    LSMT1.digest(u);
    assertTrue(LSMT1.getMinElements().contains(u));
    assertFalse(LSMT1.getMinElements().contains(y));
  }
}