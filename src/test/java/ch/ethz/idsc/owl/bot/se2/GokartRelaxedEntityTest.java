package ch.ethz.idsc.owl.bot.se2;

import ch.ethz.idsc.owl.bot.se2.glc.GokartRelaxedEntity;
import ch.ethz.idsc.owl.glc.adapter.EmptyObstacleConstraint;
import ch.ethz.idsc.owl.glc.rl2.RelaxedGlcExpand;
import ch.ethz.idsc.owl.glc.rl2.StandardRelaxedLexicographicPlanner;
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
    GokartRelaxedEntity entity = GokartRelaxedEntity.createRelaxedGokartEntity(initial, slacks);
    Tensor goal = Tensors.vector(0, 25, 0);
    StandardRelaxedLexicographicPlanner planner = (StandardRelaxedLexicographicPlanner) entity.createTrajectoryPlanner(EmptyObstacleConstraint.INSTANCE, goal);
    assertEquals(entity.getSlack(), slacks);
    planner.insertRoot(initial);
    RelaxedGlcExpand glcExpand = new RelaxedGlcExpand(planner);
    glcExpand.findAny(1000);
    // assertTrue(planner.getBest().isPresent());
  }
}
