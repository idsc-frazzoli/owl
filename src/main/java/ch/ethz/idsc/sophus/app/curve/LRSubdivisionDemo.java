// code by jph
package ch.ethz.idsc.sophus.app.curve;

import javax.swing.JToggleButton;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.util.plot.ListPlot;
import ch.ethz.idsc.sophus.util.plot.VisualRow;
import ch.ethz.idsc.sophus.util.plot.VisualSet;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import org.jfree.chart.JFreeChart;

/** compare different levels of smoothing in the Lane-iesenfeld algorithm {@link ch.ethz.idsc.sophus.crv.subdiv.LaneRiesenfeldCurveSubdivision} */
public class LRSubdivisionDemo extends ControlPointsDemo {
  private static final int WIDTH = 640;
  private static final int HEIGHT = 360;
  private static final ColorDataIndexed COLORS = ColorDataLists._097.cyclic();
  // ---
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private final JToggleButton jToggleCurvature = new JToggleButton("crvt");
  private final List<CurveSubdivisionSchemes> schemes = Arrays.asList( //
      CurveSubdivisionSchemes.LR1, //
      CurveSubdivisionSchemes.LR2, //
      CurveSubdivisionSchemes.LR3, //
      CurveSubdivisionSchemes.LR4, //
      CurveSubdivisionSchemes.LR5);
  private final List<PathRender> renders = new ArrayList<>();

  public LRSubdivisionDemo() {
    this(GeodesicDisplays.ALL);
  }

  public LRSubdivisionDemo(List<GeodesicDisplay> list) {
    super(true, list);
    // ---
    jToggleCurvature.setSelected(true);
    jToggleCurvature.setToolTipText("curvature plot");
    timerFrame.jToolBar.add(jToggleCurvature);
    // ---
    Tensor control = Tensors.fromString("{{0, 0, 0}, {1, 0, 0}, {2, 0, 0}, {3, 1, 0}, {4, 1, 0}, {5, 0, 0}, {6, 0, 0}, {7, 0, 0}}").multiply(RealScalar.of(2));
    setControlPointsSe2(control);
    timerFrame.jToolBar.addSeparator();
    // ---
    spinnerRefine.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
    spinnerRefine.setValue(6);
    spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    // ---
    for (int i = 0; i < schemes.size(); i++)
      renders.add(new PathRender(COLORS.getColor(i)));
    // ---
    timerFrame.configCoordinateOffset(100, 600);
  }

  @Override // from RenderInterface
  public synchronized final void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    VisualSet visualSet = new VisualSet();
    visualSet.setPlotLabel("Curvature");
    visualSet.setAxesLabelX("length");
    visualSet.setAxesLabelY("curvature");
    // TODO add additional plots for derivatives
    for (int i = 0; i < schemes.size(); i++) {
      Tensor refined = curve(geometricLayer, graphics, i);
      if (jToggleCurvature.isSelected() && 1 < refined.length()) {
        Tensor tensor = Tensor.of(refined.stream().map(geodesicDisplay::toPoint));
        CurveVisualSet curveVisualSet = new CurveVisualSet(tensor);
        curveVisualSet.addCurvature();
        Tensor curvature = curveVisualSet.visualSet().getVisualRow(0).points();
        VisualRow visualRow = visualSet.add(curvature);
        visualRow.setLabel(schemes.get(i).name());
        visualRow.setColor(COLORS.getColor(i));
      }
    }
    // ---
    Dimension dimension = timerFrame.geometricComponent.jComponent.getSize();
    if (jToggleCurvature.isSelected()) {
      JFreeChart jFreeChart = ListPlot.of(visualSet);
      jFreeChart.draw(graphics, new Rectangle2D.Double(dimension.width - WIDTH, 0, WIDTH, HEIGHT));
    }
  }

  public Tensor curve(GeometricLayer geometricLayer, Graphics2D graphics, final int index) {
    CurveSubdivisionSchemes scheme = schemes.get(index);
    PathRender pathRender = renders.get(index);
    GraphicsUtil.setQualityHigh(graphics);
    // ---
    Tensor control = getGeodesicControlPoints();
    int levels = spinnerRefine.getValue();
    Tensor refined;
    renderControlPoints(geometricLayer, graphics);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    {
      TensorUnaryOperator tensorUnaryOperator = StaticHelper.create(scheme.of(geodesicDisplay.geodesicInterface()), false);
      refined = control;
      for (int level = 0; level < levels; ++level) {
        Tensor prev = refined;
        refined = tensorUnaryOperator.apply(refined);
        if (CurveSubdivisionHelper.isDual(scheme) && //
            level % 2 == 1 && //
            1 < control.length()) {
          refined = Join.of( //
              Tensors.of(geodesicDisplay.geodesicInterface().split(control.get(0), prev.get(0), RationalScalar.HALF)), //
              refined, //
              Tensors.of(geodesicDisplay.geodesicInterface().split(Last.of(prev), Last.of(control), RationalScalar.HALF)) //
          );
        }
      }
    }
    // ---
    Tensor render = Tensor.of(refined.stream().map(geodesicDisplay::toPoint));
    pathRender.setCurve(render, false);
    pathRender.render(geometricLayer, graphics);
    return refined;
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new LRSubdivisionDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1200, 800);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
