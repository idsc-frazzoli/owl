// code by gjoel
package ch.ethz.idsc.owl.bot.se2.rrts;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

import javax.imageio.ImageIO;

import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;

import ch.ethz.idsc.owl.ani.adapter.FallbackControl;
import ch.ethz.idsc.owl.ani.api.AbstractRrtsEntity;
import ch.ethz.idsc.owl.bot.r2.R2ImageRegionWrap;
import ch.ethz.idsc.owl.bot.r2.R2ImageRegions;
import ch.ethz.idsc.owl.bot.se2.Se2StateSpaceModel;
import ch.ethz.idsc.owl.bot.se2.glc.CarEntity;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.gui.ren.LaneRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.lane.LaneConsumer;
import ch.ethz.idsc.owl.math.lane.LaneInterface;
import ch.ethz.idsc.owl.math.lane.StableLanes;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.rrts.LaneRrtsPlannerServer;
import ch.ethz.idsc.owl.rrts.RrtsNodeCollections;
import ch.ethz.idsc.owl.rrts.adapter.SampledTransitionRegionQuery;
import ch.ethz.idsc.owl.rrts.adapter.SimpleLaneConsumer;
import ch.ethz.idsc.owl.rrts.adapter.TransitionRegionQueryUnion;
import ch.ethz.idsc.owl.rrts.core.RrtsNodeCollection;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.sophus.app.api.ClothoidDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.PointsRender;
import ch.ethz.idsc.sophus.crv.subdiv.LaneRiesenfeldCurveSubdivision;
import ch.ethz.idsc.sophus.util.plot.ListPlot;
import ch.ethz.idsc.sophus.util.plot.VisualSet;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.ScalarSummaryStatistics;
import ch.ethz.idsc.tensor.red.StandardDeviation;

/* package */ enum ClothoidLaneSimulation {
  ;
  private static final Tensor[] CONTROLS = { // TODO GJOEL fill in
      Tensors.fromString("{{6.017, 4.983, 0.785},{8.100, 5.100, -1.571},{1.667, 1.950, -3.142}}"), //
      Tensors.fromString("{{1.817, 7.283, -3.665},{5.483, 8.817, -7.854},{7.950, 10.733, -3.142}}"), //
      Tensors.fromString("{{6.000, 5.617, -1.571},{6.967, 2.500, 0.000},{9.350, 2.500, 0.000},{10.383, 5.500, 1.571}}"), //
      Tensors.fromString("{{1.000, 4.450, 0.000},{5.500, 4.450, 0.000},{6.350, 5.650, 0.000},{10.500, 5.650, 0.000}}"), //
      Tensors.fromString("{{8.117, 3.300, 1.571},{6.967, 6.717, 2.618},{4.150, 7.167, 3.142},{3.383, 8.600, 1.571}}"), //
      Tensors.fromString("{{5.750, 1.917, -3.142},{2.000, 1.917, -3.142},{1.150, 3.250, -4.712},{2.000, 4.450, -6.283},"
          + "{5.000, 4.450, -6.283},{8.050, 6.500, -6.283},{7.767, 10.750, -3.142}}") };
  private static final int REPS = 10;
  private static final Scalar DELAY_HINT = RealScalar.of(3);
  // ---
  private static final File DIRECTORY = HomeDirectory.Pictures("LaneSim");
  private static final int WIDTH = 900;
  private static final int HEIGHT = 600;
  private static final Scalar OVERHEAD = RealScalar.of(.5);
  // ---
  private static final GeodesicDisplay GEODESIC_DISPLAY = ClothoidDisplay.INSTANCE;
  private static final int DEGREE = 3;
  private static final int LEVELS = 5;
  private static final Scalar LANE_WIDTH = RealScalar.of(1.1);
  // ---
  private static final R2ImageRegionWrap R2_IMAGE_REGION_WRAP = R2ImageRegions._GTOB;
  private static final TransitionRegionQuery TRANSITION_REGION_QUERY = TransitionRegionQueryUnion.wrap( //
      new SampledTransitionRegionQuery(R2_IMAGE_REGION_WRAP.region(), RealScalar.of(0.05)), //
      new TransitionCurvatureQuery(5.));

  public static void main(String[] args) throws Exception {
    DIRECTORY.mkdirs();
    int task = 0;
    for (Tensor controlPoints : CONTROLS) {
      // controlPoints.stream().forEach(System.out::println);
      LaneInterface lane = StableLanes.of( //
          controlPoints, //
          LaneRiesenfeldCurveSubdivision.of(GEODESIC_DISPLAY.geodesicInterface(), DEGREE)::string, //
          LEVELS, LANE_WIDTH.multiply(RationalScalar.HALF));
      // ---
      BufferedImage bufferedImage = new BufferedImage(WIDTH, WIDTH, BufferedImage.TYPE_INT_ARGB);
      Tensor diagonal = Tensors.of( //
          RealScalar.of(bufferedImage.getWidth()).divide(R2_IMAGE_REGION_WRAP.range().Get(0)), //
          RealScalar.of(bufferedImage.getHeight()).divide(R2_IMAGE_REGION_WRAP.range().Get(1)), //
          RealScalar.ONE);
      Tensor matrix = DiagonalMatrix.with(diagonal);
      GeometricLayer geometricLayer = GeometricLayer.of(matrix);
      Graphics2D graphics = bufferedImage.createGraphics();
      graphics.setColor(Color.WHITE);
      graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
      graphics.setColor(new Color(0, 0, 0, 16));
      RegionRenders.create(R2_IMAGE_REGION_WRAP.region()).render(geometricLayer, graphics);
      LaneRender laneRender = new LaneRender();
      laneRender.setLane(lane, false);
      laneRender.render(geometricLayer, graphics);
      PointsRender pointsRender = new PointsRender(new Color(255, 128, 128, 64), new Color(255, 128, 128, 255));
      pointsRender.new Show(GEODESIC_DISPLAY, GEODESIC_DISPLAY.shape(), controlPoints).render(geometricLayer, graphics);
      ImageIO.write(bufferedImage, "png", new File(DIRECTORY, String.format("scenario_%d.png", task)));
      // ---
      Tensor ttfs = Tensors.empty();
      VisualSet visualSet = new VisualSet();
      visualSet.setAxesLabelX("time [s]");
      visualSet.setAxesLabelY("cost");
      for (int rep = 0; rep < REPS; rep++) {
        System.out.println("iteration " + (rep + 1));
        run(lane, visualSet, ttfs);
      }
      ScalarSummaryStatistics statistics = new ScalarSummaryStatistics();
      ttfs.stream().map(Tensor::Get).forEach(statistics);
      System.out.println(String.format("\nscenario %d:" //
          + "\n\ttime to first solution = %s +/- %s (min=%s, max=%s)" + "\n\tsucess rate: %.2f%%\n", task, statistics.getAverage(),
          StandardDeviation.ofVector(ttfs), statistics.getMin(), statistics.getMax(), 100. * ttfs.length() / REPS));
      JFreeChart jFreeChart = ListPlot.of(visualSet);
      File file = new File(DIRECTORY, String.format("costs_%d.png", task++));
      ChartUtils.saveChartAsPNG(file, jFreeChart, WIDTH, HEIGHT);
    }
  }

  private synchronized static void run(LaneInterface lane, VisualSet visualSet, Tensor ttfs) throws Exception {
    StateTime stateTime = new StateTime(lane.midLane().get(0), RealScalar.ZERO);
    Consumer<Map<Double, Scalar>> process = observations -> {
      Tensor domain = Tensor.of(observations.keySet().stream().map(d -> Quantity.of(d, "s")));
      Tensor values = Tensor.of(observations.values().stream());
      visualSet.add(domain, values);
      observations.keySet().stream().map(RealScalar::of).findFirst().ifPresent(ttfs::append);
    };
    SimulationEntity entity = //
        new SimulationEntity(stateTime, TRANSITION_REGION_QUERY, Tensors.vector(0, 0), R2_IMAGE_REGION_WRAP.range(), true, DELAY_HINT, process);
    LaneConsumer laneConsumer = new SimpleLaneConsumer(entity, null, Collections.singleton(entity));
    laneConsumer.accept(lane);
    Thread.sleep((long) (DELAY_HINT.add(OVERHEAD).number().doubleValue() * 1000));
  }
}

/** variant of {@link ClothoidLaneRrtsEntity} */
class SimulationEntity extends AbstractRrtsEntity {
  private static final StateSpaceModel STATE_SPACE_MODEL = Se2StateSpaceModel.INSTANCE;
  private final Scalar delayHint;

  /** @param stateTime initial position of entity */
  /* package */ SimulationEntity(StateTime stateTime, TransitionRegionQuery transitionRegionQuery, Tensor lbounds, Tensor ubounds, boolean greedy,
      Scalar delayHint, Consumer<Map<Double, Scalar>> process) {
    super( //
        new LaneRrtsPlannerServer( //
            ClothoidTransitionSpace.INSTANCE, //
            transitionRegionQuery, //
            RationalScalar.of(1, 10), //
            STATE_SPACE_MODEL, //
            greedy) {
          private final Tensor lbounds_ = lbounds.copy().append(RealScalar.ZERO).unmodifiable();
          private final Tensor ubounds_ = ubounds.copy().append(Pi.TWO).unmodifiable();

          @Override // from DefaultRrtsPlannerServer
          protected RrtsNodeCollection rrtsNodeCollection() {
            return new RrtsNodeCollections(ClothoidRrtsNdType.INSTANCE, lbounds_, ubounds_);
          }

          @Override // from RrtsPlannerServer
          protected Tensor uBetween(StateTime orig, StateTime dest) {
            return Se2RrtsFlow.uBetween(orig, dest);
          }

          @Override // from ObservingExpandInterface
          public boolean isObserving() {
            return true;
          }

          @Override // from ObservingExpandInterface
          public void process(Map<Double, Scalar> observations) {
            process.accept(observations);
            super.process(observations);
          }
        }, //
        new SimpleEpisodeIntegrator( //
            STATE_SPACE_MODEL, //
            EulerIntegrator.INSTANCE, //
            stateTime), //
        CarEntity.createPurePursuitControl());
    add(FallbackControl.of(Array.zeros(3)));
    this.delayHint = delayHint;
  }

  @Override // from AbstractRrtsEntity
  protected Tensor shape() {
    return null;
  }

  @Override // from TrajectoryEntity
  public Scalar delayHint() {
    return delayHint;
  }
}
