// code by jph
package ch.ethz.idsc.owl.bot.se2.glc;

import java.awt.Graphics2D;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import ch.ethz.idsc.owl.ani.api.EntityControl;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.owl.bot.util.FlowsInterface;
import ch.ethz.idsc.owl.glc.core.TrajectoryPlanner;
import ch.ethz.idsc.owl.gui.ren.EdgeRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.owl.math.state.TrajectorySample;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorsExt;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.qty.Degree;

/** test if api is sufficient to model gokart */
public class GokartEntity extends CarEntity {
  static final Tensor PARTITIONSCALE = TensorsExt.of(2, 2, Degree.of(10).reciprocal()).unmodifiable();
  static final Scalar SPEED = RealScalar.of(2.5);
  static final Scalar LOOKAHEAD = RealScalar.of(3.0);
  static final Scalar MAX_TURNING_PLAN = Degree.of(15);
  static final Scalar MAX_TURNING_RATE = Degree.of(23);
  static final FlowsInterface CARFLOWS = Se2CarFlows.forward(SPEED, MAX_TURNING_PLAN);
  public static final Tensor SHAPE = ResourceData.of("/gokart/footprint/20171201.csv");
  // ---
  private final EdgeRender edgeRender = new EdgeRender();
  /** simulation of occasional feedback from localization algorithm */
  private final EntityControl localizationFeedback = new EntityControl() {
    private final Random random = new Random();
    private final Distribution distribution = NormalDistribution.standard();
    private boolean trigger = false;

    @Override
    public ProviderRank getProviderRank() {
      return ProviderRank.GODMODE;
    }

    @Override
    public Optional<Tensor> control(StateTime tail, Scalar now) {
      Optional<Tensor> optional = Optional.empty();
      if (trigger)
        optional = Optional.of(RandomVariate.of(distribution, 3));
      trigger = 0 == random.nextInt(20); // TODO use now to alter position every 1[s] for instance
      return optional;
    }
  };

  public GokartEntity(StateTime stateTime) {
    super(stateTime, //
        new PurePursuitControl(LOOKAHEAD, MAX_TURNING_RATE), //
        PARTITIONSCALE, CARFLOWS, SHAPE);
    // ---
    add(localizationFeedback);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    edgeRender.getRender().render(geometricLayer, graphics);
    // ---
    super.render(geometricLayer, graphics);
  }

  @Override
  public void expandResult(List<TrajectorySample> head, TrajectoryPlanner trajectoryPlanner) {
    // System.out.println(trajectoryPlanner.getDomainMap().values().size());
    edgeRender.setCollection(trajectoryPlanner.getDomainMap().values());
  }

  public EdgeRender getEdgeRender() {
    return this.edgeRender;
  }
}
