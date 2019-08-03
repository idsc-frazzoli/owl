// code by jph, gjoel
package ch.ethz.idsc.owl.ani.api;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.owl.data.tree.Nodes;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.gui.ren.TrajectoryRender;
import ch.ethz.idsc.owl.gui.ren.TransitionRender;
import ch.ethz.idsc.owl.gui.ren.TreeRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.owl.rrts.RrtsPlannerServer;
import ch.ethz.idsc.owl.rrts.core.TransitionPlanner;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.PadRight;

public abstract class AbstractRrtsEntity extends TrajectoryEntity implements RrtsPlannerCallback {
  protected final RrtsPlannerServer plannerServer;
  // ---
  private final TreeRender treeRender = new TreeRender();
  private final TransitionRender transitionRender;

  public AbstractRrtsEntity(RrtsPlannerServer plannerServer, EpisodeIntegrator episodeIntegrator, TrajectoryControl trajectoryControl) {
    super(episodeIntegrator, trajectoryControl);
    this.plannerServer = plannerServer;
    transitionRender = new TransitionRender(plannerServer.getTransitionSpace());
  }

  protected abstract Tensor shape();

  @Override // from PlannerCallback
  public void expandResult(List<TrajectorySample> head, TransitionPlanner trajectoryPlanner) {
    plannerServer.getRoot().map(Nodes::ofSubtree).ifPresent(collection -> {
      treeRender.setCollection(collection);
      transitionRender.setCollection(collection);
    });
    plannerServer.setState(head.get(0).stateTime());
    plannerServer.getTrajectory().ifPresent(this::trajectory);
  }

  @Override // from TensorMetrix
  public Scalar distance(Tensor x, Tensor y) {
    return plannerServer.getTransitionSpace().connect(x, y).length();
  }

  @Override // from TrajectoryEntity
  public TransitionPlanner createTreePlanner(PlannerConstraint plannerConstraint, Tensor goal) {
    plannerServer.setGoal(goal);
    return plannerServer;
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (Objects.nonNull(trajectoryWrap)) {
      TrajectoryRender trajectoryRender = new TrajectoryRender();
      trajectoryRender.trajectory(trajectoryWrap.trajectory());
      trajectoryRender.setColor(Color.GREEN);
      trajectoryRender.render(geometricLayer, graphics);
    }
    { // indicate current position
      final StateTime stateTime = getStateTimeNow();
      Color color = new Color(64, 64, 64, 128);
      geometricLayer.pushMatrix(Se2Matrix.of(PadRight.zeros(3).apply(stateTime.state())));
      graphics.setColor(color);
      graphics.fill(geometricLayer.toPath2D(shape()));
      geometricLayer.popMatrix();
    }
    { // indicate position delay[s] into the future
      Tensor state = getEstimatedLocationAt(delayHint());
      Point2D point = geometricLayer.toPoint2D(state);
      graphics.setColor(new Color(255, 128, 64, 192));
      graphics.fill(new Rectangle2D.Double(point.getX() - 2, point.getY() - 2, 5, 5));
    }
    // ---
    treeRender.render(geometricLayer, graphics);
    transitionRender.render(geometricLayer, graphics);
  }
}
