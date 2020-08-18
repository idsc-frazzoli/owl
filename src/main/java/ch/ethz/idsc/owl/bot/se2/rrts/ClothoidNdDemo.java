// code by jph
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.Transition;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.clt.ClothoidBuilder;
import ch.ethz.idsc.sophus.math.sample.BoxRandomSample;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.opt.Pi;

public class ClothoidNdDemo extends ControlPointsDemo {
  private static final int SIZE = 400;
  private static final Tensor LBOUNDS = Tensors.vector(-5, -5).unmodifiable();
  private static final Tensor UBOUNDS = Tensors.vector(+5, +5).unmodifiable();
  // ---
  private final RrtsNodeCollection rrtsNodeCollection1 = //
      Se2RrtsNodeCollections.of(ClothoidTransitionSpace.ANALYTIC, LBOUNDS, UBOUNDS);
  private final RrtsNodeCollection rrtsNodeCollection2 = //
      ClothoidRrtsNodeCollections.of(RealScalar.ONE, LBOUNDS, UBOUNDS);
  private final JToggleButton jToggleButton = new JToggleButton("limit");
  private final SpinnerLabel<Integer> spinnerValue = new SpinnerLabel<>();

  public ClothoidNdDemo() {
    super(false, GeodesicDisplays.CL_ONLY);
    // ---
    jToggleButton.setToolTipText("use limited curvature query");
    timerFrame.jToolBar.add(jToggleButton);
    // ---
    spinnerValue.setList(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 10, 20, 50));
    spinnerValue.setValue(10);
    spinnerValue.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    setPositioningEnabled(false);
    setMidpointIndicated(false);
    // ---
    Random random = new Random();
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of( //
        LBOUNDS.copy().append(Pi.VALUE.negate()), //
        UBOUNDS.copy().append(Pi.VALUE));
    Tensor tensor = Array.of(l -> randomSampleInterface.randomSample(random), SIZE);
    for (Tensor state : tensor) {
      rrtsNodeCollection1.insert(RrtsNode.createRoot(state, RealScalar.ZERO));
      rrtsNodeCollection2.insert(RrtsNode.createRoot(state, RealScalar.ZERO));
    }
    setControlPointsSe2(tensor);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    // ---
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor shape = geodesicDisplay.shape().multiply(RealScalar.of(10 / Math.sqrt(SIZE)));
    Color color_fill = new Color(255, 128, 128, 64);
    Color color_draw = new Color(255, 128, 128, 255);
    for (Tensor point : getGeodesicControlPoints()) {
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(point));
      Path2D path2d = geometricLayer.toPath2D(shape);
      path2d.closePath();
      graphics.setColor(color_fill);
      graphics.fill(path2d);
      graphics.setColor(color_draw);
      graphics.draw(path2d);
      geometricLayer.popMatrix();
    }
    Tensor mouse = geometricLayer.getMouseSe2State();
    {
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(mouse));
      Path2D path2d = geometricLayer.toPath2D(shape);
      path2d.closePath();
      graphics.setColor(Color.CYAN);
      graphics.fill(path2d);
      graphics.setColor(Color.BLUE);
      graphics.draw(path2d);
      geometricLayer.popMatrix();
    }
    // ---
    RrtsNodeCollection rrtsNodeCollection = jToggleButton.isSelected() //
        ? rrtsNodeCollection2
        : rrtsNodeCollection1;
    int value = spinnerValue.getValue();
    graphics.setColor(new Color(255, 0, 0, 128));
    ClothoidBuilder clothoidBuilder = (ClothoidBuilder) geodesicDisplay.geodesicInterface();
    Scalar minResolution = RealScalar.of(geometricLayer.pixel2modelWidth(10));
    for (RrtsNode rrtsNode : rrtsNodeCollection.nearTo(mouse, value)) {
      Tensor other = rrtsNode.state();
      Transition transition = ClothoidTransition.of(clothoidBuilder, other, mouse);
      graphics.draw(geometricLayer.toPath2D(transition.linearized(minResolution)));
    }
    // ---
    graphics.setColor(new Color(0, 255, 0, 128));
    for (RrtsNode rrtsNode : rrtsNodeCollection.nearFrom(mouse, value)) {
      Tensor other = rrtsNode.state();
      Transition transition = ClothoidTransition.of(clothoidBuilder, mouse, other);
      graphics.draw(geometricLayer.toPath2D(transition.linearized(minResolution)));
    }
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new ClothoidNdDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1200, 800);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
