// code by jph
package ch.ethz.idsc.owl.bot.rice;

import java.util.Collection;

import ch.ethz.idsc.owl.bot.r2.R2NoiseRegion;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.glc.adapter.TrajectoryObstacleConstraint;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.EuclideanTrajectoryControl;
import ch.ethz.idsc.owl.math.state.TrajectoryControl;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class Rice2dNoiseDemo implements DemoInterface {
  @Override
  public OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    Scalar mu = RealScalar.ZERO;
    Collection<Flow> controls = Rice2Controls.create2d(mu, 1, 15);
    TrajectoryControl trajectoryControl = EuclideanTrajectoryControl.INSTANCE;
    owlyAnimationFrame.set(new Rice2dEntity(mu, Tensors.vector(0, 0, 0, 0), trajectoryControl, controls));
    Region<Tensor> region = new R2NoiseRegion(RealScalar.of(0.5));
    owlyAnimationFrame.setPlannerConstraint(new TrajectoryObstacleConstraint(SimpleTrajectoryRegionQuery.timeInvariant(region)));
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new Rice2dNoiseDemo().start().jFrame.setVisible(true);
  }
}
