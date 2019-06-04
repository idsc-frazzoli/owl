// code by astoll
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.owl.ani.api.TrajectoryControl;
import ch.ethz.idsc.owl.bot.r2.R2ImageRegionWrap;
import ch.ethz.idsc.owl.bot.se2.Se2ComboRegion;
import ch.ethz.idsc.owl.bot.se2.Se2MinTimeGoalManager;
import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.glc.adapter.VectorCostGoalAdapter;
import ch.ethz.idsc.owl.glc.core.CostFunction;
import ch.ethz.idsc.owl.glc.core.GoalInterface;
import ch.ethz.idsc.owl.glc.core.PlannerConstraint;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.glc.rl2.StandardRelaxedLexicographicPlanner;
import ch.ethz.idsc.owl.gui.ren.EdgeRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.region.ConeRegion;
import ch.ethz.idsc.owl.math.region.RegionWithDistance;
import ch.ethz.idsc.owl.math.region.So2Region;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.qty.Degree;

public class CarRelaxedEntity extends CarEntity {
  public static CarRelaxedEntity createRelaxedCarEntity(StateTime stateTime, //
      TrajectoryControl trajectoryControl, Tensor partitionScale, FlowsInterface carFlows, //
      Tensor shape, Tensor slack) {
    CarRelaxedEntity entity = new CarRelaxedEntity(stateTime, trajectoryControl, partitionScale, carFlows, shape);
    entity.slacks = slack;
    return entity;
  }

  public CarRelaxedEntity(StateTime stateTime, TrajectoryControl trajectoryControl, Tensor partitionScale, FlowsInterface carFlows, Tensor shape) {
    super(stateTime, trajectoryControl, partitionScale, carFlows, shape);
  }

  private Tensor slacks;
  private final EdgeRender edgeRender = new EdgeRender();
  // private List<CostFunction> costVector = new ArrayList<>();

  @Override
  public final TrajectoryPlanner createTrajectoryPlanner(PlannerConstraint plannerConstraint, Tensor goal) {
    // define goal region
    goalRegion = getGoalRegionWithDistance(goal);
    Se2ComboRegion se2ComboRegion = new Se2ComboRegion(goalRegion, So2Region.periodic(goal.Get(2), goalRadius.Get(2)));
    // define corner cutting costs
    // TODO ANDRE create png file to do this
    BufferedImage bufferedImage = new BufferedImage(640, 640, BufferedImage.TYPE_BYTE_GRAY);
    Graphics graphics = bufferedImage.getGraphics();
    graphics.setColor(Color.WHITE);
    graphics.fillRect(0, 0, 640, 640);
    graphics.setColor(Color.BLACK);
    graphics.fillRect(500, 70, 70, 200);
    graphics.fillRect(270, 200, 230, 70);
    graphics.fillRect(270, 270, 70, 200);
    graphics.fillRect(70, 400, 200, 70);
    Tensor image = Transpose.of(ImageFormat.from(bufferedImage));
    Tensor range = Tensors.vector(12, 12);
    int ttl = 15;
    R2ImageRegionWrap r2ImageRegionWrap = new R2ImageRegionWrap(image, range, ttl);
    CostFunction cornerCuttingCosts = r2ImageRegionWrap.costFunction();
    // define Se2MinTimeGoalManager
    Se2MinTimeGoalManager timeCosts = new Se2MinTimeGoalManager(se2ComboRegion, controls);
    // cost vector
    List<CostFunction> costVector = Arrays.asList(timeCosts, cornerCuttingCosts);
    GoalInterface goalInterface = new VectorCostGoalAdapter(costVector, se2ComboRegion);
    return new StandardRelaxedLexicographicPlanner( //
        stateTimeRaster(), FIXEDSTATEINTEGRATOR, controls, plannerConstraint, goalInterface, slacks);
  }

  @Override
  public RegionWithDistance<Tensor> getGoalRegionWithDistance(Tensor goal) {
    return new ConeRegion(goal, Degree.of(18));
  }

  @Override
  public void expandResult(List<TrajectorySample> head, TrajectoryPlanner trajectoryPlanner) {
    // FIXME ANDRE does this make sense
    // System.out.println(trajectoryPlanner.getQueue());
    // edgeRender.setCollection(trajectoryPlanner.getQueue());
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    edgeRender.getRender().render(geometricLayer, graphics);
    // ---
    super.render(geometricLayer, graphics);
  }
}
