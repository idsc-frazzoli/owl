// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.Objects;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.BufferedImageSupplier;
import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.sym.SymLinkImages;
import ch.ethz.idsc.sophus.crv.spline.AbstractBSplineInterpolation;
import ch.ethz.idsc.sophus.crv.spline.AbstractBSplineInterpolation.Iteration;
import ch.ethz.idsc.sophus.crv.spline.GeodesicBSplineFunction;
import ch.ethz.idsc.sophus.crv.spline.GeodesicBSplineInterpolation;
import ch.ethz.idsc.sophus.crv.spline.LieGroupBSplineInterpolation;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gui.ren.Curvature2DRender;
import ch.ethz.idsc.sophus.gui.win.DubinsGenerator;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.api.ScalarTensorFunction;
import ch.ethz.idsc.tensor.lie.r2.CirclePoints;
import ch.ethz.idsc.tensor.sca.Chop;

/* package */ public class GeodesicBSplineFunctionDemo extends AbstractCurveDemo implements BufferedImageSupplier {
  private final JToggleButton jToggleItrp = new JToggleButton("interp");
  private BufferedImage bufferedImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);

  public GeodesicBSplineFunctionDemo() {
    addButtonDubins();
    // ---
    timerFrame.jToolBar.add(jToggleItrp);
    // ---
    Tensor dubins = Tensors.fromString(
        "{{1, 0, 0}, {1, 0, 0}, {2, 0, 2.5708}, {1, 0, 2.1}, {1.5, 0, 0}, {2.3, 0, -1.2}, {1.5, 0, 0}, {4, 0, 3.14159}, {2, 0, 3.14159}, {2, 0, 0}}");
    setControlPointsSe2(DubinsGenerator.of(Tensors.vector(0, 0, 2.1), //
        Tensor.of(dubins.stream().map(row -> row.pmul(Tensors.vector(2, 1, 1))))));
  }

  @Override // from RenderInterface
  public Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics, int degree, int levels, Tensor control) {
    final int upper = control.length() - 1;
    final Scalar parameter = sliderRatio().multiply(RealScalar.of(upper));
    bufferedImage = SymLinkImages.symLinkImageGBSF(degree, upper + 1, parameter).bufferedImage();
    // ---
    RenderQuality.setQuality(graphics);
    renderControlPoints(geometricLayer, graphics); // control points
    // ---
    ManifoldDisplay geodesicDisplay = manifoldDisplay();
    Tensor effective = control;
    if (jToggleItrp.isSelected()) {
      LieGroup lieGroup = geodesicDisplay.lieGroup();
      AbstractBSplineInterpolation abstractBSplineInterpolation = Objects.isNull(lieGroup) //
          ? new GeodesicBSplineInterpolation(geodesicDisplay.geodesicInterface(), degree, control)
          : new LieGroupBSplineInterpolation(lieGroup, geodesicDisplay.geodesicInterface(), degree, control);
      {
        Tensor tensor = BSplineInterpolationSequence.of(abstractBSplineInterpolation);
        Tensor shape = CirclePoints.of(9).multiply(RealScalar.of(0.05));
        graphics.setColor(new Color(64, 64, 64, 64));
        for (Tensor ctrls : tensor)
          for (Tensor ctrl : ctrls) {
            geometricLayer.pushMatrix(geodesicDisplay.matrixLift(ctrl));
            Path2D path2d = geometricLayer.toPath2D(shape);
            graphics.fill(path2d);
            geometricLayer.popMatrix();
          }
        graphics.setColor(new Color(64, 64, 64, 192));
        for (Tensor ctrls : Transpose.of(tensor))
          graphics.draw(geometricLayer.toPath2D(Tensor.of(ctrls.stream().map(geodesicDisplay::toPoint))));
      }
      Iteration iteration = abstractBSplineInterpolation.untilClose(Chop._06, 100);
      {
        graphics.setColor(Color.BLACK);
        graphics.drawString("" + iteration.steps(), 0, 20);
      }
      effective = iteration.control();
    }
    ScalarTensorFunction scalarTensorFunction = //
        GeodesicBSplineFunction.of(geodesicDisplay.geodesicInterface(), degree, effective);
    {
      Tensor selected = scalarTensorFunction.apply(parameter);
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(selected));
      Path2D path2d = geometricLayer.toPath2D(geodesicDisplay.shape());
      graphics.setColor(Color.DARK_GRAY);
      graphics.fill(path2d);
      geometricLayer.popMatrix();
    }
    Tensor refined = Subdivide.of(0, upper, Math.max(1, upper * (1 << levels))).map(scalarTensorFunction);
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
    new GeodesicBSplineFunctionDemo().setVisible(1200, 600);
  }
}
