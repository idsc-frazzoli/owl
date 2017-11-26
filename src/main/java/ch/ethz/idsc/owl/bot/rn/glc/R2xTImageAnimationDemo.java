// code by jph
package ch.ethz.idsc.owl.bot.rn.glc;

import ch.ethz.idsc.owl.bot.rn.R2ImageRegions;
import ch.ethz.idsc.owl.bot.rn.R2xTImageStateTimeRegion;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ani.AbstractEntity;
import ch.ethz.idsc.owl.gui.ani.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.map.RigidFamily;
import ch.ethz.idsc.owl.math.map.Se2Family;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;

/** the obstacle region in the demo is the outside of a rotating letter 'a' */
public class R2xTImageAnimationDemo implements DemoInterface {
  @Override
  public void start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    AbstractEntity abstractEntity = new R2xTEntity(Tensors.vector(1.5, 2), RealScalar.of(1.5));
    owlyAnimationFrame.set(abstractEntity);
    // ---
    RigidFamily rigidFamily = Se2Family.rotationAround( //
        Tensors.vectorDouble(1.5, 2), time -> time.multiply(RealScalar.of(0.1)));
    ImageRegion imageRegion = R2ImageRegions.inside_circ();
    Region<StateTime> region = new R2xTImageStateTimeRegion( //
        imageRegion, rigidFamily, () -> abstractEntity.getStateTimeNow().time());
    // ---
    owlyAnimationFrame.setObstacleQuery(new SimpleTrajectoryRegionQuery(region));
    owlyAnimationFrame.addBackground((RenderInterface) region);
    owlyAnimationFrame.configCoordinateOffset(200, 400);
    owlyAnimationFrame.jFrame.setVisible(true);
  }

  public static void main(String[] args) {
    new R2xTImageAnimationDemo().start();
  }
}
