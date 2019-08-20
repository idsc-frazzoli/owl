// code by gjoel
package ch.ethz.idsc.sophus.app.curve;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import javax.swing.JToggleButton;

import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.MinMax;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.crv.subdiv.LaneRiesenfeldCurveSubdivision;
import ch.ethz.idsc.sophus.util.plot.ListPlot;
import ch.ethz.idsc.sophus.util.plot.VisualRow;
import ch.ethz.idsc.sophus.util.plot.VisualSet;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.red.Quantile;

/** compare different levels of smoothing in the Lane-Riesenfeld algorithm
 * // * {@link LaneRiesenfeldCurveSubdivision} */
public class LRSubdivisionDemo extends ControlPointsDemo {
  private static final ColorDataIndexed COLORS = ColorDataLists._097.cyclic();
  // ---
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private final JToggleButton jToggleCurvature = new JToggleButton("crvt");
  private final List<CurveSubdivisionSchemes> schemes = Arrays.asList( //
      CurveSubdivisionSchemes.LR1, //
      CurveSubdivisionSchemes.LR2, //
      CurveSubdivisionSchemes.LR3, //
      CurveSubdivisionSchemes.LR4, //
      CurveSubdivisionSchemes.LR5 //
  );
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
    spinnerRefine.setValue(5);
    spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "refinement");
    // ---
    for (int i = 0; i < schemes.size(); i++)
      renders.add(new PathRender(COLORS.getColor(i)));
    // ---
    timerFrame.configCoordinateOffset(100, 600);
  }

  @Override // from RenderInterface
  public synchronized final void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GraphicsUtil.setQualityHigh(graphics);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    VisualSet visualSet1 = new VisualSet();
    visualSet1.setPlotLabel("Curvature");
    visualSet1.setAxesLabelX("length");
    visualSet1.setAxesLabelY("curvature");
    // ---
    VisualSet visualSet2 = new VisualSet();
    visualSet2.setPlotLabel("Curvature d/ds");
    visualSet2.setAxesLabelX("length");
    visualSet2.setAxesLabelY("curvature d/ds");
    for (int i = 0; i < schemes.size(); i++) {
      Tensor refined = curve(geometricLayer, graphics, i);
      if (jToggleCurvature.isSelected() && 1 < refined.length()) {
        Tensor tensor = Tensor.of(refined.stream().map(geodesicDisplay::toPoint));
        CurveVisualSet curveVisualSet = new CurveVisualSet(tensor);
        curveVisualSet.addCurvature();
        Tensor curvature = curveVisualSet.visualSet().getVisualRow(0).points();
        // ---
        Tensor curvatureRy = Tensor.of(Differences.of(curvature).stream().map(t -> t.Get(1).divide(t.Get(0))));
        Tensor curvatureRx = Tensor.of(IntStream.range(1, curvature.length()).mapToObj(j -> {
          Tensor domain = curvature.get(Tensor.ALL, 0);
          return Mean.of(domain.extract(j - 1, j + 1));
        }));
        // ---
        VisualRow visualRow1 = visualSet1.add(curvature);
        visualRow1.setLabel(schemes.get(i).name());
        visualRow1.setColor(COLORS.getColor(i));
        // ---
        VisualRow visualRow2 = visualSet2.add(curvatureRx, curvatureRy);
        visualRow2.setLabel(schemes.get(i).name());
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
        double min = Quantile.of(Tensor.of(visualSet2.visualRows().stream().map(VisualRow::points).map(points -> points.get(Tensor.ALL, 1)) //
            .map(MinMax::of).map(MinMax::min)), RationalScalar.of(1, schemes.size() - 1)).Get().number().doubleValue();
        double max = Quantile.of(Tensor.of(visualSet2.visualRows().stream().map(VisualRow::points).map(points -> points.get(Tensor.ALL, 1)) //
            .map(MinMax::of).map(MinMax::max)), RationalScalar.of(schemes.size() - 1, schemes.size() - 1)).Get().number().doubleValue();
        if (min != max)
          jFreeChart2.getXYPlot().getRangeAxis().setRange(1.1 * min, 1.1 * max);
      }
      jFreeChart2.draw(graphics, new Rectangle2D.Double(dimension.width * .5, dimension.height * .5, dimension.width * .5, dimension.height * .5));
    }
    GraphicsUtil.setQualityDefault(graphics);
  }

  public Tensor curve(GeometricLayer geometricLayer, Graphics2D graphics, final int index) {
    CurveSubdivisionSchemes scheme = schemes.get(index);
    PathRender pathRender = renders.get(index);
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
        // TODO somewhat redundant to BiinvariantMeanSubdivisionDemo
        if (CurveSubdivisionHelper.isDual(scheme) && //
            level % 2 == 1 && //
            1 < control.length()) {
          refined = Join.of( //
              Tensors.of(geodesicDisplay.geodesicInterface().midpoint(control.get(0), prev.get(0))), //
              refined, //
              Tensors.of(geodesicDisplay.geodesicInterface().midpoint(Last.of(prev), Last.of(control))) //
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
