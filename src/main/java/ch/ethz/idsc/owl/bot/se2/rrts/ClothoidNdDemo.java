// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.awt.Graphics2D;
import java.util.Random;

import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.math.sample.BoxRandomSample;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.opt.Pi;

public class ClothoidNdDemo extends ControlPointsDemo {
  private static final Tensor lbounds = Tensors.vector(-5, -5);
  private static final Tensor ubounds = Tensors.vector(+5, +5);

  public ClothoidNdDemo() {
    super(false, GeodesicDisplays.SE2_ONLY);
    // setPositioningEnabled(false);
    // ---
    Random random = new Random();
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of( //
        lbounds.copy().append(Pi.VALUE.negate()), //
        ubounds.copy().append(Pi.VALUE));
    setControlPointsSe2(Array.of(l -> randomSampleInterface.randomSample(random), 20));
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    // TODO Auto-generated method stub
    renderControlPoints(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new ClothoidNdDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1200, 800);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
