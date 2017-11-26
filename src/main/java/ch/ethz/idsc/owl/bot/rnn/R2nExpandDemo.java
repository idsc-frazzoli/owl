// code by jph
package ch.ethz.idsc.owl.bot.rnn;

import java.util.Collection;

import ch.ethz.idsc.owl.bot.r2.R2Flows;
import ch.ethz.idsc.owl.glc.adapter.Expand;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.std.StandardTrajectoryPlanner;
import ch.ethz.idsc.owl.gui.ani.OwlyFrame;
import ch.ethz.idsc.owl.gui.ani.OwlyGui;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.EmptyTrajectoryRegionQuery;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;

enum R2nExpandDemo {
  ;
  public static void main(String[] args) throws Exception {
    Tensor eta = Tensors.vector(4, 4);
    StateIntegrator stateIntegrator = FixedStateIntegrator.create(EulerIntegrator.INSTANCE, RationalScalar.of(1, 5), 5);
    R2Flows r2Config = new R2Flows(RealScalar.ONE);
    Collection<Flow> controls = r2Config.getFlows(16);
    RnnGoalManager rnGoal = new RnnGoalManager(Tensors.vector(4, 4), DoubleScalar.of(0.25));
    // ---
    TrajectoryPlanner trajectoryPlanner = new StandardTrajectoryPlanner( //
        eta, stateIntegrator, controls, EmptyTrajectoryRegionQuery.INSTANCE, rnGoal);
    trajectoryPlanner.insertRoot(new StateTime(Array.zeros(2), RealScalar.ZERO));
    OwlyFrame owlyFrame = OwlyGui.start();
    for (int c = 0; c < 100 && owlyFrame.jFrame.isVisible(); ++c) {
      Expand.maxSteps(trajectoryPlanner, 10);
      owlyFrame.setGlc(trajectoryPlanner);
      Thread.sleep(100);
    }
  }
}
