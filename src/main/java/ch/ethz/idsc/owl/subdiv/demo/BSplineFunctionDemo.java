// code by jph
package ch.ethz.idsc.owl.subdiv.demo;

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
import ch.ethz.idsc.owl.math.group.RnGeodesic;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.owl.subdiv.curve.GeodesicBSplineFunction;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.mat.Inverse;

/* package */ class BSplineFunctionDemo extends ControlPointsDemo {
  private static final List<Integer> DEGREES = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
  // ---
  private final SpinnerLabel<Integer> spinnerDegree = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private final JToggleButton jToggleCtrl = new JToggleButton("ctrl");
  private final JToggleButton jToggleComb = new JToggleButton("comb");

  BSplineFunctionDemo() {
    timerFrame.jToolBar.add(jButton);
    jToggleButton.setSelected(true);
    jToggleCtrl.setSelected(true);
    timerFrame.jToolBar.add(jToggleCtrl);
    // ---
    jToggleComb.setSelected(false);
    timerFrame.jToolBar.add(jToggleComb);
    // ---
    spinnerDegree.setList(DEGREES);
    spinnerDegree.setValue(1);
    spinnerDegree.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "degree");
    // ---
    spinnerRefine.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));
    spinnerRefine.setValue(4);
    spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    // ---
    setControl(Tensors.fromString("{{0, 0}, {1, 0}}"));
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GraphicsUtil.setQualityHigh(graphics);
    int degree = spinnerDegree.getValue();
    int levels = spinnerRefine.getValue();
    Tensor control = controlR2();
    {
      graphics.setStroke(new BasicStroke(1.25f));
      Tensor matrix = geometricLayer.getMatrix();
      geometricLayer.pushMatrix(Inverse.of(matrix));
      {
        ColorDataIndexed cyclic = ColorDataLists._097.cyclic().deriveWithAlpha(192);
        for (int length = 2; length <= 8; ++length) {
          Tensor string = Tensors.fromString("{{100, 0, 0}, {0, -100, 0}, {0, 0, 1}}");
          string.set(RealScalar.of(110 * length), 1, 2);
          geometricLayer.pushMatrix(string);
          for (int k_th = 0; k_th < length; ++k_th) {
            GeodesicBSplineFunction bSplineFunction = GeodesicBSplineFunction.of(RnGeodesic.INSTANCE, degree, UnitVector.of(length, k_th));
            Tensor domain = Subdivide.of(0, length - 1, 100);
            Tensor values = domain.map(bSplineFunction);
            Tensor tensor = Transpose.of(Tensors.of(domain, values));
            graphics.setColor(cyclic.getColor(k_th));
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
    GeodesicBSplineFunction bSplineFunction = GeodesicBSplineFunction.of(RnGeodesic.INSTANCE, degree, control);
    final Tensor refined = Subdivide.of(0, control.length() - 1, 4 << levels).map(bSplineFunction);
    graphics.setColor(new Color(255, 128, 128, 255));
    for (Tensor point : control) {
      geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(point.copy().append(RealScalar.ZERO)));
      Path2D path2d = geometricLayer.toPath2D(CIRCLE_HI);
      path2d.closePath();
      graphics.setColor(new Color(255, 128, 128, 64));
      graphics.fill(path2d);
      graphics.setColor(new Color(255, 128, 128, 255));
      graphics.draw(path2d);
      geometricLayer.popMatrix();
    }
    new CurveRender(refined, false, jToggleComb.isSelected()).render(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    BSplineFunctionDemo curveSubdivisionDemo = new BSplineFunctionDemo();
    curveSubdivisionDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 800);
    curveSubdivisionDemo.timerFrame.jFrame.setVisible(true);
  }
}
