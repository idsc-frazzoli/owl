// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.stream.Collectors;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.noise.SimplexContinuousNoise;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.LogWeighting;
import ch.ethz.idsc.sophus.app.api.LogWeightings;
import ch.ethz.idsc.sophus.krg.InversePowerVariogram;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.LieGroupOps;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/* package */ class Se2CoveringAnimationDemo extends ControlPointsDemo {
  private static final Tensor BETAS = Tensors.fromString("{0, 1/2, 1, 3/2, 2, 5/2, 3}");
  // ---
  private final SpinnerLabel<LogWeighting> spinnerLogWeighting = new SpinnerLabel<>();
  private final SpinnerLabel<Scalar> spinnerBeta = new SpinnerLabel<>();
  private final JToggleButton jToggleAxes = new JToggleButton("axes");
  private final JToggleButton jToggleAnimate = new JToggleButton("animate");
  private final Timing timing = Timing.started();
  // ---
  private Tensor snapshotUncentered;
  private Tensor snapshot;

  public Se2CoveringAnimationDemo() {
    super(true, GeodesicDisplays.SE2C_SE2);
    setMidpointIndicated(false);
    {
      spinnerLogWeighting.setList(LogWeightings.list());
      spinnerLogWeighting.setIndex(0);
      spinnerLogWeighting.addToComponentReduced(timerFrame.jToolBar, new Dimension(200, 28), "weights");
    }
    {
      spinnerBeta.setList(BETAS.stream().map(Scalar.class::cast).collect(Collectors.toList()));
      spinnerBeta.setIndex(2);
      spinnerBeta.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "beta");
    }
    timerFrame.jToolBar.addSeparator();
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
            GeodesicDisplay geodesicDisplay = geodesicDisplay();
            LieGroup lieGroup = geodesicDisplay.lieGroup();
            LieGroupOps lieGroupOps = new LieGroupOps(lieGroup);
            Tensor origin = controlPointsAll.get(0);
            snapshot = lieGroupOps.allLeft(controlPointsAll, lieGroup.element(origin).inverse().toCoordinate());
          }
        } else
          setControlPointsSe2(snapshotUncentered);
      });
      timerFrame.jToolBar.add(jToggleAnimate);
    }
    setControlPointsSe2(Tensors.fromString("{{0, 0, 0}, {3, -2, -1}, {4, 2, 1}, {-1, 3, 2}, {-2, -3, -2}, {-3, 0, 0.3}}"));
  }

  private static Tensor random(double toc, int index) {
    return Tensors.vector( //
        SimplexContinuousNoise.FUNCTION.at(toc, index, 0) * 2, //
        SimplexContinuousNoise.FUNCTION.at(toc, index, 1) * 2, //
        SimplexContinuousNoise.FUNCTION.at(toc, index, 2));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (jToggleAxes.isSelected())
      AxesRender.INSTANCE.render(geometricLayer, graphics);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    LieGroup lieGroup = geodesicDisplay.lieGroup();
    LieGroupOps lieGroupOps = new LieGroupOps(lieGroup);
    Tensor controlPoints = getGeodesicControlPoints();
    if (0 < controlPoints.length()) {
      if (jToggleAnimate.isSelected())
        setControlPointsSe2(lieGroupOps.allConjugate(snapshot, random(10 + timing.seconds() * 0.1, 0)));
      RenderQuality.setQuality(graphics);
      Tensor sequence = controlPoints.extract(1, controlPoints.length());
      TensorUnaryOperator tensorUnaryOperator = spinnerLogWeighting.getValue().from( //
          geodesicDisplay.vectorLogManifold(), //
          InversePowerVariogram.of(spinnerBeta.getValue()), //
          sequence);
      LeverRender leverRender = LeverRender.of( //
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
    new Se2CoveringAnimationDemo().setVisible(1200, 600);
  }
}
