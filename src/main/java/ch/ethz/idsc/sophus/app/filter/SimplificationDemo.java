// code by jph
package ch.ethz.idsc.sophus.app.filter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Arrays;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplayDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.GokartPoseData;
import ch.ethz.idsc.sophus.app.api.GokartPoseDataV2;
import ch.ethz.idsc.sophus.app.api.GokartPoseDatas;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.crv.GeodesicSimplification;
import ch.ethz.idsc.sophus.crv.subdiv.BSpline1CurveSubdivision;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.sca.Power;

/* package */ class SimplificationDemo extends GeodesicDisplayDemo {
  private static final Color COLOR_CURVE = new Color(255, 128, 128, 255);
  private static final Color COLOR_SHAPE = new Color(160, 160, 160, 192);
  // ---
  private final PathRender pathRenderCurve = new PathRender(COLOR_CURVE);
  private final PathRender pathRenderShape = new PathRender(COLOR_SHAPE, 2f);
  // ---
  private final GokartPoseData gokartPoseData;
  protected final SpinnerLabel<String> spinnerLabelString = new SpinnerLabel<>();
  protected final SpinnerLabel<Integer> spinnerLabelLimit = new SpinnerLabel<>();
  protected final SpinnerLabel<Integer> spinnerLabelLevel = new SpinnerLabel<>();
  protected Tensor _control = Tensors.empty();

  public SimplificationDemo(GokartPoseData gokartPoseData) {
    super(GeodesicDisplays.SE2_R2);
    this.gokartPoseData = gokartPoseData;
    timerFrame.geometricComponent.setModel2Pixel(GokartPoseDatas.HANGAR_MODEL2PIXEL);
    {
      spinnerLabelString.setList(gokartPoseData.list());
      spinnerLabelString.addSpinnerListener(type -> updateState());
      spinnerLabelString.setIndex(0);
      spinnerLabelString.addToComponentReduced(timerFrame.jToolBar, new Dimension(200, 28), "data");
    }
    {
      spinnerLabelLimit.setList(Arrays.asList(500, 1000, 1500, 2000, 3000, 5000));
      spinnerLabelLimit.setIndex(2);
      spinnerLabelLimit.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "limit");
      spinnerLabelLimit.addSpinnerListener(type -> updateState());
    }
    {
      spinnerLabelLevel.setList(Arrays.asList(0, 1, 2, 3, 4, 5));
      spinnerLabelLevel.setIndex(2);
      spinnerLabelLevel.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "eps power");
      spinnerLabelLevel.addSpinnerListener(type -> updateState());
    }
    updateState();
  }

  protected void updateState() {
    int limit = spinnerLabelLimit.getValue();
    String name = spinnerLabelString.getValue();
    _control = gokartPoseData.getPose(name, limit);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GraphicsUtil.setQualityHigh(graphics);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    {
      final Tensor shape = geodesicDisplay.shape().multiply(RealScalar.of(0.2));
      pathRenderCurve.setCurve(_control, false).render(geometricLayer, graphics);
      if (_control.length() < 1000)
        for (Tensor point : _control) {
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
    Scalar epsilon = Power.of(RationalScalar.HALF, spinnerLabelLevel.getValue());
    TensorUnaryOperator tensorUnaryOperator = GeodesicSimplification.of( //
        geodesicDisplay.lieGroup(), geodesicDisplay.lieExponential(), geodesicDisplay.dimensions(), epsilon);
    Tensor xy = Tensor.of(_control.stream().map(geodesicDisplay::project));
    Tensor simplified = tensorUnaryOperator.apply(xy);
    Tensor refined = Nest.of(new BSpline1CurveSubdivision(geodesicDisplay.geodesicInterface())::string, simplified, 4);
    pathRenderShape.setCurve(refined, false).render(geometricLayer, graphics);
    {
      final Tensor shape = geodesicDisplay.shape();
      for (Tensor point : simplified) {
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
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new SimplificationDemo(GokartPoseDataV2.INSTANCE);
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 800);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
