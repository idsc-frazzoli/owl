// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

import ch.ethz.idsc.java.awt.BufferedImageSupplier;
import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.sym.SymLinkImages;
import ch.ethz.idsc.sophus.crv.spline.GeodesicBSplineFunction;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gds.Se2CoveringDisplay;
import ch.ethz.idsc.sophus.gui.ren.Curvature2DRender;
import ch.ethz.idsc.sophus.gui.win.DubinsGenerator;
import ch.ethz.idsc.sophus.math.Geodesic;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Range;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.api.ScalarTensorFunction;
import ch.ethz.idsc.tensor.itp.DeBoor;

// TODO JPH demo does not seem correct
/* package */ public class GeodesicDeBoorDemo extends AbstractCurveDemo implements BufferedImageSupplier {
  private BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

  public GeodesicDeBoorDemo() {
    addButtonDubins();
    // ---
    setGeodesicDisplay(Se2CoveringDisplay.INSTANCE);
    // ---
    Tensor dubins = Tensors.fromString("{{1, 0, 0}, {2, 0, 2.5708}}");
    setControlPointsSe2(DubinsGenerator.of(Tensors.vector(0, 0, 0), //
        Tensor.of(dubins.stream().map(row -> row.pmul(Tensors.vector(2, 1, 1))))));
  }

  @Override // from RenderInterface
  public Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics, int degree, int levels, Tensor control) {
    final int upper = control.length() - 1;
    final Scalar parameter = sliderRatio().multiply(RealScalar.of(upper));
    Tensor knots = Range.of(0, 2 * upper);
    bufferedImage = SymLinkImages.deboor(knots, control.length(), parameter).bufferedImage();
    // ---
    RenderQuality.setQuality(graphics);
    renderControlPoints(geometricLayer, graphics); // control points
    // ---
    ManifoldDisplay geodesicDisplay = manifoldDisplay();
    Geodesic geodesicInterface = geodesicDisplay.geodesicInterface();
    ScalarTensorFunction scalarTensorFunction = //
        DeBoor.of(geodesicInterface, knots, control);
    GeodesicBSplineFunction.of(geodesicDisplay.geodesicInterface(), degree, control);
    Scalar center = RationalScalar.of(control.length() - 1, 2);
    Tensor refined = Subdivide.of( //
        center.subtract(RationalScalar.HALF), //
        center.add(RationalScalar.HALF), //
        Math.max(1, upper * (1 << levels))).map(scalarTensorFunction);
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

  @Override
  public BufferedImage bufferedImage() {
    return bufferedImage;
  }

  public static void main(String[] args) {
    new GeodesicDeBoorDemo().setVisible(1200, 600);
  }
}
