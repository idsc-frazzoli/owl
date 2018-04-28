// code by jph
package ch.ethz.idsc.owl.glc.std;

import java.io.IOException;

import ch.ethz.idsc.owl.glc.adapter.TrajectoryObstacleConstraint;
import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class TrajectoryObstacleConstraintTest extends TestCase {
  public void testSerializable() throws ClassNotFoundException, IOException {
    PlannerConstraint plannerConstraint = new TrajectoryObstacleConstraint(null);
    Serialization.copy(plannerConstraint);
  }
}
