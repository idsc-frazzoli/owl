// code by gjoel
package ch.ethz.idsc.sophus.app.subdiv;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import javax.swing.JToggleButton;

import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gui.ren.CurveVisualSet;
import ch.ethz.idsc.sophus.gui.ren.PathRender;
import ch.ethz.idsc.sophus.gui.win.ControlPointsDemo;
import ch.ethz.idsc.sophus.math.Geodesic;
import ch.ethz.idsc.sophus.math.MinMax;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.fig.ListPlot;
import ch.ethz.idsc.tensor.fig.VisualRow;
import ch.ethz.idsc.tensor.fig.VisualSet;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.red.Quantile;

/** compare different levels of smoothing in the LaneRiesenfeldCurveSubdivision */
/* package */ class LaneRiesenfeldComparisonDemo extends ControlPointsDemo {
  private static final ColorDataIndexed COLORS = ColorDataLists._097.cyclic();
  private static final List<CurveSubdivisionSchemes> CURVE_SUBDIVISION_SCHEMES = //
      CurveSubdivisionHelper.LANE_RIESENFELD;
  // ---
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private final JToggleButton jToggleCurvature = new JToggleButton("crvt");
  private final List<PathRender> pathRenders = new ArrayList<>();

  public LaneRiesenfeldComparisonDemo() {
    this(GeodesicDisplays.WITHOUT_Sn_SO3);
  }

  public LaneRiesenfeldComparisonDemo(List<ManifoldDisplay> list) {
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
    spinnerRefine.setValue(4);
    spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    // ---
    for (int i = 0; i < CURVE_SUBDIVISION_SCHEMES.size(); ++i)
      pathRenders.add(new PathRender(COLORS.getColor(i)));
    // ---
    timerFrame.geometricComponent.setOffset(100, 600);
  }

  @Override // from RenderInterface
  public synchronized final void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    ManifoldDisplay geodesicDisplay = manifoldDisplay();
    VisualSet visualSet1 = new VisualSet();
    visualSet1.setPlotLabel("Curvature");
    visualSet1.setAxesLabelX("length");
    visualSet1.setAxesLabelY("curvature");
    // ---
    VisualSet visualSet2 = new VisualSet();
    visualSet2.setPlotLabel("Curvature d/ds");
    visualSet2.setAxesLabelX("length");
    visualSet2.setAxesLabelY("curvature d/ds");
    for (int i = 0; i < CURVE_SUBDIVISION_SCHEMES.size(); ++i) {
      Tensor refined = curve(geometricLayer, graphics, i);
      if (jToggleCurvature.isSelected() && 1 < refined.length()) {
        Tensor tensor = Tensor.of(refined.stream().map(geodesicDisplay::toPoint));
        VisualSet visualSet = new VisualSet(ColorDataLists._097.cyclic().deriveWithAlpha(192));
        CurveVisualSet curveVisualSet = new CurveVisualSet(tensor);
        VisualRow visualRow = curveVisualSet.addCurvature(visualSet);
        Tensor curvature = visualRow.points();
        // ---
        Tensor curvatureRy = Tensor.of(Differences.of(curvature).stream().map(t -> t.Get(1).divide(t.Get(0))));
        Tensor curvatureRx = Tensor.of(IntStream.range(1, curvature.length()).mapToObj(j -> {
          Tensor domain = curvature.get(Tensor.ALL, 0);
          return Mean.of(domain.extract(j - 1, j + 1));
        }));
        // ---
        VisualRow visualRow1 = visualSet1.add(curvature);
        visualRow1.setLabel(CURVE_SUBDIVISION_SCHEMES.get(i).name());
        visualRow1.setColor(COLORS.getColor(i));
        // ---
        VisualRow visualRow2 = visualSet2.add(curvatureRx, curvatureRy);
        visualRow2.setLabel(CURVE_SUBDIVISION_SCHEMES.get(i).name());
        visualRow2.setColor(COLORS.getColor(i));
      }
    }
    // ---
    Dimension dimension = timerFrame.geometricComponent.jComponent.getSize();
    if (jToggleCurvature.isSelected()) {
      JFreeChart jFreeChart1 = ListPlot.of(visualSet1);
      jFreeChart1.draw(graphics, new Rectangle2D.Double(dimension.width * .5, 0, dimension.width * .5, dimension.height * .5));
      // ---
      JFreeChart jFreeChart2 = ListPlot.of(visualSet2);
      if (!visualSet2.visualRows().isEmpty()) {
        Tensor tensorMin = Tensor.of(visualSet2.visualRows().stream().map(VisualRow::points).map(points -> points.get(Tensor.ALL, 1)) //
            .map(MinMax::of).map(MinMax::min));
        double min = Quantile.of(tensorMin).apply(RationalScalar.of(1, CURVE_SUBDIVISION_SCHEMES.size() - 1)).number().doubleValue();
        Tensor tensorMax = Tensor.of(visualSet2.visualRows().stream().map(VisualRow::points).map(points -> points.get(Tensor.ALL, 1)) //
            .map(MinMax::of).map(MinMax::max));
        double max = Quantile.of(tensorMax) //
            .apply(RationalScalar.of(CURVE_SUBDIVISION_SCHEMES.size() - 1, CurveSubdivisionHelper.LANE_RIESENFELD.size() - 1)).number().doubleValue();
        if (min != max)
          jFreeChart2.getXYPlot().getRangeAxis().setRange(1.1 * min, 1.1 * max);
      }
      jFreeChart2.draw(graphics, new Rectangle2D.Double(dimension.width * .5, dimension.height * .5, dimension.width * .5, dimension.height * .5));
    }
    RenderQuality.setDefault(graphics);
  }

  public Tensor curve(GeometricLayer geometricLayer, Graphics2D graphics, int index) {
    CurveSubdivisionSchemes scheme = CURVE_SUBDIVISION_SCHEMES.get(index);
    PathRender pathRender = pathRenders.get(index);
    // ---
    Tensor control = getGeodesicControlPoints();
    int levels = spinnerRefine.getValue();
    renderControlPoints(geometricLayer, graphics);
    ManifoldDisplay geodesicDisplay = manifoldDisplay();
    Geodesic geodesicInterface = geodesicDisplay.geodesicInterface();
    Tensor refined = StaticHelper.refine(control, levels, scheme.of(geodesicDisplay), //
        CurveSubdivisionHelper.isDual(scheme), false, geodesicInterface);
    // ---
    Tensor render = Tensor.of(refined.stream().map(geodesicDisplay::toPoint));
    pathRender.setCurve(render, false);
    pathRender.render(geometricLayer, graphics);
    return refined;
  }

  public static void main(String[] args) {
    new LaneRiesenfeldComparisonDemo().setVisible(1200, 800);
  }
}
