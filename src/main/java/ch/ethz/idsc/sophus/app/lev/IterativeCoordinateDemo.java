// code by jph
package ch.ethz.idsc.sophus.app.lev;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.Optional;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerLabel;
import ch.ethz.idsc.java.awt.SpinnerListener;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gds.R2Display;
import ch.ethz.idsc.sophus.gds.S2Display;
import ch.ethz.idsc.sophus.gds.Se2AbstractDisplay;
import ch.ethz.idsc.sophus.hs.HsDesign;
import ch.ethz.idsc.sophus.hs.VectorLogManifold;
import ch.ethz.idsc.sophus.opt.LogWeightings;
import ch.ethz.idsc.sophus.ply.d2.IterativeCoordinateMatrix;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ class IterativeCoordinateDemo extends LogWeightingDemo implements SpinnerListener<ManifoldDisplay> {
  private final SpinnerLabel<Integer> spinnerTotal = new SpinnerLabel<>();
  // private final JToggleButton jToggleNeutral = new JToggleButton("neutral");

  public IterativeCoordinateDemo() {
    super(true, GeodesicDisplays.R2_ONLY, LogWeightings.list());
    // ---
    spinnerTotal.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 50, 100));
    spinnerTotal.setValue(2);
    // spinnerTotal.addSpinnerListener(this::config);
    spinnerTotal.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "total");
    // ---
    ManifoldDisplay geodesicDisplay = R2Display.INSTANCE;
    setGeodesicDisplay(geodesicDisplay);
    setBitype(Bitype.LEVERAGES1);
    actionPerformed(geodesicDisplay);
    addSpinnerListener(this);
    timerFrame.geometricComponent.addRenderInterfaceBackground(AxesRender.INSTANCE);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    ManifoldDisplay geodesicDisplay = manifoldDisplay();
    Optional<Tensor> optional = getOrigin();
    if (optional.isPresent()) {
      Tensor sequence = getSequence();
      Tensor origin = optional.get();
      LeversRender leversRender = //
          LeversRender.of(geodesicDisplay, sequence, origin, geometricLayer, graphics);
      leversRender.renderSurfaceP();
      LeversHud.render(bitype(), leversRender, null);
      VectorLogManifold vectorLogManifold = geodesicDisplay.hsManifold();
      HsDesign hsDesign = new HsDesign(vectorLogManifold);
      try {
        Tensor matrix = IterativeCoordinateMatrix.of(spinnerTotal.getValue()).origin(hsDesign.matrix(sequence, origin));
        Tensor circum = matrix.dot(sequence);
        // new PointsRender(color_fill, color_draw).show(matrixLift, shape, points);
        // new PointsRender(new Color(128, 128, 128, 64), new Color(128, 128, 128, 255)) //
        // .show(geodesicDisplay::matrixLift, geodesicDisplay.shape(), circum) //
        // .render(geometricLayer, graphics);
        leversRender.renderMatrix2(origin, matrix);
        LeversRender lr2 = LeversRender.of(geodesicDisplay, circum, origin, geometricLayer, graphics);
        lr2.renderSequence();
        lr2.renderIndexP("c");
      } catch (Exception exception) {
        System.err.println(exception.getMessage());
      }
    } else {
      renderControlPoints(geometricLayer, graphics);
    }
  }

  @Override
  public void actionPerformed(ManifoldDisplay geodesicDisplay) {
    if (geodesicDisplay instanceof R2Display) {
      setControlPointsSe2(R2PointCollection.SOME);
    } else //
    if (geodesicDisplay instanceof S2Display) {
      setControlPointsSe2(Tensors.fromString( //
          "{{0.300, 0.092, 0.000}, {-0.563, -0.658, 0.262}, {-0.854, -0.200, 0.000}, {-0.746, 0.663, -0.262}, {0.467, 0.758, 0.262}, {0.446, -0.554, 0.262}}"));
      setControlPointsSe2(Tensors.fromString( //
          "{{-0.521, 0.621, 0.262}, {-0.863, 0.258, 0.000}, {-0.725, 0.588, -0.785}, {0.392, 0.646, 0.000}, {-0.375, 0.021, 0.000}, {-0.525, -0.392, 0.000}}"));
      setControlPointsSe2(Tensors.fromString( //
          "{{-0.583, 0.338, 0.000}, {-0.904, -0.258, 0.262}, {-0.513, 0.804, 0.000}, {0.646, 0.667, 0.000}, {0.704, -0.100, 0.000}, {0.396, -0.688, 0.000}}"));
      setControlPointsSe2(Tensors.fromString( //
          "{{-0.363, 0.388, 0.000}, {-0.825, -0.271, 0.000}, {-0.513, 0.804, 0.000}, {0.646, 0.667, 0.000}, {0.704, -0.100, 0.000}, {-0.075, -0.733, 0.000}}"));
    } else //
    if (geodesicDisplay instanceof Se2AbstractDisplay) {
      setControlPointsSe2(Tensors.fromString(
          "{{3.150, -2.700, -0.524}, {-1.950, -3.683, 0.000}, {-1.500, -1.167, 2.094}, {4.533, -0.733, -1.047}, {8.567, -3.300, -1.309}, {2.917, -5.050, -1.047}}"));
    }
  }

  public static void main(String[] args) {
    new IterativeCoordinateDemo().setVisible(1200, 900);
  }
}
