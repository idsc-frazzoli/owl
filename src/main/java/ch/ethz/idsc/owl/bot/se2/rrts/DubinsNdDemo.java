// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Random;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.PointsRender;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.crv.dubins.DubinsPathComparator;
import ch.ethz.idsc.sophus.math.sample.BoxRandomSample;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.opt.Pi;

// TODO JPH reduce redundancy with clothoid nd demo
public class DubinsNdDemo extends ControlPointsDemo {
  private static final Tensor LBOUNDS = Tensors.vector(-5, -5).unmodifiable();
  private static final Tensor UBOUNDS = Tensors.vector(+5, +5).unmodifiable();
  private static final Scalar RADIUS = RealScalar.of(.3);
  // ---
  private final TransitionSpace transitionSpace = DubinsTransitionSpace.of(RADIUS, DubinsPathComparator.LENGTH);
  private final RrtsNodeCollection rrtsNodeCollection = DubinsRrtsNodeCollections.of( //
      transitionSpace, LBOUNDS, UBOUNDS);
  private final SpinnerLabel<Integer> spinnerValue = new SpinnerLabel<>();
  private final PointsRender pointsRender = //
      new PointsRender(new Color(128, 128, 128, 64), new Color(128, 128, 128, 255));

  public DubinsNdDemo() {
    super(false, GeodesicDisplays.SE2_ONLY);
    // ---
    spinnerValue.setList(Arrays.asList(1, 2, 3, 4, 5, 6, 7));
    spinnerValue.setValue(3);
    spinnerValue.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    // setPositioningEnabled(false);
    setMidpointIndicated(false);
    // ---
    Random random = new Random();
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of( //
        LBOUNDS.copy().append(Pi.VALUE.negate()), //
        UBOUNDS.copy().append(Pi.VALUE));
    Tensor tensor = Array.of(l -> randomSampleInterface.randomSample(random), 50);
    for (Tensor state : tensor)
      rrtsNodeCollection.insert(RrtsNode.createRoot(state, RealScalar.ZERO));
    setControlPointsSe2(tensor);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    // ---
    GraphicsUtil.setQualityHigh(graphics);
    Tensor shape = getControlPointShape().multiply(RealScalar.of(0.5));
    pointsRender.new Show(geodesicDisplay(), shape, getGeodesicControlPoints()) //
        .render(geometricLayer, graphics);
    Tensor mouse = geometricLayer.getMouseSe2State();
    // ---
    int value = spinnerValue.getValue();
    graphics.setColor(new Color(255, 0, 0, 128));
    for (RrtsNode rrtsNode : rrtsNodeCollection.nearTo(mouse, value)) {
      Tensor other = rrtsNode.state();
      Transition transition = transitionSpace.connect(other, mouse);
      graphics.draw(geometricLayer.toPath2D(transition.linearized(RealScalar.of(.2))));
    }
    // ---
    graphics.setColor(new Color(0, 255, 0, 128));
    for (RrtsNode rrtsNode : rrtsNodeCollection.nearFrom(mouse, value)) {
      Tensor other = rrtsNode.state();
      Transition transition = transitionSpace.connect(mouse, other);
      graphics.draw(geometricLayer.toPath2D(transition.linearized(RealScalar.of(.2))));
    }
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new DubinsNdDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1200, 800);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
