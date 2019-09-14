// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Random;

import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.math.sample.BoxRandomSample;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.opt.Pi;

public class ClothoidNdDemo extends ControlPointsDemo {
  private static final Tensor lbounds = Tensors.vector(-5, -5);
  private static final Tensor ubounds = Tensors.vector(+5, +5);
  // ---
  private final RrtsNodeCollection rrtsNodeCollection = ClothoidRrtsNodeCollections.of(lbounds, ubounds);
  private final SpinnerLabel<Integer> spinnerValue = new SpinnerLabel<>();

  public ClothoidNdDemo() {
    super(false, GeodesicDisplays.SE2_ONLY);
    // ---
    spinnerValue.setList(Arrays.asList(1, 2, 3, 4, 5));
    spinnerValue.setValue(3);
    spinnerValue.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    // setPositioningEnabled(false);
    setMidpointIndicated(false);
    // ---
    Random random = new Random();
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of( //
        lbounds.copy().append(Pi.VALUE.negate()), //
        ubounds.copy().append(Pi.VALUE));
    Tensor tensor = Array.of(l -> randomSampleInterface.randomSample(random), 20);
    for (Tensor state : tensor)
      rrtsNodeCollection.insert(RrtsNode.createRoot(state, RealScalar.ZERO));
    setControlPointsSe2(tensor);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    // ---
    renderControlPoints(geometricLayer, graphics);
    Tensor mouse = geometricLayer.getMouseSe2State();
    // ---
    int value = spinnerValue.getValue();
    graphics.setColor(new Color(255, 0, 0, 128));
    for (RrtsNode rrtsNode : rrtsNodeCollection.nearTo(mouse, value)) {
      Tensor other = rrtsNode.state();
      ClothoidTransition clothoidTransition = ClothoidTransition.of(other, mouse);
      graphics.draw(geometricLayer.toPath2D(clothoidTransition.linearized(RealScalar.of(.2))));
    }
    // ---
    graphics.setColor(new Color(0, 255, 0, 128));
    for (RrtsNode rrtsNode : rrtsNodeCollection.nearFrom(mouse, value)) {
      Tensor other = rrtsNode.state();
      ClothoidTransition clothoidTransition = ClothoidTransition.of(mouse, other);
      graphics.draw(geometricLayer.toPath2D(clothoidTransition.linearized(RealScalar.of(.2))));
    }
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new ClothoidNdDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1200, 800);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
