// code by jph
package ch.ethz.idsc.owl.math.pursuit;

import java.io.IOException;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class TrajectoryEntryTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    TrajectoryEntry trajectoryEntry = Serialization.copy(new TrajectoryEntry(null, RealScalar.ONE));
    assertFalse(trajectoryEntry.point().isPresent());
    assertEquals(trajectoryEntry.variable(), RealScalar.ONE);
  }
}
