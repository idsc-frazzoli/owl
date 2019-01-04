// code by jph
package ch.ethz.idsc.sophus.app.misc;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.List;

import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.curve.GeodesicBSplineFunction;
import ch.ethz.idsc.sophus.group.RnGeodesic;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.opt.BSplineInterpolation;

/* package */ class BSplineBasisDemo extends ControlPointsDemo {
  private static final List<Integer> DEGREES = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.cyclic().deriveWithAlpha(192);
  // ---
  private final SpinnerLabel<Integer> spinnerDegree = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private final JToggleButton jToggleItrp = new JToggleButton("interp");

  BSplineBasisDemo() {
    super(true, true, GeodesicDisplays.R2_ONLY);
    // ---
    timerFrame.jToolBar.add(jToggleItrp);
    // ---
    spinnerDegree.setList(DEGREES);
    spinnerDegree.setValue(1);
    spinnerDegree.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "degree");
    // ---
    spinnerRefine.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));
    spinnerRefine.setValue(4);
    spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    // ---
    setControl(Tensors.fromString("{{0, 0, 0}, {1, 0, 0}}"));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GraphicsUtil.setQualityHigh(graphics);
    int degree = spinnerDegree.getValue();
    int levels = spinnerRefine.getValue();
    Tensor control = control();
    {
      graphics.setStroke(new BasicStroke(1.25f));
      Tensor matrix = geometricLayer.getMatrix();
      geometricLayer.pushMatrix(Inverse.of(matrix));
      {
        for (int length = 2; length <= 8; ++length) {
          Tensor string = Tensors.fromString("{{100, 0, 0}, {0, -100, 0}, {0, 0, 1}}");
          string.set(RealScalar.of(110 * length), 1, 2);
          geometricLayer.pushMatrix(string);
          for (int k_th = 0; k_th < length; ++k_th) {
            GeodesicBSplineFunction bSplineFunction = //
                GeodesicBSplineFunction.of(RnGeodesic.INSTANCE, degree, UnitVector.of(length, k_th));
            Tensor domain = Subdivide.of(0, length - 1, 100);
            Tensor values = domain.map(bSplineFunction);
            Tensor tensor = Transpose.of(Tensors.of(domain, values));
            graphics.setColor(COLOR_DATA_INDEXED.getColor(k_th));
            graphics.draw(geometricLayer.toPath2D(tensor));
            // ---
            graphics.setColor(new Color(0, 0, 0, 128));
            graphics.draw(geometricLayer.toPath2D(Tensors.matrix(new Number[][] { { k_th, 0 }, { k_th, .1 } })));
          }
          geometricLayer.popMatrix();
        }
      }
      geometricLayer.popMatrix();
      graphics.setStroke(new BasicStroke(1f));
    }
    // ---
    final Tensor refined;
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    {
      Tensor rnctrl = jToggleItrp.isSelected() //
          ? BSplineInterpolation.solve(degree, control)
          : control;
      GeodesicBSplineFunction bSplineFunction = //
          GeodesicBSplineFunction.of(RnGeodesic.INSTANCE, degree, rnctrl);
      refined = Subdivide.of(0, rnctrl.length() - 1, 4 << levels).map(bSplineFunction);
    }
    graphics.setColor(new Color(255, 128, 128, 255));
    for (Tensor point : control) {
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(point));
      Path2D path2d = geometricLayer.toPath2D(geodesicDisplay.shape());
      path2d.closePath();
      graphics.setColor(new Color(255, 128, 128, 64));
      graphics.fill(path2d);
      graphics.setColor(new Color(255, 128, 128, 255));
      graphics.draw(path2d);
      geometricLayer.popMatrix();
    }
    renderCurve(refined, false, geometricLayer, graphics);
  }

  public static void main(String[] args) {
    BSplineBasisDemo curveSubdivisionDemo = new BSplineBasisDemo();
    curveSubdivisionDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 800);
    curveSubdivisionDemo.timerFrame.jFrame.setVisible(true);
  }
}
