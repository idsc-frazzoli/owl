// code by jph
package ch.ethz.idsc.sophus.app.misc;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.List;

import ch.ethz.idsc.owl.bot.util.DemoInterface;
import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.BaseFrame;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

public class GeodesicDemo extends AbstractDemo implements DemoInterface {
  private static final Color COLOR = new Color(128, 128, 128, 128);
  private static final int SPLITS = 20;
  // ---
  private final PathRender pathRender = new PathRender(new Color(128, 128, 255), //
      new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0));
  private final SpinnerLabel<GeodesicDisplay> geodesicDisplaySpinner = new SpinnerLabel<>();

  public GeodesicDemo() {
    List<GeodesicDisplay> list = GeodesicDisplays.ALL;
    geodesicDisplaySpinner.setList(list);
    geodesicDisplaySpinner.setIndex(0);
    if (1 < list.size()) {
      geodesicDisplaySpinner.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "geodesic type");
      timerFrame.jToolBar.addSeparator();
    }
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GraphicsUtil.setQualityHigh(graphics);
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    GeodesicDisplay geodesicDisplay = geodesicDisplaySpinner.getValue();
    GeodesicInterface geodesicInterface = geodesicDisplay.geodesicInterface();
    Tensor xya = geometricLayer.getMouseSe2State();
    graphics.setColor(COLOR);
    Tensor q = geodesicDisplay.project(xya);
    ScalarTensorFunction scalarTensorFunction = //
        geodesicInterface.curve(geodesicDisplay.project(xya.map(Scalar::zero)), q);
    for (Tensor split : Subdivide.of(0, 1, SPLITS).map(scalarTensorFunction)) {
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(split));
      graphics.fill(geometricLayer.toPath2D(geodesicDisplay.shape()));
      geometricLayer.popMatrix();
    }
    {
      Tensor refined = Subdivide.of(0, 1, SPLITS * 6).map(scalarTensorFunction);
      Tensor render = Tensor.of(refined.stream().map(geodesicDisplay::toPoint));
      CurveCurvatureRender.of(render, false, geometricLayer, graphics);
    }
    {
      Tensor refined = Subdivide.of(1, 1.5, SPLITS * 3).map(scalarTensorFunction);
      Tensor render = Tensor.of(refined.stream().map(geodesicDisplay::toPoint));
      // CurveCurvatureRender.of(render, false, geometricLayer, graphics);
      pathRender.setCurve(render, false);
      pathRender.render(geometricLayer, graphics);
    }
    graphics.setColor(new Color(255, 128, 128));
    for (Tensor split : Subdivide.of(1, 1.5, SPLITS).map(scalarTensorFunction)) {
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(split));
      graphics.fill(geometricLayer.toPath2D(geodesicDisplay.shape().multiply(RealScalar.of(.3))));
      geometricLayer.popMatrix();
    }
    GraphicsUtil.setQualityDefault(graphics);
  }

  @Override // from DemoInterface
  public BaseFrame start() {
    return timerFrame;
  }

  public static void main(String[] args) {
    GeodesicDemo geodesicDemo = new GeodesicDemo();
    geodesicDemo.timerFrame.jFrame.setBounds(100, 100, 600, 600);
    geodesicDemo.timerFrame.jFrame.setVisible(true);
  }
}
