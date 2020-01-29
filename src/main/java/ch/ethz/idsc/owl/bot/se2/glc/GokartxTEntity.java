// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.List;
import java.util.Objects;

import ch.ethz.idsc.owl.ani.adapter.TemporalTrajectoryControl;
import ch.ethz.idsc.owl.bot.se2.Se2Wrap;
import ch.ethz.idsc.owl.glc.adapter.EtaRaster;
import ch.ethz.idsc.owl.glc.adapter.StateTimeCoordinateWrap;
import ch.ethz.idsc.owl.glc.core.StateTimeRaster;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.gui.ren.EdgeRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;

/** several magic constants are hard-coded in the implementation.
 * that means, the functionality does not apply to all examples universally. */
class GokartxTEntity extends CarEntity {
  private final EdgeRender edgeRender = new EdgeRender();

  GokartxTEntity(StateTime stateTime) {
    super(stateTime, TemporalTrajectoryControl.createInstance(), GokartEntity.PARTITION_SCALE, GokartEntity.CARFLOWS, GokartEntity.SHAPE);
  }

  @Override
  public Scalar delayHint() {
    return RealScalar.of(2.0);
  }

  @Override
  protected StateTimeRaster stateTimeRaster() {
    return EtaRaster.timeDependent( //
        partitionScale, FIXED_STATE_INTEGRATOR.getTimeStepTrajectory(), //
        new StateTimeCoordinateWrap(Se2Wrap.INSTANCE));
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    edgeRender.getRender().render(geometricLayer, graphics);
    // ---
    super.render(geometricLayer, graphics);
    // ---
    if (Objects.nonNull(trajectoryWrap)) {
      StateTime stateTime = getStateTimeNow();
      Scalar now = stateTime.time();
      if (trajectoryWrap.isDefined(now)) {
        TrajectorySample trajectorySample = trajectoryWrap.getSample(now);
        Path2D path2d = geometricLayer.toPath2D(Tensors.of(stateTime.state(), trajectorySample.stateTime().state()));
        graphics.setColor(Color.PINK);
        graphics.draw(path2d);
      }
    }
  }

  @Override
  public void expandResult(List<TrajectorySample> head, TrajectoryPlanner trajectoryPlanner) {
    edgeRender.setCollection(trajectoryPlanner.getDomainMap().values());
  }
}
