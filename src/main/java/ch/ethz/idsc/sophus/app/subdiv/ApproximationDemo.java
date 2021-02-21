// code by jph
package ch.ethz.idsc.sophus.app.subdiv;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.ren.GridRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.io.GokartPoseData;
import ch.ethz.idsc.sophus.app.io.GokartPoseDataV2;
import ch.ethz.idsc.sophus.app.io.GokartPoseDatas;
import ch.ethz.idsc.sophus.flt.CenterFilter;
import ch.ethz.idsc.sophus.flt.ga.GeodesicCenter;
import ch.ethz.idsc.sophus.gds.GeodesicDisplayDemo;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gui.ren.PathRender;
import ch.ethz.idsc.sophus.ref.d1.CurveSubdivision;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.sca.Round;
import ch.ethz.idsc.tensor.sca.win.GaussianWindow;

/* package */ class ApproximationDemo extends GeodesicDisplayDemo {
  private static final Color COLOR_CURVE = new Color(255, 128, 128, 255);
  private static final Color COLOR_SHAPE = new Color(160, 160, 160, 192);
  private static final Scalar MARKER_SCALE = RealScalar.of(0.1);
  private static final GridRender GRID_RENDER = new GridRender(Subdivide.of(0, 100, 10));
  private static final List<CurveSubdivisionSchemes> SCHEMES = Arrays.asList( //
      CurveSubdivisionSchemes.BSPLINE1, //
      CurveSubdivisionSchemes.BSPLINE2, //
      CurveSubdivisionSchemes.BSPLINE3, //
      CurveSubdivisionSchemes.BSPLINE4_S2LO, //
      CurveSubdivisionSchemes.FOURPOINT, //
      CurveSubdivisionSchemes.SIXPOINT);

  private static class Container {
    private final ManifoldDisplay geodesicDisplay;
    private final Tensor tracked;
    private final Tensor control;
    private final Tensor refined;

    public Container(ManifoldDisplay geodesicDisplay, Tensor tracked, Tensor control, Tensor refined) {
      this.geodesicDisplay = geodesicDisplay;
      this.tracked = tracked;
      this.control = control;
      this.refined = refined;
    }
  }

  /***************************************************/
  private final PathRender pathRenderCurve = new PathRender(COLOR_CURVE);
  private final PathRender pathRenderShape = new PathRender(COLOR_SHAPE);
  private final GokartPoseData gokartPoseData;
  private final SpinnerLabel<String> spinnerLabelString = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerLabelLimit = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerLabelWidth = new SpinnerLabel<>();
  private final SpinnerLabel<CurveSubdivisionSchemes> spinnerLabelScheme = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerLabelLevel = new SpinnerLabel<>();
  // ---
  private Container _container = null;

  public ApproximationDemo(GokartPoseData gokartPoseData) {
    super(GeodesicDisplays.SE2_R2);
    this.gokartPoseData = gokartPoseData;
    timerFrame.geometricComponent.addRenderInterfaceBackground(GRID_RENDER);
    timerFrame.geometricComponent.setModel2Pixel(GokartPoseDatas.HANGAR_MODEL2PIXEL);
    addSpinnerListener(type -> updateState());
    {
      spinnerLabelString.setList(gokartPoseData.list());
      spinnerLabelString.addSpinnerListener(type -> updateState());
      spinnerLabelString.setIndex(0);
      spinnerLabelString.addToComponentReduced(timerFrame.jToolBar, new Dimension(200, 28), "data");
    }
    {
      spinnerLabelLimit.setList(Arrays.asList(100, 250, 500, 1000, 2000, 5000));
      spinnerLabelLimit.setIndex(3);
      spinnerLabelLimit.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "limit");
      spinnerLabelLimit.addSpinnerListener(type -> updateState());
    }
    timerFrame.jToolBar.addSeparator();
    {
      spinnerLabelWidth.setList(Arrays.asList(0, 2, 4, 6, 8, 10, 12, 14, 16, 18, 20));
      spinnerLabelWidth.setIndex(6);
      spinnerLabelWidth.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "width");
      spinnerLabelWidth.addSpinnerListener(type -> updateState());
    }
    {
      spinnerLabelScheme.setList(SCHEMES);
      spinnerLabelScheme.setValue(CurveSubdivisionSchemes.BSPLINE1);
      spinnerLabelScheme.addToComponentReduced(timerFrame.jToolBar, new Dimension(160, 28), "scheme");
      spinnerLabelScheme.addSpinnerListener(type -> updateState());
    }
    {
      spinnerLabelLevel.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6));
      spinnerLabelLevel.setIndex(5);
      spinnerLabelLevel.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "level");
      spinnerLabelLevel.addSpinnerListener(type -> updateState());
    }
    updateState();
  }

  private void updateState() {
    Tensor rawdata = gokartPoseData.getPose(spinnerLabelString.getValue(), spinnerLabelLimit.getValue());
    ManifoldDisplay geodesicDisplay = manifoldDisplay();
    TensorUnaryOperator tensorUnaryOperator = GeodesicCenter.of(geodesicDisplay.geodesicInterface(), GaussianWindow.FUNCTION);
    TensorUnaryOperator centerFilter = CenterFilter.of(tensorUnaryOperator, spinnerLabelWidth.getValue());
    Tensor tracked = centerFilter.apply(rawdata);
    int level = spinnerLabelLevel.getIndex();
    int steps = 1 << level;
    System.out.println(DoubleScalar.of(steps).divide(gokartPoseData.getSampleRate()).map(Round._3));
    Tensor control = Tensor.of(IntStream.range(0, tracked.length() / steps) //
        .map(i -> i * steps) //
        .mapToObj(tracked::get));
    CurveSubdivision curveSubdivision = //
        spinnerLabelScheme.getValue().of(geodesicDisplay);
    Tensor refined = Nest.of(curveSubdivision::string, control, level);
    _container = new Container(geodesicDisplay, tracked, control, refined);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Container container = _container;
    if (Objects.isNull(container))
      return;
    RenderQuality.setQuality(graphics);
    ManifoldDisplay geodesicDisplay = container.geodesicDisplay;
    {
      Tensor tracked = container.tracked;
      pathRenderCurve.setCurve(tracked, false).render(geometricLayer, graphics);
    }
    {
      Tensor control = container.control;
      int level = spinnerLabelLevel.getIndex();
      final Tensor shape = geodesicDisplay.shape().multiply(MARKER_SCALE.multiply(RealScalar.of(1 + level)));
      for (Tensor point : control) {
        geometricLayer.pushMatrix(geodesicDisplay.matrixLift(point));
        Path2D path2d = geometricLayer.toPath2D(shape);
        path2d.closePath();
        graphics.setColor(new Color(255, 128, 128, 64));
        graphics.fill(path2d);
        graphics.setColor(COLOR_CURVE);
        graphics.draw(path2d);
        geometricLayer.popMatrix();
      }
    }
    {
      Tensor refined = container.refined;
      final Tensor shape = geodesicDisplay.shape().multiply(MARKER_SCALE.multiply(RealScalar.of(0.5)));
      pathRenderShape.setCurve(refined, false).render(geometricLayer, graphics);
      for (Tensor point : refined) {
        geometricLayer.pushMatrix(geodesicDisplay.matrixLift(point));
        Path2D path2d = geometricLayer.toPath2D(shape);
        path2d.closePath();
        graphics.setColor(COLOR_SHAPE);
        graphics.fill(path2d);
        graphics.setColor(Color.BLACK);
        graphics.draw(path2d);
        geometricLayer.popMatrix();
      }
    }
    RenderQuality.setDefault(graphics);
  }

  public static void main(String[] args) {
    new ApproximationDemo(GokartPoseDataV2.RACING_DAY).setVisible(1000, 800);
  }
}
