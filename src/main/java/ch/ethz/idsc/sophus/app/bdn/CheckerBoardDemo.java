// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.java.awt.SpinnerListener;
import ch.ethz.idsc.owl.gui.region.ImageRender;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.ArrayPlotRender;
import ch.ethz.idsc.sophus.app.api.GeodesicArrayPlot;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.H2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.PolygonCoordinates;
import ch.ethz.idsc.sophus.app.api.R2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.S2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.lev.LeversRender;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Floor;
import ch.ethz.idsc.tensor.sca.Mod;

/** transfer weights from barycentric coordinates defined by set of control points
 * in the square domain (subset of R^2) to means in non-linear spaces */
/* package */ class CheckerBoardDemo extends ExportWeightingDemo //
    implements SpinnerListener<GeodesicDisplay> {
  public static final ColorDataIndexed COLOR_DATA_INDEXED = HueColorData.of(6, 3);
  // ---
  public final JToggleButton jToggleButton = new JToggleButton("freeze");
  private Tensor reference;

  public CheckerBoardDemo() {
    super(true, GeodesicDisplays.R2_H2_S2, PolygonCoordinates.list());
    // ---
    timerFrame.jToolBar.add(jToggleButton);
    // ---
    GeodesicDisplay geodesicDisplay = R2GeodesicDisplay.INSTANCE;
    actionPerformed(geodesicDisplay);
    addSpinnerListener(this);
    addSpinnerListener(l -> recompute());
    recompute();
    timerFrame.geometricComponent.addRenderInterfaceBackground(AxesRender.INSTANCE);
  }

  @Override
  public final void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    // if (jToggleAxes.isSelected())
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    RenderQuality.setQuality(graphics);
    renderControlPoints(geometricLayer, graphics);
    // ---
    if (jToggleButton.isSelected()) {
      LeversRender leversRender = LeversRender.of( //
          geodesicDisplay(), getGeodesicControlPoints(), null, geometricLayer, graphics);
      leversRender.renderSurfaceP();
      {
        GeodesicArrayPlot geodesicArrayPlot = geodesicDisplay().geodesicArrayPlot();
        Tensor matrix = geodesicArrayPlot.raster(refinement(), new Some()::compute, DoubleScalar.INDETERMINATE);
        BufferedImage bufferedImage = ArrayPlotRender.rescale(matrix, ColorDataGradients.CLASSIC, 1).export();
        RenderQuality.setDefault(graphics); // default so that raster becomes visible
        Tensor pixel2model = geodesicArrayPlot.pixel2model(new Dimension(bufferedImage.getHeight(), bufferedImage.getHeight()));
        ImageRender.of(bufferedImage, pixel2model).render(geometricLayer, graphics);
      }
    } else {
      reference = getGeodesicControlPoints();
      LeversRender leversRender = LeversRender.of( //
          geodesicDisplay(), reference, null, geometricLayer, graphics);
      leversRender.renderSurfaceP();
    }
  }

  private static final Mod MOD = Mod.function(2);
  private static final Scalar SCALE = RealScalar.of(5.0);

  private class Some {
    private final TensorUnaryOperator operator;

    public Some() {
      operator = operator(getGeodesicControlPoints());
    }

    public Scalar compute(Tensor p) {
      try {
        Tensor weights = operator.apply(p);
        Scalar scalar = Total.ofVector(weights.dot(reference).multiply(SCALE).map(Floor.FUNCTION));
        return MOD.apply(scalar);
      } catch (Exception exception) {
        // ---
      }
      return DoubleScalar.INDETERMINATE;
    }
  }

  int resolution() {
    return 120; // for sequence of length 6
  }

  @Override
  public void actionPerformed(GeodesicDisplay geodesicDisplay) {
    if (geodesicDisplay instanceof R2GeodesicDisplay) {
      setControlPointsSe2(Tensors.fromString( //
          "{{0.287, -0.958, 0.000}, {-1.017, -0.953, 0.000}, {-0.717, 0.229, 0.000}, {-0.912, 0.669, 0.000}, {-0.644, 0.967, 0.000}, {0.933, 0.908, 0.000}, {0.950, -0.209, 0.000}, {-0.461, 0.637, 0.000}, {0.956, -0.627, 0.000}}"));
    } else //
    if (geodesicDisplay instanceof H2GeodesicDisplay) {
      setControlPointsSe2(Tensors.fromString( //
          "{{-2.900, 2.467, 0.000}, {-0.367, 2.550, 0.000}, {-0.450, 0.400, 0.000}, {-1.533, 0.250, 0.000}, {-0.600, -0.567, 0.000}, {0.250, 2.867, 0.000}, {0.400, -0.683, 0.000}, {0.867, -1.067, 0.000}, {1.450, 2.800, 0.000}, {2.300, 2.117, 0.000}, {2.700, 0.317, 0.000}, {2.183, -0.517, 0.000}, {1.183, 0.167, 0.000}, {1.683, -1.767, 0.000}, {1.600, -2.583, 0.000}, {-0.800, -2.650, 0.000}, {-2.650, -1.900, 0.000}, {-2.917, 0.550, 0.000}}"));
    } else //
    if (geodesicDisplay instanceof S2GeodesicDisplay) {
      setControlPointsSe2(Tensors.fromString( //
          "{{-0.933, -0.325, 0.000}, {-0.708, 0.500, 0.000}, {-0.262, 0.592, 0.000}, {-0.621, 0.746, 0.000}, {-0.375, 0.879, 0.000}, {0.079, 0.979, 0.000}, {0.700, 0.567, 0.000}, {0.096, 0.775, 0.000}, {-0.233, 0.833, 0.000}, {-0.004, 0.646, 0.000}, {0.733, 0.455, 0.000}, {0.942, 0.242, 0.000}, {0.033, 0.371, 0.000}, {-0.522, 0.372, 0.000}, {-0.808, 0.042, 0.000}, {-0.192, -0.158, 0.000}, {-0.634, -0.188, 0.000}, {0.014, -0.459, 0.000}, {-0.169, 0.260, 0.000}, {0.916, 0.142, 0.000}, {0.792, -0.465, 0.000}, {0.408, -0.200, 0.000}, {0.480, 0.054, 0.000}, {0.121, -0.008, 0.000}, {0.462, -0.800, 0.000}, {0.067, -0.712, 0.000}, {-0.321, -0.621, 0.000}, {0.233, -0.933, 0.000}, {-0.071, -0.975, 0.000}, {-0.200, -0.846, 0.000}, {-0.550, -0.737, 0.000}}"));
    }
  }

  public static void main(String[] args) {
    new CheckerBoardDemo().setVisible(1300, 900);
  }
}
