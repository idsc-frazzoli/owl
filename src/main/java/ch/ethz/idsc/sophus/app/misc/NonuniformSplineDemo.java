// code by jph
package ch.ethz.idsc.sophus.app.misc;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.crv.spline.GeodesicBSplineFunction;
import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.alg.Ordering;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ class NonuniformSplineDemo extends ControlPointsDemo {
  private static final List<Integer> DEGREES = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
  // ---
  private final SpinnerLabel<Integer> spinnerDegree = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private final JToggleButton jToggleItrp = new JToggleButton("interp");

  NonuniformSplineDemo() {
    super(true, GeodesicDisplays.R2_ONLY);
    // ---
    timerFrame.jToolBar.add(jToggleItrp);
    jToggleItrp.setEnabled(false);
    // ---
    spinnerDegree.setList(DEGREES);
    spinnerDegree.setValue(1);
    spinnerDegree.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "degree");
    // ---
    spinnerRefine.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));
    spinnerRefine.setValue(4);
    spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    // ---
    setControlPointsSe2(Tensors.fromString("{{0, 0, 0}, {1, 0, 0}}"));
    // ---
    timerFrame.geometricComponent.addRenderInterfaceBackground(AxesRender.INSTANCE);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GraphicsUtil.setQualityHigh(graphics);
    int degree = spinnerDegree.getValue();
    int levels = spinnerRefine.getValue();
    Tensor control = getGeodesicControlPoints();
    // ---
    Tensor _effective = control;
    // ---
    Integer[] array = Ordering.INCREASING.of(_effective.get(Tensor.ALL, 0));
    Tensor x = Tensor.of(Stream.of(array).map(i -> _effective.get(i, 0)));
    Tensor y = Tensor.of(Stream.of(array).map(i -> _effective.get(i, 1)));
    ScalarTensorFunction scalarTensorFunction = //
        GeodesicBSplineFunction.of(RnGeodesic.INSTANCE, degree, x, y);
    Clip clip = Clips.interval(x.Get(0), Last.of(x));
    Tensor domain = Subdivide.increasing(clip, 4 << levels);
    Tensor values = domain.map(scalarTensorFunction);
    renderControlPoints(geometricLayer, graphics);
    Curvature2DRender.of(Transpose.of(Tensors.of(domain, values)), false, geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new NonuniformSplineDemo().setVisible(1000, 800);
  }
}
