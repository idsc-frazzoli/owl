// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.GraphicsUtil;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.PointsRender;
import ch.ethz.idsc.sophus.app.api.SnGeodesicDisplay;
import ch.ethz.idsc.sophus.hs.sn.SnRandomSample;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.pdf.DiscreteUniformDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;

/* package */ class ClassificationDemo extends ControlPointsDemo {
  private static final Random RANDOM = new Random(2);
  private static final ColorDataIndexed COLOR_DATA_INDEXED_O = ColorDataLists._097.cyclic();
  private static final ColorDataIndexed COLOR_DATA_INDEXED_T = COLOR_DATA_INDEXED_O.deriveWithAlpha(128);
  private final JToggleButton jToggleAxes = new JToggleButton("axes");
  private final JToggleButton jToggleClas = new JToggleButton("clas");
  private final SpinnerLabel<Integer> spinnerCount = new SpinnerLabel<>();
  private final JButton jButtonShuffle = new JButton("shuffle");
  private Classification argMaxClassification;

  public ClassificationDemo() {
    super(false, GeodesicDisplays.SE2C_S2_R2);
    setMidpointIndicated(false);
    setPositioningEnabled(false);
    {
      timerFrame.jToolBar.add(jToggleAxes);
      timerFrame.jToolBar.add(jToggleClas);
    }
    {
      spinnerCount.setList(Arrays.asList(10, 15, 20, 25, 30, 40));
      spinnerCount.setValue(15);
      spinnerCount.addToComponentReduced(timerFrame.jToolBar, new Dimension(60, 28), "magnify");
      spinnerCount.addSpinnerListener(this::shuffle);
    }
    {
      jButtonShuffle.addActionListener(e -> shuffle(spinnerCount.getValue()));
      timerFrame.jToolBar.add(jButtonShuffle);
    }
    shuffle(spinnerCount.getValue());
  }

  void shuffle(int n) {
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    final Tensor xyzs;
    if (geodesicDisplay instanceof SnGeodesicDisplay) {
      RandomSampleInterface randomSampleInterface = SnRandomSample.of(2);
      xyzs = Tensors.vector(i -> randomSampleInterface.randomSample(RANDOM), n);
      xyzs.set(Scalar::abs, Tensor.ALL, 2);
    } else {
      xyzs = Join.of(1, RandomVariate.of(UniformDistribution.of(-5, 5), RANDOM, n, 2), RandomVariate.of(UniformDistribution.of(-2, 2), RANDOM, n, 1));
    }
    setControlPointsSe2(xyzs);
    Tensor vector = RandomVariate.of(DiscreteUniformDistribution.of(0, 3), RANDOM, n);
    argMaxClassification = new Classification(vector);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (jToggleAxes.isSelected())
      AxesRender.INSTANCE.render(geometricLayer, graphics);
    GraphicsUtil.setQualityHigh(graphics);
    Tensor mouseSe2State = geometricLayer.getMouseSe2State();
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor controlPoints = getGeodesicControlPoints();
    Tensor geodesicMouse = geodesicDisplay.project(mouseSe2State);
    LeverRender leverRender = new LeverRender(geodesicDisplay, controlPoints, geodesicMouse, geometricLayer, graphics);
    leverRender.renderLevers();
    if (jToggleClas.isSelected()) {
      Tensor shape = geodesicDisplay.shape().multiply(RealScalar.of(1.4));
      for (int label = 0; label < argMaxClassification.size(); ++label) {
        Tensor sequence = Tensor.of(argMaxClassification.labelIndices(label).mapToObj(controlPoints::get));
        PointsRender pointsRender = new PointsRender( //
            COLOR_DATA_INDEXED_T.getColor(label), //
            COLOR_DATA_INDEXED_O.getColor(label));
        pointsRender.show(geodesicDisplay::matrixLift, shape, sequence).render(geometricLayer, graphics);
        int bestLabel = argMaxClassification.getArgMax(leverRender.getWeights());
        geometricLayer.pushMatrix(geodesicDisplay.matrixLift(geodesicMouse));
        Path2D path2d = geometricLayer.toPath2D(shape, true);
        graphics.setColor(COLOR_DATA_INDEXED_T.getColor(bestLabel));
        graphics.fill(path2d);
        graphics.setColor(COLOR_DATA_INDEXED_O.getColor(bestLabel));
        graphics.draw(path2d);
        geometricLayer.popMatrix();
      }
    } else {
      leverRender.renderWeights();
      leverRender.renderSequence();
      leverRender.renderOrigin();
    }
  }

  public static void main(String[] args) {
    new ClassificationDemo().setVisible(1200, 900);
  }
}
