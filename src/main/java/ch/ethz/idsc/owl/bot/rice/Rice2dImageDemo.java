// code by jph
package ch.ethz.idsc.owl.bot.rice;

import java.util.Collection;

import ch.ethz.idsc.owl.ani.adapter.EuclideanTrajectoryControl;
import ch.ethz.idsc.owl.ani.api.TrajectoryControl;
import ch.ethz.idsc.owl.ani.api.TrajectoryEntity;
import ch.ethz.idsc.owl.bot.r2.R2ImageRegionWrap;
import ch.ethz.idsc.owl.bot.r2.R2ImageRegions;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.RegionConstraints;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.gui.win.MouseGoal;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class Rice2dImageDemo implements DemoInterface {
  @Override
  public OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    Scalar mu = RealScalar.of(-0.5);
    Collection<Flow> controls = Rice2Controls.create2d(mu, 1).getFlows(15);
    R2ImageRegionWrap r2ImageRegionWrap = R2ImageRegions._GTOB;
    Region<Tensor> region = r2ImageRegionWrap.imageRegion();
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(region);
    TrajectoryControl trajectoryControl = new EuclideanTrajectoryControl();
    TrajectoryEntity trajectoryEntity = new Rice2dEntity(mu, Tensors.vector(7, 6, 0, 0), trajectoryControl, controls);
    owlyAnimationFrame.add(trajectoryEntity);
    MouseGoal.simple(owlyAnimationFrame, trajectoryEntity, plannerConstraint);
    owlyAnimationFrame.addBackground(RegionRenders.create(region));
    owlyAnimationFrame.configCoordinateOffset(50, 700);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new Rice2dImageDemo().start().jFrame.setVisible(true);
  }
}
