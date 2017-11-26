// code by jl
package ch.ethz.idsc.owl.bot.delta.glc;

import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.gui.ani.OwlyFrame;
import ch.ethz.idsc.owl.gui.ani.OwlyGui;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

enum DeltaGlcConstTimeDemo {
  ;
  public static void main(String[] args) throws Exception {
    RationalScalar resolution = (RationalScalar) RationalScalar.of(10, 1);
    Tensor partitionScale = Tensors.vector(2e26, 2e26);
    TrajectoryPlannerContainer trajectoryPlannerContainer = DeltaHelper.createGlc(RealScalar.of(-0.5), resolution, partitionScale);
    OwlyFrame owlyFrame = OwlyGui.start();
    owlyFrame.configCoordinateOffset(33, 416);
    owlyFrame.jFrame.setBounds(100, 100, 620, 475);
    Scalar planningTime = RealScalar.of(1);
    while (!trajectoryPlannerContainer.getTrajectoryPlanner().getBest().isPresent() && owlyFrame.jFrame.isVisible()) {
      int expandIter = GlcExpand.constTime(trajectoryPlannerContainer.getTrajectoryPlanner(), planningTime, //
          trajectoryPlannerContainer.getParameters().getDepthLimit());
      owlyFrame.setGlc((TrajectoryPlanner) trajectoryPlannerContainer.getTrajectoryPlanner());
      if (expandIter < 1)
        break;
      Thread.sleep(1);
    }
  }
}
