// code by jph
package ch.ethz.idsc.owl.bot.sat;

import java.util.Collection;

import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.RegionConstraints;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.gui.ani.TrajectoryEntity;
import ch.ethz.idsc.owl.gui.win.MouseGoal;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.EllipsoidRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.EuclideanTrajectoryControl;
import ch.ethz.idsc.owl.math.state.TrajectoryControl;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class SatelliteDemo implements DemoInterface {
  @Override // from DemoInterface
  public OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    Collection<Flow> controls = SatelliteControls.create2d(15);
    TrajectoryControl trajectoryControl = EuclideanTrajectoryControl.INSTANCE;
    TrajectoryEntity trajectoryEntity = //
        new SatelliteEntity(Tensors.vector(2, 0, 0, 1), trajectoryControl, controls);
    owlyAnimationFrame.add(trajectoryEntity);
    Region<Tensor> region = new EllipsoidRegion( //
        Tensors.vector(0, 0, 0, 0), //
        Tensors.vector(0.5, 0.5, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(region);
    // PlannerConstraint plannerConstraint = EmptyObstacleConstraint.INSTANCE;
    MouseGoal.simple(owlyAnimationFrame, trajectoryEntity, plannerConstraint);
    owlyAnimationFrame.addBackground(RegionRenders.create(region));
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new SatelliteDemo().start().jFrame.setVisible(true);
  }
}
