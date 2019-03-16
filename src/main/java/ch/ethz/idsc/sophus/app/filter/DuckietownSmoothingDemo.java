// code by jph & ob
package ch.ethz.idsc.sophus.app.filter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.List;

import javax.swing.JSlider;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
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
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.red.Norm;

public class DuckietownSmoothingDemo extends DatasetKernelDemo {
  private static final List<Integer> DEGREES = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
  // ---
  private final SpinnerLabel<Integer> spinnerDegree = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private final JSlider jSlider = new JSlider(0, 1000, 500);
  static final List<String> LIST = Arrays.asList( //
      "duckie20180713-175124.csv", //
      "duckie20180713-175420.csv", //
      "duckie20180713-175601.csv", //
      "duckie20180901-152902.csv");

  public DuckietownSmoothingDemo() {
    updateState();
    spinnerDegree.setList(DEGREES);
    spinnerDegree.setValue(2);
    spinnerDegree.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "degree");
    // ---
    spinnerRefine.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
    spinnerRefine.setValue(2);
    spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    //
    jSlider.setPreferredSize(new Dimension(500, 28));
    timerFrame.jToolBar.add(jSlider);
  }

  @Override
  protected void updateState() {
    // TODO OB: Liste anpassen => neue DatasetKernelDemo fuer duckietown?
    Tensor tensor = ResourceData.of("/autolab/localization/2018/" + LIST.get(spinnerLabelString.getIndex()));
    tensor = tensor.map(xya -> xya);
    _control = Tensor.of(tensor.stream() //
        .limit(spinnerLabelLimit.getValue()) //
        .map(row -> row.extract(2, 5)));
  }

  @Override // from RenderInterface
  protected Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    final int degree = spinnerDegree.getValue();
    final int levels = spinnerRefine.getValue();
    final Tensor control = control();
    Tensor effective = control;
    Tensor diffs = Differences.of(control);
    Tensor knots = Accumulate.of(Join.of(Tensors.vector(0), Tensor.of(diffs.stream().map(xya -> xya.extract(0, 2)).map(Norm._2::ofVector))));
    final Scalar upper = (Scalar) Last.of(knots);
    final Scalar parameter = RationalScalar.of(jSlider.getValue(), jSlider.getMaximum()).multiply(upper);
    // ---
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    GeodesicBSplineFunction scalarTensorFunction = //
        GeodesicBSplineFunction.of(geodesicDisplay.geodesicInterface(), degree, knots, effective);
    if (jToggleSymi.isSelected()) {
      GeodesicDeBoor geodesicDeBoor = scalarTensorFunction.deBoor(parameter);
      SymLinkImage symLinkImage = SymLinkImages.deboor(geodesicDeBoor, geodesicDeBoor.degree() + 1, parameter);
      graphics.drawImage(symLinkImage.bufferedImage(), 0, 0, null);
    }
    GraphicsUtil.setQualityHigh(graphics);
    Tensor refined = Subdivide.of(RealScalar.ZERO, upper, Math.max(1, control.length() * (1 << levels))).map(scalarTensorFunction);
    {
      Tensor selected = scalarTensorFunction.apply(parameter);
      geometricLayer.pushMatrix(geodesicDisplay.matrixLift(selected));
      Path2D path2d = geometricLayer.toPath2D(geodesicDisplay.shape().multiply(RealScalar.of(.01)));
      graphics.setColor(Color.DARK_GRAY);
      graphics.fill(path2d);
      geometricLayer.popMatrix();
    }
    return refined;
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new DuckietownSmoothingDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 800);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
