// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.Arrays;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gui.ren.PathRender;
import ch.ethz.idsc.sophus.gui.win.ControlPointsDemo;
import ch.ethz.idsc.sophus.itp.BarycentricRationalInterpolation;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Sort;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.api.ScalarTensorFunction;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.itp.InterpolatingPolynomial;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.sca.N;

/* package */ class R1BarycentricDegreeDemo extends ControlPointsDemo {
  private static final Stroke STROKE = //
      new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);
  private final JToggleButton jToggleButton = new JToggleButton("lagrange");
  private final SpinnerLabel<Integer> spinnerDegree = new SpinnerLabel<>();

  public R1BarycentricDegreeDemo() {
    super(true, GeodesicDisplays.R2_ONLY);
    {
      spinnerDegree.setList(Arrays.asList(0, 1, 2, 3, 4));
      spinnerDegree.setValue(1);
      spinnerDegree.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "magnify");
    }
    {
      jToggleButton.setSelected(true);
      timerFrame.jToolBar.add(jToggleButton);
    }
    // ---
    setControlPointsSe2(Tensors.fromString("{{0, 0, 0}, {1, 1, 0}, {2, 2, 0}}"));
    // ---
    timerFrame.geometricComponent.addRenderInterfaceBackground(AxesRender.INSTANCE);
  }

  private static final Scalar MARGIN = RealScalar.of(2);

  static Tensor domain(Tensor support) {
    return Subdivide.of( //
        support.stream().reduce(Min::of).get().add(MARGIN.negate()), //
        support.stream().reduce(Max::of).get().add(MARGIN), 128).map(N.DOUBLE);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    renderControlPoints(geometricLayer, graphics);
    // ---
    Tensor control = Sort.of(getGeodesicControlPoints());
    if (1 < control.length()) {
      Tensor support = control.get(Tensor.ALL, 0);
      Tensor funceva = control.get(Tensor.ALL, 1);
      // ---
      Tensor domain = domain(support);
      if (jToggleButton.isSelected()) {
        ScalarTensorFunction geodesicNeville = InterpolatingPolynomial.of(manifoldDisplay().geodesicInterface(), support).scalarTensorFunction(funceva);
        Tensor basis = domain.map(geodesicNeville);
        {
          Tensor curve = Transpose.of(Tensors.of(domain, basis));
          new PathRender(new Color(255, 0, 0, 128), STROKE).setCurve(curve, false).render(geometricLayer, graphics);
        }
      }
      // ---
      ScalarTensorFunction scalarTensorFunction = //
          BarycentricRationalInterpolation.of(support, spinnerDegree.getValue());
      Tensor basis = domain.map(scalarTensorFunction);
      {
        Tensor curve = Transpose.of(Tensors.of(domain, basis.dot(funceva)));
        new PathRender(Color.BLUE, 1.25f).setCurve(curve, false).render(geometricLayer, graphics);
      }
      ColorDataIndexed colorDataIndexed = ColorDataLists._097.cyclic();
      for (int index = 0; index < funceva.length(); ++index) {
        Color color = colorDataIndexed.getColor(index);
        Tensor curve = Transpose.of(Tensors.of(domain, basis.get(Tensor.ALL, index)));
        new PathRender(color, 1f).setCurve(curve, false).render(geometricLayer, graphics);
      }
    }
  }

  public static void main(String[] args) {
    new R1BarycentricDegreeDemo().setVisible(1000, 800);
  }
}
