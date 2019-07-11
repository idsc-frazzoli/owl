package ch.ethz.idsc.owl.bot.se2.glc;

import ch.ethz.idsc.owl.glc.adapter.ConstraintViolationCost;
import ch.ethz.idsc.owl.glc.adapter.EmptyObstacleConstraint;
import ch.ethz.idsc.owl.glc.rl2.RelaxedGlcExpand;
import ch.ethz.idsc.owl.glc.rl2.RelaxedTrajectoryPlanner;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class GokartRelaxedEntityTest extends TestCase {
  public void testSimple() {
    final StateTime initial = new StateTime(Tensors.vector(0, 10, 0), RealScalar.ZERO);
    // define region costs
    // define slack vector
    Tensor slacks = Tensors.vector(0, 0);
    GokartRelaxedEntity gokartRelaxedEntity = GokartRelaxedEntity.createRelaxedGokartEntity(initial, slacks);
    gokartRelaxedEntity.setAdditionalCostFunction(ConstraintViolationCost.of(EmptyObstacleConstraint.INSTANCE, RealScalar.ONE));
    Tensor goal = Tensors.vector(0, 25, 0);
    RelaxedTrajectoryPlanner relaxedTrajectoryPlanner = gokartRelaxedEntity.createTreePlanner(EmptyObstacleConstraint.INSTANCE, goal);
    assertEquals(gokartRelaxedEntity.getSlack(), slacks);
    relaxedTrajectoryPlanner.insertRoot(initial);
    RelaxedGlcExpand glcExpand = new RelaxedGlcExpand(relaxedTrajectoryPlanner);
    glcExpand.findAny(1000);
    assertTrue(relaxedTrajectoryPlanner.getBest().isPresent());
  }
}
