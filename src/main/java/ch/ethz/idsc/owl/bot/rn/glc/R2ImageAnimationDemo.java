// code by jph
package ch.ethz.idsc.owl.bot.rn.glc;

import ch.ethz.idsc.owl.bot.r2.R2ImageRegionWrap;
import ch.ethz.idsc.owl.bot.r2.R2ImageRegions;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.glc.adapter.SimpleTrajectoryRegionQuery;
import ch.ethz.idsc.owl.gui.ani.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.tensor.Tensors;

/** demo shows the use of a cost image that is added to the distance cost
 * which gives an incentive to stay clear of obstacles */
public class R2ImageAnimationDemo implements DemoInterface {
  @Override
  public void start() {
    try {
      R2ImageRegionWrap r2ImageRegionWrap = R2ImageRegions._GTOB;
      // ---
      OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
      R2Entity r2Entity = new R2Entity(Tensors.vector(7, 6));
      r2Entity.extraCosts.add(r2ImageRegionWrap.costFunction());
      owlyAnimationFrame.set(r2Entity);
      ImageRegion imageRegion = r2ImageRegionWrap.imageRegion();
      owlyAnimationFrame.setObstacleQuery(SimpleTrajectoryRegionQuery.timeInvariant(imageRegion));
      owlyAnimationFrame.addBackground(RegionRenders.create(imageRegion));
      owlyAnimationFrame.configCoordinateOffset(50, 700);
      owlyAnimationFrame.jFrame.setVisible(true);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public static void main(String[] args) {
    new R2ImageAnimationDemo().start();
  }
}
