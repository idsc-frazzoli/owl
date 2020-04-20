// code by jph
package ch.ethz.idsc.sophus.app.hermite;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.Arrays;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.PathRender;
import ch.ethz.idsc.sophus.app.PointsRender;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplayRender;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.S2GeodesicDisplay;
import ch.ethz.idsc.sophus.crv.hermite.HermiteSubdivision;
import ch.ethz.idsc.sophus.crv.hermite.HermiteSubdivisions;
import ch.ethz.idsc.sophus.hs.s2.S2Transport;
import ch.ethz.idsc.sophus.hs.sn.SnExponential;
import ch.ethz.idsc.sophus.math.Do;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.math.TensorIteration;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

/* package */ class S2HermiteSubdivisionDemo extends ControlPointsDemo {
  private final SpinnerLabel<HermiteSubdivisions> spinnerLabelScheme = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private final JToggleButton jToggleCyclic = new JToggleButton("cyclic");
  private final JToggleButton jToggleButton = new JToggleButton("derivatives");

  public S2HermiteSubdivisionDemo() {
    super(true, GeodesicDisplays.S2_ONLY);
    // ---
    {
      spinnerLabelScheme.setArray(HermiteSubdivisions.values());
      spinnerLabelScheme.setValue(HermiteSubdivisions.HERMITE1);
      spinnerLabelScheme.addToComponentReduced(timerFrame.jToolBar, new Dimension(140, 28), "scheme");
    }
    {
      spinnerRefine.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
      spinnerRefine.setValue(4);
      spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    }
    timerFrame.jToolBar.addSeparator();
    {
      jToggleCyclic.setToolTipText("cyclic curve");
      timerFrame.jToolBar.add(jToggleCyclic);
    }
    {
      jToggleButton.setSelected(true);
      jToggleButton.setToolTipText("show derivatives");
      timerFrame.jToolBar.add(jToggleButton);
    }
    timerFrame.geometricComponent.addRenderInterfaceBackground(new GeodesicDisplayRender() {
      @Override
      public GeodesicDisplay getGeodesicDisplay() {
        return geodesicDisplay();
      }
    });
    Tensor model2pixel = timerFrame.geometricComponent.getModel2Pixel();
    timerFrame.geometricComponent.setModel2Pixel(Tensors.vector(5, 5, 1).pmul(model2pixel));
    timerFrame.configCoordinateOffset(400, 400);
    // ---
    setControlPointsSe2(Tensors.fromString("{{-0.3, 0.0, 0}, {0.0, 0.5, 0.0}, {0.5, 0.5, 1}, {0.5, -0.4, 0}}"));
  }

  private static final PointsRender POINTS_RENDER_0 = //
      new PointsRender(new Color(255, 128, 128, 64), new Color(255, 128, 128, 255));
  private static final Stroke STROKE = //
      new BasicStroke(2.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    S2GeodesicDisplay geodesicDisplay = (S2GeodesicDisplay) geodesicDisplay();
    Tensor control = Tensor.of(getControlPointsSe2().stream().map(xya -> {
      Tensor xy0 = xya.copy();
      xy0.set(Scalar::zero, 2);
      return Tensors.of( //
          geodesicDisplay.project(xy0), //
          geodesicDisplay.projectTangent(xy0, xya.Get(2)));
    }));
    POINTS_RENDER_0.show(geodesicDisplay()::matrixLift, getControlPointShape(), control.get(Tensor.ALL, 0)).render(geometricLayer, graphics);
    GeodesicInterface geodesicInterface = geodesicDisplay.geodesicInterface();
    for (Tensor ctrl : control) {
      Tensor p = ctrl.get(0);
      Tensor q = new SnExponential(p).exp(ctrl.get(1).multiply(RealScalar.of(0.3)));
      ScalarTensorFunction scalarTensorFunction = geodesicInterface.curve(p, q);
      graphics.setStroke(STROKE);
      Tensor domain = Subdivide.of(0, 1, 21);
      Tensor ms = Tensor.of(domain.map(scalarTensorFunction).stream().map(geodesicDisplay::toPoint));
      graphics.setColor(Color.LIGHT_GRAY);
      graphics.draw(geometricLayer.toPath2D(ms));
    }
    HermiteSubdivision hermiteSubdivision = spinnerLabelScheme.getValue().supply( //
        geodesicDisplay.hsExponential(), S2Transport.INSTANCE, geodesicDisplay.biinvariantMean());
    // Hermite2Subdivisions.standard(geodesicDisplay.hsExponential(), S2Transport.INSTANCE);
    if (1 < control.length()) {
      TensorIteration tensorIteration = jToggleCyclic.isSelected() //
          ? hermiteSubdivision.cyclic(RealScalar.ONE, control)
          : hermiteSubdivision.string(RealScalar.ONE, control);
      int n = spinnerRefine.getValue();
      Tensor result = 0 < n //
          ? Do.of(tensorIteration::iterate, n)
          : control;
      Tensor points = result.get(Tensor.ALL, 0);
      // POINTS_RENDER_0.show(geodesicDisplay::matrixLift, geodesicDisplay.shape(), points).render(geometricLayer, graphics);
      new PathRender(Color.BLUE).setCurve(points, jToggleCyclic.isSelected()).render(geometricLayer, graphics);
    }
    // if (1 < tensor.length()) {
    //
    // Tensor control;
    // switch (geodesicDisplay.toString()) {
    // case "SE2C":
    // case "SE2":
    // // TODO use various options: unit vector, scaled by parametric distance, ...
    // control = Tensor.of(tensor.stream().map(xya -> Tensors.of(xya, UnitVector.of(3, 0))));
    // break;
    // case "R2":
    // // TODO use various options: unit vector, scaled by parametric distance, ...
    // control = Tensor.of(tensor.stream().map(xya -> Tensors.of(xya.extract(0, 2), AngleVector.of(xya.Get(2)))));
    // break;
    // default:
    // return;
    // }
    // {
    // Tensor distances = Distances.of(Se2ClothoidDistance.INSTANCE, tensor);
    // // Distances.of(geodesicDisplay::parametricDistance, control.get(Tensor.ALL, 0));
    // if (0 < distances.length()) {
    // Tensor scaling = Array.zeros(control.length());
    // scaling.set(distances.get(0), 0);
    // for (int index = 1; index < distances.length(); ++index)
    // scaling.set(Mean.of(distances.extract(index - 1, index + 1)), index);
    // scaling.set(Last.of(distances).Get(), control.length() - 1);
    // // ---
    // for (int index = 0; index < control.length(); ++index) {
    // int fi = index;
    // control.set(t -> t.multiply(scaling.Get(fi)), index, 1);
    // }
    // }
    // }
    // HermiteSubdivision hermiteSubdivision = spinnerLabelScheme.getValue().supply( //
    // geodesicDisplay.lieGroup(), geodesicDisplay.exponential(), geodesicDisplay.biinvariantMean());
    // TensorIteration tensorIteration = hermiteSubdivision.string(RealScalar.ONE, control);
    // int levels = spinnerRefine.getValue();
    // Tensor iterate = Do.of(tensorIteration::iterate, levels);
    // Tensor curve = Tensor.of(iterate.get(Tensor.ALL, 0).stream().map(Extract2D.FUNCTION));
    // Curvature2DRender.of(curve, false, geometricLayer, graphics);
    // {
    // Scalar scale = RealScalar.of(0.3);
    // switch (geodesicDisplay.toString()) {
    // case "SE2C":
    // case "SE2":
    // new Se2HermitePlot(iterate, scale).render(geometricLayer, graphics);
    // break;
    // case "R2":
    // new R2HermitePlot(iterate, scale).render(geometricLayer, graphics);
    // break;
    // }
    // }
    // // ---
    // if (jToggleButton.isSelected()) {
    // Tensor deltas = iterate.get(Tensor.ALL, 1);
    // if (0 < deltas.length()) {
    // JFreeChart jFreeChart = StaticHelper.listPlot(deltas);
    // Dimension dimension = timerFrame.geometricComponent.jComponent.getSize();
    // jFreeChart.draw(graphics, new Rectangle2D.Double(dimension.width - WIDTH, 0, WIDTH, HEIGHT));
    // }
    // }
    // }
  }

  public static void main(String[] args) {
    new S2HermiteSubdivisionDemo().setVisible(1000, 800);
  }
}
