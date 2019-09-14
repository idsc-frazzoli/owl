// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

import ch.ethz.idsc.owl.bot.rn.RnTransitionSpace;
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
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.opt.Pi;

public class Se2TransitionNdDemo extends ControlPointsDemo {
  private static enum Se2TransitionNdType {
    CLOTHOID(ClothoidTransitionSpace.INSTANCE), //
    DUBINS(DubinsTransitionSpace.of(RealScalar.of(0.4), DubinsPathComparator.LENGTH)), //
    R2(RnTransitionSpace.INSTANCE), //
    ;
    private final TransitionSpace transitionSpace;

    private Se2TransitionNdType(TransitionSpace transitionSpace) {
      this.transitionSpace = transitionSpace;
    }

    public TransitionSpace transitionSpace() {
      return transitionSpace;
    }
  }

  private static final Tensor LBOUNDS = Tensors.vector(-5, -5).unmodifiable();
  private static final Tensor UBOUNDS = Tensors.vector(+5, +5).unmodifiable();
  // ---
  private final Map<Se2TransitionNdType, RrtsNodeCollection> map = new EnumMap<>(Se2TransitionNdType.class);
  // private final SpinnerLabel<Se2TransitionNdType> spinnerType = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerValue = new SpinnerLabel<>();
  private final PointsRender pointsRender = //
      new PointsRender(new Color(128, 128, 128, 64), new Color(128, 128, 128, 255));

  public Se2TransitionNdDemo() {
    super(false, GeodesicDisplays.CLOTH_SE2_R2);
    // ---
    spinnerValue.setList(Arrays.asList(1, 2, 3, 4, 5, 10, 20));
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
    for (Se2TransitionNdType se2TransitionNdType : Se2TransitionNdType.values()) {
      RrtsNodeCollection rrtsNodeCollection = Se2TransitionRrtsNodeCollections.of( //
          se2TransitionNdType.transitionSpace(), LBOUNDS, UBOUNDS);
      for (Tensor state : tensor)
        rrtsNodeCollection.insert(RrtsNode.createRoot(state, RealScalar.ZERO));
      map.put(se2TransitionNdType, rrtsNodeCollection);
    }
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
    Se2TransitionNdType se2TransitionNdType = null;
    switch (geodesicDisplay().toString()) {
    case "Cl":
      se2TransitionNdType = Se2TransitionNdType.CLOTHOID;
      break;
    case "SE2":
      se2TransitionNdType = Se2TransitionNdType.DUBINS;
      break;
    case "R2":
      se2TransitionNdType = Se2TransitionNdType.R2;
      break;
    default:
      break;
    }
    RrtsNodeCollection rrtsNodeCollection = map.get(se2TransitionNdType);
    TransitionSpace transitionSpace = se2TransitionNdType.transitionSpace();
    int value = spinnerValue.getValue();
    graphics.setColor(new Color(255, 0, 0, 128));
    for (RrtsNode rrtsNode : rrtsNodeCollection.nearTo(mouse, value)) {
      Tensor other = rrtsNode.state();
      Transition transition = transitionSpace.connect(other, mouse);
      graphics.draw(geometricLayer.toPath2D(transition.linearized(RealScalar.of(.1))));
    }
    // ---
    graphics.setColor(new Color(0, 255, 0, 128));
    for (RrtsNode rrtsNode : rrtsNodeCollection.nearFrom(mouse, value)) {
      Tensor other = rrtsNode.state();
      Transition transition = transitionSpace.connect(mouse, other);
      graphics.draw(geometricLayer.toPath2D(transition.linearized(RealScalar.of(.1))));
    }
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new Se2TransitionNdDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1200, 800);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
