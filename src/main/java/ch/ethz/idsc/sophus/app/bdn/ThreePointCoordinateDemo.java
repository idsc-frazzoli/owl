// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;

import ch.ethz.idsc.java.awt.SpinnerListener;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.ArrayPlotRender;
import ch.ethz.idsc.sophus.app.api.GeodesicArrayPlot;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplayRender;
import ch.ethz.idsc.sophus.app.api.H2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.LogWeighting;
import ch.ethz.idsc.sophus.app.api.R2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.S2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.ThreePointCoordinates;
import ch.ethz.idsc.sophus.app.lev.LeversRender;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** transfer weights from barycentric coordinates defined by set of control points
 * in the square domain (subset of R^2) to means in non-linear spaces */
/* package */ class ThreePointCoordinateDemo extends A2ScatteredSetCoordinateDemo //
    implements SpinnerListener<GeodesicDisplay> {
  private final List<LogWeighting> array;

  public ThreePointCoordinateDemo() {
    this(ThreePointCoordinates.list());
  }

  public ThreePointCoordinateDemo(List<LogWeighting> array) {
    super(array);
    this.array = array;
    // ---
    GeodesicDisplay geodesicDisplay = R2GeodesicDisplay.INSTANCE;
    actionPerformed(geodesicDisplay);
    addSpinnerListener(this);
    addSpinnerListener(l -> recompute());
    recompute();
  }

  @Override
  public final void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    super.render(geometricLayer, graphics);
    // ---
    LeversRender leversRender = LeversRender.of( //
        geodesicDisplay(), getGeodesicControlPoints(), null, geometricLayer, graphics);
    leversRender.renderSurfaceP();
  }

  @Override
  public final void actionPerformed(ActionEvent actionEvent) {
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    File root = HomeDirectory.Pictures( //
        getClass().getSimpleName(), //
        geodesicDisplay.toString());
    root.mkdirs();
    for (LogWeighting logWeighting : array) {
      Tensor sequence = getGeodesicControlPoints();
      TensorUnaryOperator tensorUnaryOperator = logWeighting.operator( //
          null, geodesicDisplay.vectorLogManifold(), null, sequence);
      System.out.print("computing " + logWeighting);
      GeodesicArrayPlot geodesicArrayPlot = geodesicDisplay.geodesicArrayPlot();
      int refinement = resolution();
      ArrayPlotRender arrayPlotRender = arrayPlotRender(sequence, refinement, tensorUnaryOperator, 1);
      BufferedImage foreground = arrayPlotRender.export();
      BufferedImage background = new BufferedImage(foreground.getWidth(), foreground.getHeight(), BufferedImage.TYPE_INT_ARGB);
      Graphics2D graphics = background.createGraphics();
      if (geodesicDisplay instanceof S2GeodesicDisplay) {
        Tensor matrix = geodesicArrayPlot.pixel2model(new Dimension(refinement, refinement));
        GeometricLayer geometricLayer = GeometricLayer.of(Inverse.of(matrix));
        for (int count = 0; count < sequence.length(); ++count) {
          GeodesicDisplayRender.render_s2(geometricLayer, graphics);
          geometricLayer.pushMatrix(Se2Matrix.translation(Tensors.vector(2, 0)));
        }
      }
      graphics.drawImage(foreground, 0, 0, null);
      try {
        File file = new File(root, logWeighting.toString() + ".png");
        ImageIO.write(background, "png", file);
      } catch (Exception exception) {
        exception.printStackTrace();
      }
      System.out.println(" done");
    }
    System.out.println("all done");
  }

  int resolution() {
    return 70; // for sequence of length 6
  }

  @Override
  public void actionPerformed(GeodesicDisplay geodesicDisplay) {
    if (geodesicDisplay instanceof R2GeodesicDisplay) {
      setControlPointsSe2(Tensors.fromString( //
          "{{-1.017, -0.953, 0.000}, {-0.991, 0.113, 0.000}, {-0.644, 0.967, 0.000}, {0.509, 0.840, 0.000}, {0.689, 0.513, 0.000}, {0.956, -0.627, 0.000}}"));
    } else //
    if (geodesicDisplay instanceof H2GeodesicDisplay) {
      setControlPointsSe2(Tensors.fromString( //
          "{{-1.900, 1.783, 0.000}, {-0.867, 2.450, 0.000}, {2.300, 2.117, 0.000}, {2.567, 0.150, 0.000}, {1.600, -2.583, 0.000}, {-2.550, -1.817, 0.000}}"));
    } else //
    if (geodesicDisplay instanceof S2GeodesicDisplay) {
      setControlPointsSe2(Tensors.fromString( //
          "{{-0.933, -0.325, 0.000}, {-0.708, 0.500, 0.000}, {0.217, 0.683, 0.000}, {0.408, 0.125, 0.000}, {0.404, -0.333, 0.000}, {-0.200, -0.846, 0.000}}"));
    }
  }

  public static void main(String[] args) {
    new ThreePointCoordinateDemo().setVisible(1300, 900);
  }
}
