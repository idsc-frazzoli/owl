// code by gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;

import ch.ethz.idsc.owl.bot.r2.R2ImageRegionWrap;
import ch.ethz.idsc.owl.bot.r2.R2ImageRegions;
import ch.ethz.idsc.owl.data.tree.Nodes;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.lane.LaneConsumer;
import ch.ethz.idsc.owl.lane.LaneInterface;
import ch.ethz.idsc.owl.lane.StableLanes;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.rrts.adapter.SampledTransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.adapter.SimpleLaneConsumer;
import ch.ethz.idsc.owl.rrts.adapter.TransitionRegionQueryUnion;
import ch.ethz.idsc.owl.rrts.core.RrtsNode;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gds.Se2ClothoidDisplay;
import ch.ethz.idsc.sophus.math.MinMax;
import ch.ethz.idsc.sophus.ref.d1.LaneRiesenfeldCurveSubdivision;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.fig.ListPlot;
import ch.ethz.idsc.tensor.fig.VisualRow;
import ch.ethz.idsc.tensor.fig.VisualSet;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Min;
import ch.ethz.idsc.tensor.red.ScalarSummaryStatistics;
import ch.ethz.idsc.tensor.red.StandardDeviation;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ enum ClothoidLaneSimulation {
  ;
  private static final Tensor[] CONTROLS = { //
      Tensors.fromString("{{6.017, 4.983, 0.785},{8.100, 5.100, -1.571},{1.667, 1.950, -3.142}}"), //
      Tensors.fromString("{{1.817, 7.283, -3.665},{5.483, 8.817, -7.854},{7.950, 10.733, -3.142}}"), //
      Tensors.fromString("{{6.000, 5.617, -1.571},{6.967, 2.500, 0.000},{9.350, 2.500, 0.000},{10.383, 5.500, 1.571}}"), //
      Tensors.fromString("{{1.000, 4.450, 0.000},{5.500, 4.450, 0.000},{6.350, 5.650, 0.000},{10.500, 5.650, 0.000}}"), //
      Tensors.fromString("{{8.117, 3.300, 1.571},{6.967, 6.717, 2.618},{4.150, 7.167, 3.142},{3.383, 8.600, 1.571}}"), //
      Tensors.fromString("{{5.750, 1.917, -3.142},{2.000, 1.917, -3.142},{1.150, 3.250, -4.712},{2.000, 4.450, -6.283},"
          + "{5.000, 4.450, -6.283},{8.050, 6.500, -6.283},{7.767, 10.750, -3.142}}") };
  private static final int REPS = 25;
  private static final Scalar DELAY_HINT = RealScalar.of(3);
  // ---
  private static final File DIRECTORY = HomeDirectory.Pictures("LaneSim");
  private static final int WIDTH = 900;
  private static final int HEIGHT = 600;
  private static final Scalar OVERHEAD = RealScalar.of(0.5);
  private static final Scalar MIN_RESOLUTION = RealScalar.of(0.1);
  // ---
  private static final ManifoldDisplay GEODESIC_DISPLAY = Se2ClothoidDisplay.ANALYTIC;
  private static final int DEGREE = 3;
  private static final int LEVELS = 5;
  private static final Scalar LANE_WIDTH = RealScalar.of(1.1);
  // ---
  private static final R2ImageRegionWrap R2_IMAGE_REGION_WRAP = R2ImageRegions._GTOB;
  private static final TransitionRegionQuery TRANSITION_REGION_QUERY = TransitionRegionQueryUnion.wrap( //
      new SampledTransitionRegionQuery(R2_IMAGE_REGION_WRAP.region(), RealScalar.of(0.05)), //
      new ClothoidCurvatureQuery(Clips.absolute(5.)));

  public static void main(String[] args) throws Exception {
    DIRECTORY.mkdirs();
    try (PrintWriter writer = new PrintWriter(new File(DIRECTORY, "report.txt"))) {
      int task = 1;
      for (Tensor controlPoints : CONTROLS) {
        // controlPoints.stream().forEach(System.out::println);
        LaneInterface lane = StableLanes.of( //
            controlPoints, //
            LaneRiesenfeldCurveSubdivision.of(GEODESIC_DISPLAY.geodesicInterface(), DEGREE)::string, //
            LEVELS, LANE_WIDTH.multiply(RationalScalar.HALF));
        // ---
        Tensor diagonal = Tensors.of( //
            RealScalar.of(WIDTH /* graphics.getWidth() */).divide(R2_IMAGE_REGION_WRAP.range().Get(0)), //
            RealScalar.of(WIDTH /* graphics.getHeight() */).divide(R2_IMAGE_REGION_WRAP.range().Get(1)), //
            RealScalar.ONE);
        Tensor matrix = DiagonalMatrix.with(diagonal);
        GeometricLayer geometricLayer = GeometricLayer.of(matrix);
        // SVGUtils.writeToSVG(new File(DIRECTORY, String.format("scenario_%d.svg", task)),
        // scenario(geometricLayer, lane).getSVGElement() /* graphics.getSVGElement() */);
        // ---
        Tensor ttfs = Tensors.empty();
        VisualSet visualSet = new VisualSet();
        visualSet.setAxesLabelX("time [s]");
        visualSet.setAxesLabelY("cost");
        for (int rep = 0; rep < REPS; rep++) {
          System.out.println("iteration " + (rep + 1));
          run(lane, visualSet, ttfs, geometricLayer, task, rep + 1);
        }
        ScalarSummaryStatistics statistics = new ScalarSummaryStatistics();
        ttfs.stream().map(Scalar.class::cast).forEach(statistics);
        String summary = String.format("scenario %d:" //
            + "\n\ttime to first solution = %s +/- %s (min=%s, max=%s)" + "\n\tsuccess rate: %.2f%%", //
            task, statistics.getAverage(), StandardDeviation.ofVector(ttfs), statistics.getMin(), statistics.getMax(), 100. * ttfs.length() / REPS);
        writer.println(summary.replace("\n", System.lineSeparator()));
        System.out.println("\n" + summary + "\n");
        JFreeChart jFreeChart = ListPlot.of(visualSet);
        jFreeChart.getXYPlot().setDomainAxis(new LogarithmicAxis(visualSet.getAxesLabelX()));
        List<MinMax> minMaxes = visualSet.visualRows().stream().map(VisualRow::points).filter(Tensors::nonEmpty) //
            .map(points -> MinMax.of(points.get(Tensor.ALL, 1))).collect(Collectors.toList());
        jFreeChart.getXYPlot().getRangeAxis().setRange( //
            Math.max(0., 0.9 * minMaxes.stream() //
                .map(MinMax::min) //
                .map(Scalar.class::cast) //
                .reduce(Min::of) //
                .get().number().doubleValue()), //
            1.1 * minMaxes.stream() //
                .map(MinMax::max) //
                .map(Scalar.class::cast) //
                .reduce(Max::of) //
                .get().number().doubleValue());
        File file = new File(DIRECTORY, String.format("costs_%d.png", task++));
        ChartUtils.saveChartAsPNG(file, jFreeChart, WIDTH, HEIGHT);
      }
    }
  }

  private synchronized static void run(LaneInterface lane, VisualSet visualSet, Tensor ttfs, GeometricLayer geometricLayer, int task, int rep)
      throws Exception {
    StateTime stateTime = new StateTime(lane.midLane().get(0), RealScalar.ZERO);
    Consumer<Map<Double, Scalar>> process = observations -> {
      Tensor domain = Tensor.of(observations.keySet().stream().map(d -> Quantity.of(d, "s")));
      Tensor values = Tensor.of(observations.values().stream());
      visualSet.add(domain, values);
      observations.keySet().stream().map(RealScalar::of).findFirst().ifPresent(ttfs::append);
    };
    List<RrtsNode> first = new ArrayList<>();
    List<RrtsNode> last = new ArrayList<>();
    ClothoidLaneEntity entity = //
        new ClothoidLaneEntity(stateTime, TRANSITION_REGION_QUERY, Tensors.vector(0, 0), R2_IMAGE_REGION_WRAP.range(), true, DELAY_HINT, //
            process, rrtsNode -> first.addAll(Nodes.listFromRoot(rrtsNode)), rrtsNode -> last.addAll(Nodes.listFromRoot(rrtsNode)));
    LaneConsumer laneConsumer = new SimpleLaneConsumer(entity, null, Collections.singleton(entity));
    laneConsumer.accept(lane);
    Thread.sleep((long) (DELAY_HINT.add(OVERHEAD).number().doubleValue() * 1000));
    // ---
    // if (!last.isEmpty()) {
    // SVGGraphics2D graphics = scenario(geometricLayer, lane);
    // // TreeRender treeRender = new TreeRender();
    // // treeRender.setCollection(Nodes.ofSubtree(last.get(0)));
    // // treeRender.render(geometricLayer, graphics);
    // TransitionRender transitionRender = new TransitionRender(ClothoidTransitionSpace.ANALYTIC);
    // transitionRender.setCollection(Nodes.ofSubtree(last.get(0)));
    // transitionRender.render(geometricLayer, graphics);
    // render(first, geometricLayer, graphics, Color.ORANGE);
    // render(last, geometricLayer, graphics, Color.BLUE);
    // SVGUtils.writeToSVG(new File(DIRECTORY, String.format("scenario_%d_%d.svg", task, rep)), graphics.getSVGElement());
    // }
  }

  private static void render(Collection<RrtsNode> nodes, GeometricLayer geometricLayer, Graphics2D graphics2D, Color color) {
    Tensor points = Tensors.empty();
    Iterator<RrtsNode> iterator = nodes.iterator();
    RrtsNode end = iterator.next();
    while (iterator.hasNext()) {
      RrtsNode start = end;
      end = iterator.next();
      ClothoidTransitionSpace.ANALYTIC.connect(start.state(), end.state()).linearized(MIN_RESOLUTION).forEach(points::append);
    }
    graphics2D.setColor(color);
    graphics2D.draw(geometricLayer.toPath2D(points));
  }
  //
  // private static SVGGraphics2D scenario(GeometricLayer geometricLayer, LaneInterface lane) {
  // SVGGraphics2D graphics = new SVGGraphics2D(WIDTH, WIDTH);
  // graphics.setColor(Color.WHITE);
  // graphics.fillRect(0, 0, graphics.getWidth(), graphics.getHeight());
  // graphics.setColor(new Color(0, 0, 0, 16));
  // RegionRenders.create(R2_IMAGE_REGION_WRAP.region()).render(geometricLayer, graphics);
  // LaneRender laneRender = new LaneRender();
  // laneRender.setLane(lane, false);
  // laneRender.render(geometricLayer, graphics);
  // PointsRender pointsRender = new PointsRender(new Color(255, 128, 128, 64), new Color(255, 128, 128, 255));
  // pointsRender.show(GEODESIC_DISPLAY::matrixLift, GEODESIC_DISPLAY.shape(), lane.controlPoints()).render(geometricLayer, graphics);
  // return graphics;
  // }
}
