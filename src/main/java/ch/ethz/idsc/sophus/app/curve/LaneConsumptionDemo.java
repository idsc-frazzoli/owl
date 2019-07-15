// code by gjoel
package ch.ethz.idsc.sophus.app.curve;

import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import javax.swing.JButton;

import ch.ethz.idsc.owl.gui.ren.LaneRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.lane.LaneInterface;
import ch.ethz.idsc.owl.math.lane.StableLane;
import ch.ethz.idsc.sophus.app.api.Clothoid1Display;
import ch.ethz.idsc.sophus.app.api.Se2CoveringGeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.Se2GeodesicDisplay;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Serialization;

public class LaneConsumptionDemo extends BaseCurvatureDemo {
  private final LaneRender laneRender = new LaneRender(false);
  private LaneInterface lane = null;

  @SafeVarargs
  public LaneConsumptionDemo(Consumer<LaneInterface>... consumers) {
    this(Arrays.asList(consumers));
  }

  public LaneConsumptionDemo(Collection<Consumer<LaneInterface>> consumers) {
    super(Arrays.asList( //
        Clothoid1Display.INSTANCE, //
        // Clothoid2Display.INSTANCE, //
        // Clothoid3Display.INSTANCE, //
        Se2CoveringGeodesicDisplay.INSTANCE, //
        Se2GeodesicDisplay.INSTANCE));
    jToggleCurvature.setSelected(false);
    {
      timerFrame.jToolBar.addSeparator();
      JButton jButtonRun = new JButton("run");
      jButtonRun.addActionListener(l -> {
        if (Objects.nonNull(lane))
          consumers.forEach(consumer -> consumer.accept(lane));
      });
      timerFrame.jToolBar.add(jButtonRun);
    }
  }

  @Override
  protected Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics, int degree, int levels, Tensor control) {
    renderControlPoints(geometricLayer, graphics);
    LaneInterface lane = StableLane.of(geodesicDisplay().geodesicInterface(), control, width(), degree, levels);
    try {
      this.lane = Serialization.copy(lane);
    } catch (Exception e) {
      // ---
    }
    laneRender.setLane(lane);
    laneRender.render(geometricLayer, graphics);
    return lane.midLane();
  }

  protected final Scalar width() {
    return RationalScalar.of(jSlider.getValue(), 200);
  }

  protected final Optional<LaneInterface> lane() {
    return Optional.ofNullable(lane);
  }

  public static void main(String[] args) {
    LaneConsumptionDemo demo = new LaneConsumptionDemo( //
        lane -> System.out.println("control points: " + lane.controlPoints().length()), //
        lane -> System.out.println("refined points: " + lane.midLane().length()));
    demo.timerFrame.jFrame.setBounds(100, 100, 1200, 900);
    demo.timerFrame.jFrame.setVisible(true);
  }
}
