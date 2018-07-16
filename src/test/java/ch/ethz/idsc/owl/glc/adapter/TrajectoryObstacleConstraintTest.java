// code by jph
package ch.ethz.idsc.owl.glc.adapter;

import java.io.IOException;

import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class TrajectoryObstacleConstraintTest extends TestCase {
  public void testSerializable() throws ClassNotFoundException, IOException {
    PlannerConstraint plannerConstraint = new TrajectoryObstacleConstraint(null);
    Serialization.copy(plannerConstraint);
  }
}
