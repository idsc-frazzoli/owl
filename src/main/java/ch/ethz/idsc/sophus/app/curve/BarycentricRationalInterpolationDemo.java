// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.stream.Collectors;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.PathRender;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.crv.spline.BarycentricRationalInterpolation;
import ch.ethz.idsc.sophus.hs.BiinvariantMean;
import ch.ethz.idsc.sophus.math.win.KnotSpacing;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/* package */ class BarycentricRationalInterpolationDemo extends ControlPointsDemo {
  private final SpinnerLabel<Scalar> spinnerBeta = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerDegree = new SpinnerLabel<>();

  public BarycentricRationalInterpolationDemo() {
    super(true, GeodesicDisplays.ALL);
    {
      spinnerBeta.setList(Tensors.fromString("{0, 1/4, 1/2, 3/4, 1}").stream().map(Scalar.class::cast).collect(Collectors.toList()));
      spinnerBeta.setIndex(0);
      spinnerBeta.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "beta");
    }
    {
      spinnerDegree.setList(Arrays.asList(0, 1, 2, 3, 4));
      spinnerDegree.setValue(1);
      spinnerDegree.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "degree");
    }
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    Tensor control = getGeodesicControlPoints();
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    TensorUnaryOperator tensorUnaryOperator = //
        KnotSpacing.centripetal(geodesicDisplay.parametricDistance(), spinnerBeta.getValue());
    Tensor knots = tensorUnaryOperator.apply(control);
    if (1 < control.length()) {
      Tensor domain = Subdivide.of(knots.get(0), Last.of(knots), 25 * control.length());
      ScalarTensorFunction scalarTensorFunction = //
          BarycentricRationalInterpolation.of(knots, spinnerDegree.getValue());
      BiinvariantMean biinvariantMean = geodesicDisplay.biinvariantMean();
      Tensor points = Tensor.of(domain.map(scalarTensorFunction).stream() //
          .map(weights -> biinvariantMean.mean(control, weights)));
      new PathRender(Color.BLUE) //
          .setCurve(points, false) //
          .render(geometricLayer, graphics);
    }
    // ---
    renderControlPoints(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new BarycentricRationalInterpolationDemo().setVisible(1200, 600);
  }
}
