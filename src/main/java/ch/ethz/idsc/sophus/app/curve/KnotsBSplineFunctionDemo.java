// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.stream.IntStream;

import javax.swing.JSlider;

import ch.ethz.idsc.java.awt.BufferedImageSupplier;
import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.Curvature2DRender;
import ch.ethz.idsc.sophus.app.api.DubinsGenerator;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.sym.SymGeodesic;
import ch.ethz.idsc.sophus.app.sym.SymLinkImage;
import ch.ethz.idsc.sophus.app.sym.SymLinkImages;
import ch.ethz.idsc.sophus.app.sym.SymScalar;
import ch.ethz.idsc.sophus.crv.spline.GeodesicBSplineFunction;
import ch.ethz.idsc.sophus.math.win.KnotSpacing;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.DeBoor;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

public class KnotsBSplineFunctionDemo extends BaseCurvatureDemo implements BufferedImageSupplier {
  private final JSlider jSliderExponent = new JSlider(0, 100, 100);
  private BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

  public KnotsBSplineFunctionDemo() {
    super(GeodesicDisplays.CL_SE2_R2);
    // ---
    jSliderExponent.setPreferredSize(new Dimension(200, 28));
    jSliderExponent.setToolTipText("centripetal exponent");
    timerFrame.jToolBar.add(jSliderExponent);
    // ---
    Tensor dubins = Tensors.fromString(
        "{{1, 0, 0}, {1, 0, 0}, {2, 0, 2.5708}, {1, 0, 2.1}, {1.5, 0, 0}, {2.3, 0, -1.2}, {1.5, 0, 0}, {4, 0, 3.14159}, {2, 0, 3.14159}, {2, 0, 0}}");
    setControlPointsSe2(DubinsGenerator.of(Tensors.vector(0, 0, 2.1), //
        Tensor.of(dubins.stream().map(row -> row.pmul(Tensors.vector(2, 1, 1))))));
  }

  @Override // from RenderInterface
  protected Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics, int degree, int levels, Tensor control) {
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Scalar exponent = RationalScalar.of(jSliderExponent.getValue(), jSliderExponent.getMaximum());
    Tensor knots = KnotSpacing.centripetal(geodesicDisplay.parametricDistance(), exponent).apply(control);
    Scalar upper = Last.of(knots);
    Scalar parameter = sliderRatio().multiply(upper);
    // ---
    GeodesicBSplineFunction scalarTensorFunction = //
        GeodesicBSplineFunction.of(geodesicDisplay.geodesicInterface(), degree, knots, control);
    {
      DeBoor geodesicDeBoor = scalarTensorFunction.deBoor(parameter);
      SymLinkImage symLinkImage = KnotsBSplineFunctionDemo.deboor(geodesicDeBoor, geodesicDeBoor.degree() + 1, parameter);
      bufferedImage = symLinkImage.bufferedImage();
    }
    // ---
    RenderQuality.setQuality(graphics);
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
    Curvature2DRender.of(render, false, geometricLayer, graphics);
    if (levels < 5)
      renderPoints(geodesicDisplay, refined, geometricLayer, graphics);
    return refined;
  }

  @Override // from BufferedImageSupplier
  public BufferedImage bufferedImage() {
    return bufferedImage;
  }

  public static SymLinkImage deboor(DeBoor geodesicDeBoor, int length, Scalar scalar) {
    Tensor knots = geodesicDeBoor.knots();
    Tensor vector = Tensor.of(IntStream.range(0, length).mapToObj(SymScalar::leaf));
    // Tensor vector = knots.map(s->s.subtract(knots.Get(0))).map(SymScalar::leaf);
    ScalarTensorFunction scalarTensorFunction = DeBoor.of(SymGeodesic.INSTANCE, knots, vector);
    Tensor tensor = scalarTensorFunction.apply(scalar);
    SymLinkImage symLinkImage = new SymLinkImage((SymScalar) tensor, SymLinkImages.FONT_SMALL);
    symLinkImage.title("DeBoor at " + scalar);
    return symLinkImage;
  }

  public static void main(String[] args) {
    new KnotsBSplineFunctionDemo().setVisible(1200, 600);
  }
}
