// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

import ch.ethz.idsc.owl.bot.rn.RnTransitionSpace;
import ch.ethz.idsc.owl.bot.rn.rrts.EuclideanNdType;
import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.rrts.NdTypeRrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.owl.rrts.core.TransitionSpace;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
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

/** this demo maps the GeodesicDisplays
 * 
 * Clothoid -> ClothoidTransitionSpace
 * SE2 -> DubinsTransitionSpace
 * R2 -> RnTransitionSpace
 * 
 * this design is not extendable!
 * do not reproduce this design! */
public class TransitionNdDemo extends ControlPointsDemo {
  private static enum Se2TransitionNdType {
    CLOTHOID(ClothoidTransitionSpace.INSTANCE), //
    DUBINS(DubinsTransitionSpace.of(RealScalar.of(0.4), DubinsPathComparator.LENGTH)), //
    R2(RnTransitionSpace.INSTANCE), //
    ;
    private final TransitionSpace transitionSpace;

    private Se2TransitionNdType(TransitionSpace transitionSpace) {
      this.transitionSpace = transitionSpace;
    }
  }

  private static final Tensor LBOUNDS = Tensors.vector(-5, -5).unmodifiable();
  private static final Tensor UBOUNDS = Tensors.vector(+5, +5).unmodifiable();
  // ---
  private final Map<Se2TransitionNdType, RrtsNodeCollection> map = new EnumMap<>(Se2TransitionNdType.class);
  private final SpinnerLabel<Integer> spinnerValue = new SpinnerLabel<>();
  private final PointsRender pointsRender = //
      new PointsRender(new Color(128, 128, 128, 64), new Color(128, 128, 128, 255));
  private final Scalar minResolution = RealScalar.of(0.1);

  public TransitionNdDemo() {
    super(false, GeodesicDisplays.CLOTH_SE2_R2);
    // ---
    spinnerValue.setList(Arrays.asList(1, 2, 3, 4, 5, 10, 20));
    spinnerValue.setValue(3);
    spinnerValue.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    setPositioningEnabled(false);
    setMidpointIndicated(false);
    // ---
    Random random = new Random(1);
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of( //
        LBOUNDS.copy().append(Pi.VALUE.negate()), //
        UBOUNDS.copy().append(Pi.VALUE));
    Tensor tensor = Array.of(l -> randomSampleInterface.randomSample(random), 20);
    for (GeodesicDisplay geodesicDisplay : GeodesicDisplays.CLOTH_SE2_R2) {
      Se2TransitionNdType se2TransitionNdType = se2TransitionNdType(geodesicDisplay);
      RrtsNodeCollection rrtsNodeCollection = se2TransitionNdType.equals(Se2TransitionNdType.R2) //
          ? NdTypeRrtsNodeCollection.of(EuclideanNdType.INSTANCE, LBOUNDS, UBOUNDS)
          : Se2TransitionRrtsNodeCollections.of( //
              se2TransitionNdType.transitionSpace, LBOUNDS, UBOUNDS);
      for (Tensor state : tensor)
        rrtsNodeCollection.insert(RrtsNode.createRoot(geodesicDisplay.project(state), RealScalar.ZERO));
      map.put(se2TransitionNdType, rrtsNodeCollection);
    }
    setControlPointsSe2(tensor);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    // AxesRender.INSTANCE.render(geometricLayer, graphics);
    // ---
    GraphicsUtil.setQualityHigh(graphics);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor shape = geodesicDisplay.shape().multiply(RealScalar.of(1.0));
    pointsRender.new Show(geodesicDisplay, shape, getGeodesicControlPoints()) //
        .render(geometricLayer, graphics);
    Tensor mouse = geodesicDisplay.project(geometricLayer.getMouseSe2State());
    // ---
    Se2TransitionNdType se2TransitionNdType = se2TransitionNdType(geodesicDisplay());
    RrtsNodeCollection rrtsNodeCollection = map.get(se2TransitionNdType);
    TransitionSpace transitionSpace = se2TransitionNdType.transitionSpace;
    int value = spinnerValue.getValue();
    graphics.setStroke(new BasicStroke(1.5f));
    graphics.setColor(new Color(255, 0, 0, 128));
    for (RrtsNode rrtsNode : rrtsNodeCollection.nearTo(mouse, value)) {
      Transition transition = transitionSpace.connect(rrtsNode.state(), mouse);
      graphics.draw(geometricLayer.toPath2D(transition.linearized(minResolution)));
    }
    // ---
    graphics.setColor(new Color(0, 255, 0, 128));
    for (RrtsNode rrtsNode : rrtsNodeCollection.nearFrom(mouse, value)) {
      Transition transition = transitionSpace.connect(mouse, rrtsNode.state());
      graphics.draw(geometricLayer.toPath2D(transition.linearized(minResolution)));
    }
  }

  private static Se2TransitionNdType se2TransitionNdType(GeodesicDisplay geodesicDisplay) {
    switch (geodesicDisplay.toString()) {
    case "Cl":
      return Se2TransitionNdType.CLOTHOID;
    case "SE2":
      return Se2TransitionNdType.DUBINS;
    case "R2":
      return Se2TransitionNdType.R2;
    }
    throw new IllegalArgumentException();
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new TransitionNdDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1200, 800);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
