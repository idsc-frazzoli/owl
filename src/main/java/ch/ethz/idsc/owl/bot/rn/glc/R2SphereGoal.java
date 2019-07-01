// code by jph
package ch.ethz.idsc.owl.bot.rn.glc;

import java.util.List;
import java.util.Optional;

import ch.ethz.idsc.owl.glc.adapter.GlcExpand;
import ch.ethz.idsc.owl.glc.adapter.StateTimeTrajectories;
import ch.ethz.idsc.owl.glc.core.GlcNode;
import ch.ethz.idsc.owl.glc.core.GlcNodes;
import ch.ethz.idsc.owl.glc.core.GlcTrajectoryPlanner;
import ch.ethz.idsc.owl.math.state.StateTime;

/* package */ enum R2SphereGoal {
  ;
  public static void main(String[] args) {
    R2SphereBase r2SphereBase = new R2SphereBase();
    GlcTrajectoryPlanner trajectoryPlanner = r2SphereBase.create();
    GlcExpand glcExpand = new GlcExpand(trajectoryPlanner);
    glcExpand.findAny(1000);
    System.out.println("ExpandCount=" + glcExpand.getExpandCount());
    Optional<GlcNode> optional = trajectoryPlanner.getBest();
    if (optional.isPresent()) {
      List<StateTime> trajectory = GlcNodes.getPathFromRootTo(optional.get());
      StateTimeTrajectories.print(trajectory);
    }
    r2SphereBase.show(trajectoryPlanner);
  }
}
