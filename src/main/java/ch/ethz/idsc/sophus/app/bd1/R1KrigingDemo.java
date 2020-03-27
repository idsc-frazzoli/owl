// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.stream.Collectors;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.GraphicsUtil;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.sophus.itp.Kriging;
import ch.ethz.idsc.sophus.itp.MetricKriging;
import ch.ethz.idsc.sophus.itp.PowerVariogram;
import ch.ethz.idsc.sophus.itp.ProjectedKriging;
import ch.ethz.idsc.sophus.lie.rn.RnManifold;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.alg.Sort;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.sca.N;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/* package */ class R1KrigingDemo extends ControlPointsDemo {
  private static final Stroke STROKE = //
      new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);
  // ---
  private final JToggleButton jToggleButton = new JToggleButton("biinv");
  private final SpinnerLabel<Scalar> spinnerBeta = new SpinnerLabel<>();
  private final SpinnerLabel<Scalar> spinnerCvar = new SpinnerLabel<>();

  public R1KrigingDemo() {
    super(true, GeodesicDisplays.R2_ONLY);
    timerFrame.jToolBar.add(jToggleButton);
    {
      spinnerCvar.setList(Tensors.fromString("{0, 0.01, 0.1, 0.5, 1}").stream().map(Scalar.class::cast).collect(Collectors.toList()));
      spinnerCvar.setIndex(0);
      spinnerCvar.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "error");
    }
    {
      spinnerBeta.setList(Tensors.fromString("{1, 9/8, 5/4, 3/2, 1.75, 1.99}").stream().map(Scalar.class::cast).collect(Collectors.toList()));
      spinnerBeta.setIndex(0);
      spinnerBeta.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "beta");
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
    GraphicsUtil.setQualityHigh(graphics);
    renderControlPoints(geometricLayer, graphics);
    // ---
    Tensor control = Sort.of(getGeodesicControlPoints());
    if (1 < control.length()) {
      Tensor support = control.get(Tensor.ALL, 0);
      Tensor funceva = control.get(Tensor.ALL, 1);
      // ---
      Tensor domain = domain(support);
      Tensor sequence = support.map(Tensors::of);
      ScalarUnaryOperator variogram = PowerVariogram.fit(sequence, funceva, spinnerBeta.getValue());
      Tensor covariance = DiagonalMatrix.with(ConstantArray.of(spinnerCvar.getValue(), support.length()));
      Kriging kriging = jToggleButton.isSelected() //
          ? ProjectedKriging.regression(RnManifold.INSTANCE, variogram, sequence, funceva, covariance)
          : MetricKriging.regression(RnManifold.INSTANCE, variogram, support.map(Tensors::of), funceva, covariance);
      Tensor result = Tensor.of(domain.stream().map(Tensors::of).map(kriging::estimate));
      new PathRender(Color.BLUE, 1.25f) //
          .setCurve(Transpose.of(Tensors.of(domain, result)), false) //
          .render(geometricLayer, graphics);
      Tensor errors = Tensor.of(domain.stream().map(Tensors::of).map(kriging::variance));
      // ---
      new PathRender(Color.RED, STROKE) //
          .setCurve(Transpose.of(Tensors.of(domain, result.add(errors))), false) //
          .render(geometricLayer, graphics);
      new PathRender(Color.GREEN, STROKE) //
          .setCurve(Transpose.of(Tensors.of(domain, result.subtract(errors))), false) //
          .render(geometricLayer, graphics);
    }
  }

  public static void main(String[] args) {
    new R1KrigingDemo().setVisible(1000, 800);
  }
}
