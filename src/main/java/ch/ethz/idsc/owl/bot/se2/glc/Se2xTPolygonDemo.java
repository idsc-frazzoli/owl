// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import ch.ethz.idsc.owl.bot.r2.R2ExamplePolygons;
import ch.ethz.idsc.owl.bot.r2.R2xTPolygonStateTimeRegion;
import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.bot.util.SimpleTranslationFamily;
import ch.ethz.idsc.owl.glc.adapter.RegionConstraints;
import ch.ethz.idsc.owl.glc.std.PlannerConstraint;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.MouseGoal;
import ch.ethz.idsc.owl.gui.win.OwlyAnimationFrame;
import ch.ethz.idsc.owl.math.map.BijectionFamily;
import ch.ethz.idsc.owl.math.region.Region;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Sin;

public class Se2xTPolygonDemo implements DemoInterface {
  @Override
  public OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    CarxTEntity carxTEntity = new CarxTEntity(new StateTime(Tensors.vector(6.75, 5.4, 1 + Math.PI), RealScalar.ZERO));
    owlyAnimationFrame.add(carxTEntity);
    // ---
    BijectionFamily shift = new SimpleTranslationFamily( //
        scalar -> Tensors.of(Sin.FUNCTION.apply(scalar.multiply(RealScalar.of(0.2))), RealScalar.ZERO));
    Region<StateTime> region = new R2xTPolygonStateTimeRegion( //
        R2ExamplePolygons.CORNER_TOP_LEFT, shift, () -> carxTEntity.getStateTimeNow().time());
    PlannerConstraint plannerConstraint = RegionConstraints.stateTime(region);
    carxTEntity.plannerConstraint = plannerConstraint;
    MouseGoal.simple(owlyAnimationFrame, carxTEntity, plannerConstraint);
    // owlyAnimationFrame.addRegionRender(imageRegion);
    owlyAnimationFrame.addBackground((RenderInterface) region);
    // ---
    owlyAnimationFrame.configCoordinateOffset(50, 700);
    owlyAnimationFrame.jFrame.setBounds(100, 50, 1200, 800);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new Se2xTPolygonDemo().start().jFrame.setVisible(true);
  }
}
