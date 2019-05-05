// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import javax.swing.JSlider;
import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.DubinsGenerator;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.misc.CurveCurvatureRender;
import ch.ethz.idsc.sophus.curve.GeodesicBSplineFunction;
import ch.ethz.idsc.sophus.curve.GeodesicDeBoor;
import ch.ethz.idsc.sophus.sym.SymLinkImage;
import ch.ethz.idsc.sophus.sym.SymLinkImages;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Accumulate;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.sca.Power;

public class KnotsBSplineFunctionDemo extends BaseCurvatureDemo {
  private final JToggleButton jToggleUnif = new JToggleButton("unif");
  private final JSlider jSliderCentripetal = new JSlider(0, 100, 100);

  public KnotsBSplineFunctionDemo() {
    super(GeodesicDisplays.CLOTH_SE2_R2);
    // addButtonDubins();
    // ---
    timerFrame.jToolBar.add(jToggleUnif);
    // ---
    jSliderCentripetal.setPreferredSize(new Dimension(500, 28));
    timerFrame.jToolBar.add(jSliderCentripetal);
    // ---
    Tensor dubins = Tensors.fromString("{{1,0,0},{1,0,0},{2,0,2.5708},{1,0,2.1},{1.5,0,0},{2.3,0,-1.2},{1.5,0,0},{4,0,3.14159},{2,0,3.14159},{2,0,0}}");
    setControlPointsSe2(DubinsGenerator.of(Tensors.vector(0, 0, 2.1), //
        Tensor.of(dubins.stream().map(row -> row.pmul(Tensors.vector(2, 1, 1))))));
  }

  @Override // from RenderInterface
  public Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    final int degree = spinnerDegree.getValue();
    final int levels = spinnerRefine.getValue();
    final Tensor control = getGeodesicControlPoints();
    // ---
    Tensor effective = control;
    Tensor diffs = Tensors.vector(0);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    if (!jToggleUnif.isSelected()) {
      // Centripetal method: "B-Spline Interpolation and Approximation Hongxin Zhang and Jieqing Feng"
      Scalar exponent = RationalScalar.of(jSliderCentripetal.getValue(), 100);
      for (int index = 1; index < control.length(); ++index) {
        Scalar scalar = geodesicDisplay.parametricDistance(control.get(index - 1), control.get(index));
        diffs.append(Power.of(scalar, exponent));
      }
    }
    Tensor knots = jToggleUnif.isSelected() //
        ? Range.of(0, control.length())
        : Accumulate.of(diffs);
    final Scalar upper = (Scalar) Last.of(knots);
    final Scalar parameter = RationalScalar.of(jSlider.getValue(), jSlider.getMaximum()).multiply(upper);
    // ---
    GeodesicBSplineFunction scalarTensorFunction = //
        GeodesicBSplineFunction.of(geodesicDisplay.geodesicInterface(), degree, knots, effective);
    if (jToggleSymi.isSelected()) {
      GeodesicDeBoor geodesicDeBoor = scalarTensorFunction.deBoor(parameter);
      SymLinkImage symLinkImage = SymLinkImages.deboor(geodesicDeBoor, geodesicDeBoor.degree() + 1, parameter);
      graphics.drawImage(symLinkImage.bufferedImage(), 0, 0, null);
    }
    // ---
    GraphicsUtil.setQualityHigh(graphics);
    renderControlPoints(geometricLayer, graphics); // control points
    Tensor refined = Subdivide.of(RealScalar.ZERO, upper, Math.max(1, control.length() * (1 << levels))).map(scalarTensorFunction);
    {
      Tensor selected = scalarTensorFunction.apply(parameter);
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(selected));
      Path2D path2d = geometricLayer.toPath2D(geodesicDisplay.shape());
      graphics.setColor(Color.DARK_GRAY);
      graphics.fill(path2d);
      geometricLayer.popMatrix();
    }
    Tensor render = Tensor.of(refined.stream().map(geodesicDisplay::toPoint));
    CurveCurvatureRender.of(render, false, geometricLayer, graphics);
    if (levels < 5)
      renderPoints(geodesicDisplay, refined, geometricLayer, graphics);
    return refined;
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new KnotsBSplineFunctionDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1200, 600);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
