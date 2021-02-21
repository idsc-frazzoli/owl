// code by jph
package ch.ethz.idsc.sophus.app.hermite;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.gds.GeodesicDisplayRender;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gds.S2Display;
import ch.ethz.idsc.sophus.gui.ren.PathRender;
import ch.ethz.idsc.sophus.gui.ren.PointsRender;
import ch.ethz.idsc.sophus.gui.win.ControlPointsDemo;
import ch.ethz.idsc.sophus.hs.sn.SnExponential;
import ch.ethz.idsc.sophus.math.Do;
import ch.ethz.idsc.sophus.math.Geodesic;
import ch.ethz.idsc.sophus.math.TensorIteration;
import ch.ethz.idsc.sophus.opt.HermiteSubdivisions;
import ch.ethz.idsc.sophus.ref.d1h.HermiteSubdivision;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.api.ScalarTensorFunction;

/* package */ class S2HermiteSubdivisionDemo extends ControlPointsDemo {
  private final SpinnerLabel<HermiteSubdivisions> spinnerLabelScheme = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private final SpinnerLabel<Scalar> spinnerBeta = new SpinnerLabel<>();
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
      spinnerRefine.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8));
      spinnerRefine.setValue(4);
      spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    }
    {
      spinnerBeta.setList(Tensors.fromString("{1/8, 1/4, 1/2, 1, 3/2, 2}").stream().map(Scalar.class::cast).collect(Collectors.toList()));
      spinnerBeta.setValue(RealScalar.ONE);
      spinnerBeta.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "beta");
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
      public ManifoldDisplay getGeodesicDisplay() {
        return manifoldDisplay();
      }
    });
    Tensor model2pixel = timerFrame.geometricComponent.getModel2Pixel();
    timerFrame.geometricComponent.setModel2Pixel(Tensors.vector(5, 5, 1).pmul(model2pixel));
    timerFrame.geometricComponent.setOffset(400, 400);
    // ---
    setControlPointsSe2(Tensors.fromString("{{-0.3, 0.0, 0}, {0.0, 0.5, 0.0}, {0.5, 0.5, 1}, {0.5, -0.4, 0}}"));
  }

  private static final PointsRender POINTS_RENDER_0 = //
      new PointsRender(new Color(255, 128, 128, 64), new Color(255, 128, 128, 255));
  private static final Stroke STROKE = //
      new BasicStroke(2.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);
  private static final Tensor GEODESIC_DOMAIN = Subdivide.of(0.0, 1.0, 11);

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    S2Display geodesicDisplay = (S2Display) manifoldDisplay();
    Scalar vscale = spinnerBeta.getValue();
    Tensor control = Tensor.of(getControlPointsSe2().stream().map(xya -> {
      Tensor xy0 = xya.copy();
      xy0.set(Scalar::zero, 2);
      return Tensors.of( //
          geodesicDisplay.project(xy0), //
          geodesicDisplay.createTangent(xy0, xya.Get(2)).multiply(vscale));
    }));
    POINTS_RENDER_0.show(manifoldDisplay()::matrixLift, getControlPointShape(), control.get(Tensor.ALL, 0)).render(geometricLayer, graphics);
    Geodesic geodesicInterface = geodesicDisplay.geodesicInterface();
    { // render tangents as geodesic on sphere
      for (Tensor ctrl : control) {
        Tensor p = ctrl.get(0); // point
        Tensor v = ctrl.get(1); // vector
        if (jToggleButton.isSelected()) {
          Tensor q = new SnExponential(p).exp(v); // point on sphere
          ScalarTensorFunction scalarTensorFunction = geodesicInterface.curve(p, q);
          graphics.setStroke(STROKE);
          Tensor ms = Tensor.of(GEODESIC_DOMAIN.map(scalarTensorFunction).stream().map(geodesicDisplay::toPoint));
          graphics.setColor(Color.LIGHT_GRAY);
          graphics.draw(geometricLayer.toPath2D(ms));
        }
        {
          graphics.setStroke(new BasicStroke(1.5f));
          graphics.setColor(Color.GRAY);
          geometricLayer.pushMatrix(geodesicDisplay.matrixLift(p));
          graphics.draw(geometricLayer.toLine2D(geodesicDisplay.tangentProjection(p).apply(v)));
          geometricLayer.popMatrix();
        }
      }
    }
    HermiteSubdivisions hermiteSubdivisions = spinnerLabelScheme.getValue();
    HermiteSubdivision hermiteSubdivision = hermiteSubdivisions.supply( //
        geodesicDisplay.hsManifold(), //
        geodesicDisplay.hsTransport(), //
        geodesicDisplay.biinvariantMean());
    if (1 < control.length()) {
      TensorIteration tensorIteration = jToggleCyclic.isSelected() //
          ? hermiteSubdivision.cyclic(RealScalar.ONE, control)
          : hermiteSubdivision.string(RealScalar.ONE, control);
      int n = spinnerRefine.getValue();
      Tensor result = Do.of(control, tensorIteration::iterate, n);
      Tensor points = result.get(Tensor.ALL, 0);
      new PathRender(Color.BLUE).setCurve(points, jToggleCyclic.isSelected()).render(geometricLayer, graphics);
      if (jToggleButton.isSelected() && result.length() < 100) {
        for (Tensor pv : result) {
          Tensor p = pv.get(0);
          Tensor v = pv.get(1);
          {
            Tensor q = new SnExponential(p).exp(v); // point on sphere
            ScalarTensorFunction scalarTensorFunction = geodesicInterface.curve(p, q);
            graphics.setStroke(STROKE);
            Tensor ms = Tensor.of(GEODESIC_DOMAIN.map(scalarTensorFunction).stream().map(geodesicDisplay::toPoint));
            graphics.setColor(Color.LIGHT_GRAY);
            graphics.draw(geometricLayer.toPath2D(ms));
          }
          // {
          // Tensor pr = S2GeodesicDisplay.tangentSpace(p).dot(v);
          // geometricLayer.pushMatrix(geodesicDisplay.matrixLift(p));
          // graphics.setStroke(new BasicStroke(1f));
          // graphics.setColor(Color.LIGHT_GRAY);
          // graphics.draw(geometricLayer.toLine2D(pr));
          // geometricLayer.popMatrix();
          // }
        }
      }
    }
  }

  public static void main(String[] args) {
    new S2HermiteSubdivisionDemo().setVisible(1000, 800);
  }
}
