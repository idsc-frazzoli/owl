// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.CurveRender;
import ch.ethz.idsc.sophus.app.api.DubinsGenerator;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.curve.BezierFunction;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.sca.Clip;

/** Bezier function */
/* package */ class BezierFunctionDemo extends ControlPointsDemo {
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();

  BezierFunctionDemo() {
    super(true, GeodesicDisplays.ALL);
    // ---
    addButtonDubins();
    // ---
    spinnerRefine.addSpinnerListener(value -> timerFrame.geometricComponent.jComponent.repaint());
    spinnerRefine.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    spinnerRefine.setValue(8);
    spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    {
      Tensor tensor = Tensors.fromString("{{1,0,0},{2,0,2.5708},{1,0,2.1},{1.5,0,0},{2.3,0,-1.2}}");
      setControl(DubinsGenerator.of(Tensors.vector(0, 0, 2.1), //
          Tensor.of(tensor.stream().map(row -> row.pmul(Tensors.vector(2, 1, 1))))));
    }
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    GraphicsUtil.setQualityHigh(graphics);
    renderControlPoints(geometricLayer, graphics);
    // ---
    ScalarTensorFunction scalarTensorFunction = BezierFunction.of(geodesicDisplay.geodesicInterface(), control());
    int levels = spinnerRefine.getValue();
    Tensor refined = Subdivide.of(Clip.unit(), 1 << levels).map(scalarTensorFunction);
    Tensor render = Tensor.of(refined.stream().map(geodesicDisplay::toPoint));
    new CurveRender(render, false, curvatureButton().isSelected()).render(geometricLayer, graphics);
    if (levels < 5)
      renderPoints(geometricLayer, graphics, refined);
  }

  public static void main(String[] args) {
    BezierFunctionDemo bezierDemo = new BezierFunctionDemo();
    bezierDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 600);
    bezierDemo.timerFrame.jFrame.setVisible(true);
  }
}
