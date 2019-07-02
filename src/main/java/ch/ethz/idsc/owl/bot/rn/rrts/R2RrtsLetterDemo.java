// code by jph, gjoel
package ch.ethz.idsc.owl.bot.rn.rrts;

import ch.ethz.idsc.owl.bot.r2.R2ImageRegions;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.gui.win.MouseGoal;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;

class R2RrtsLetterDemo implements DemoInterface {
  @Override
  public OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    ImageRegion imageRegion = R2ImageRegions._GTOB.imageRegion();
    StateTime stateTime = new StateTime(Tensors.vector(6, 5), RealScalar.ZERO);
    R2RrtsEntity entity = new R2RrtsEntity(stateTime, imageRegion);
    owlyAnimationFrame.addBackground(RegionRenders.create(imageRegion));
    MouseGoal.simpleRrts(owlyAnimationFrame, entity, null);
    owlyAnimationFrame.add(entity);
    owlyAnimationFrame.configCoordinateOffset(50, 700);
    owlyAnimationFrame.jFrame.setTitle(getClass().getSimpleName());
    owlyAnimationFrame.jFrame.setBounds(100, 50, 1200, 800);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new R2RrtsLetterDemo().start().jFrame.setVisible(true);
  }
}
