// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.Graphics2D;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.noise.SimplexContinuousNoise;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.LogWeightings;
import ch.ethz.idsc.sophus.lie.so3.Rodrigues;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/* package */ class S2AnimationDemo extends LogWeightingDemo {
  private final JToggleButton jToggleAxes = new JToggleButton("axes");
  private final JToggleButton jToggleAnimate = new JToggleButton("animate");
  private final Timing timing = Timing.started();
  // ---
  private Tensor snapshotUncentered;
  private Tensor snapshot;

  public S2AnimationDemo() {
    super(true, GeodesicDisplays.S2_ONLY, LogWeightings.list());
    setMidpointIndicated(false);
    {
      timerFrame.jToolBar.add(jToggleAxes);
      jToggleAxes.setSelected(true);
    }
    {
      jToggleAnimate.addActionListener(e -> {
        if (jToggleAnimate.isSelected()) {
          snapshotUncentered = getControlPointsSe2();
          Tensor controlPointsAll = getGeodesicControlPoints();
          if (0 < controlPointsAll.length()) {
            snapshot = snapshotUncentered.copy();
          }
        } else
          setControlPointsSe2(snapshotUncentered);
      });
      timerFrame.jToolBar.add(jToggleAnimate);
    }
    setControlPointsSe2(Tensors.fromString("{{0, 0, 0}, {0.3, -0.2, 0}, {0.4, 0.2, 0}, {-0.1, 0.3, 0}, {-0.2, -0.3, 0}, {-0.3, 0, 0}}"));
  }

  private static Tensor random(double toc, int index) {
    return Tensors.vector( //
        SimplexContinuousNoise.FUNCTION.at(toc, index, 0), //
        SimplexContinuousNoise.FUNCTION.at(toc, index, 1), //
        SimplexContinuousNoise.FUNCTION.at(toc, index, 2)).multiply(RealScalar.of(1));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (jToggleAxes.isSelected())
      AxesRender.INSTANCE.render(geometricLayer, graphics);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor controlPoints = getGeodesicControlPoints();
    if (0 < controlPoints.length()) {
      if (jToggleAnimate.isSelected()) {
        Tensor vector = random(10 + timing.seconds() * 0.1, 0);
        Tensor vectorExp = Rodrigues.vectorExp(vector);
        Tensor list = Tensors.empty();
        for (Tensor xya : snapshot) {
          Tensor project = vectorExp.dot(geodesicDisplay.project(xya));
          Tensor xya_ = geodesicDisplay.toPoint(project).append(RealScalar.ZERO);
          list.append(xya_);
          // project);
        }
        setControlPointsSe2(list);
      }
      RenderQuality.setQuality(graphics);
      Tensor sequence = controlPoints.extract(1, controlPoints.length());
      TensorUnaryOperator tensorUnaryOperator = operator(sequence);
      LeversRender leverRender = LeversRender.of( //
          geodesicDisplay, //
          tensorUnaryOperator, //
          sequence, //
          controlPoints.get(0), geometricLayer, graphics);
      leverRender.renderLevers();
      leverRender.renderWeights();
      leverRender.renderSequence();
      leverRender.renderOrigin();
    }
  }

  public static void main(String[] args) {
    new S2AnimationDemo().setVisible(1200, 600);
  }
}
