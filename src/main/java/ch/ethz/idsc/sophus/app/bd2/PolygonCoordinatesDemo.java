// code by jph
package ch.ethz.idsc.sophus.app.bd2;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import ch.ethz.idsc.java.awt.SpinnerListener;
import ch.ethz.idsc.java.io.HtmlUtf8;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.lev.LeversRender;
import ch.ethz.idsc.sophus.gds.H2Display;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gds.R2Display;
import ch.ethz.idsc.sophus.gds.S2Display;
import ch.ethz.idsc.sophus.gui.ren.ArrayPlotRender;
import ch.ethz.idsc.sophus.opt.LogWeighting;
import ch.ethz.idsc.sophus.opt.PolygonCoordinates;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.api.TensorUnaryOperator;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;

/** transfer weights from barycentric coordinates defined by set of control points
 * in the square domain (subset of R^2) to means in non-linear spaces */
/* package */ class PolygonCoordinatesDemo extends A2ScatteredSetCoordinateDemo //
    implements SpinnerListener<ManifoldDisplay> {
  public static final ColorDataIndexed COLOR_DATA_INDEXED = HueColorData.of(6, 3);
  private final List<LogWeighting> array;

  public PolygonCoordinatesDemo() {
    this(PolygonCoordinates.list());
  }

  public PolygonCoordinatesDemo(List<LogWeighting> array) {
    super(array);
    this.array = array;
    // ---
    ManifoldDisplay geodesicDisplay = R2Display.INSTANCE;
    actionPerformed(geodesicDisplay);
    addSpinnerListener(this);
    addSpinnerListener(l -> recompute());
    recompute();
    timerFrame.geometricComponent.addRenderInterfaceBackground(AxesRender.INSTANCE);
  }

  @Override
  public final void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    super.render(geometricLayer, graphics);
    // ---
    LeversRender leversRender = LeversRender.of( //
        manifoldDisplay(), getGeodesicControlPoints(), null, geometricLayer, graphics);
    leversRender.renderSurfaceP();
    // BufferedImage bufferedImage = levelsImage(refinement());
    // graphics.drawImage(bufferedImage, 0, 200, bufferedImage.getWidth() * magnification(), bufferedImage.getHeight() * magnification(), null);
  }

  @Override
  public final void actionPerformed(ActionEvent actionEvent) {
    ManifoldDisplay geodesicDisplay = manifoldDisplay();
    File root = HomeDirectory.Pictures( //
        getClass().getSimpleName(), //
        geodesicDisplay.toString());
    root.mkdirs();
    try (HtmlUtf8 htmlUtf8 = HtmlUtf8.page(new File(root, "index.html"))) {
      final Tensor sequence = getGeodesicControlPoints();
      {
        System.out.println("computing levels image");
        BufferedImage bufferedImage = HilbertLevelImage.of(manifoldDisplay(), sequence, resolution(), colorDataGradient(), 32);
        try {
          File file = new File(root, "levels.png");
          ImageIO.write(bufferedImage, "png", file);
          htmlUtf8.appendln("min K until non-negative<br/>");
          String collect = Stream.of(IterativeGenesis.values()).map(Object::toString).collect(Collectors.joining(" | "));
          htmlUtf8.appendln(collect + ":<br/>");
          htmlUtf8.appendln("<img src='" + file.getName() + "'><br/>");
          htmlUtf8.appendln("<hr/>");
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
      for (LogWeighting logWeighting : array) {
        TensorUnaryOperator tensorUnaryOperator = logWeighting.operator( //
            null, geodesicDisplay.hsManifold(), null, sequence);
        System.out.print("computing " + logWeighting);
        // GeodesicArrayPlot geodesicArrayPlot = geodesicDisplay.geodesicArrayPlot();
        int refinement = resolution();
        try {
          ArrayPlotRender arrayPlotRender = arrayPlotRender(sequence, refinement, tensorUnaryOperator, 1);
          BufferedImage bufferedImage = StaticHelper.fuseImages(geodesicDisplay, arrayPlotRender, refinement, sequence.length());
          File file = new File(root, logWeighting.toString() + ".png");
          ImageIO.write(bufferedImage, "png", file);
          htmlUtf8.appendln(logWeighting.toString() + "<br/>");
          htmlUtf8.appendln("<img src='" + file.getName() + "'><br/>");
          htmlUtf8.appendln("<hr/>");
        } catch (Exception exception) {
          exception.printStackTrace();
        }
        System.out.println(" done");
      }
    }
    System.out.println("all done");
  }

  int resolution() {
    return 120; // for sequence of length 6
  }

  @Override
  public void actionPerformed(ManifoldDisplay geodesicDisplay) {
    if (geodesicDisplay instanceof R2Display) {
      setControlPointsSe2(Tensors.fromString( //
          "{{-0.076, -0.851, 0.000}, {-0.300, -0.992, 0.000}, {-0.689, -0.097, 0.000}, {-0.689, -0.892, 0.000}, {-1.017, -0.953, 0.000}, {-0.991, 0.113, 0.000}, {-0.465, 0.157, 0.000}, {-0.164, -0.362, 0.000}, {0.431, -0.539, 0.000}, {-0.912, 0.669, 0.000}, {-0.644, 0.967, 0.000}, {0.509, 0.840, 0.000}, {1.051, 0.495, 0.000}, {0.950, -0.209, 0.000}, {0.747, 0.469, 0.000}, {-0.461, 0.637, 0.000}, {0.956, -0.627, 0.000}}"));
    } else //
    if (geodesicDisplay instanceof H2Display) {
      setControlPointsSe2(Tensors.fromString( //
          "{{-2.900, 2.467, 0.000}, {-0.367, 2.550, 0.000}, {-0.450, 0.400, 0.000}, {-1.533, 0.250, 0.000}, {-0.600, -0.567, 0.000}, {0.250, 2.867, 0.000}, {0.400, -0.683, 0.000}, {0.867, -1.067, 0.000}, {1.450, 2.800, 0.000}, {2.300, 2.117, 0.000}, {2.700, 0.317, 0.000}, {2.183, -0.517, 0.000}, {1.183, 0.167, 0.000}, {1.683, -1.767, 0.000}, {1.600, -2.583, 0.000}, {-0.800, -2.650, 0.000}, {-2.650, -1.900, 0.000}, {-2.917, 0.550, 0.000}}"));
    } else //
    if (geodesicDisplay instanceof S2Display) {
      setControlPointsSe2(Tensors.fromString( //
          "{{-0.933, -0.325, 0.000}, {-0.708, 0.500, 0.000}, {-0.262, 0.592, 0.000}, {-0.621, 0.746, 0.000}, {-0.375, 0.879, 0.000}, {0.079, 0.979, 0.000}, {0.700, 0.567, 0.000}, {0.096, 0.775, 0.000}, {-0.233, 0.833, 0.000}, {-0.004, 0.646, 0.000}, {0.733, 0.455, 0.000}, {0.942, 0.242, 0.000}, {0.033, 0.371, 0.000}, {-0.522, 0.372, 0.000}, {-0.808, 0.042, 0.000}, {-0.192, -0.158, 0.000}, {-0.634, -0.188, 0.000}, {0.014, -0.459, 0.000}, {-0.169, 0.260, 0.000}, {0.916, 0.142, 0.000}, {0.792, -0.465, 0.000}, {0.408, -0.200, 0.000}, {0.480, 0.054, 0.000}, {0.121, -0.008, 0.000}, {0.462, -0.800, 0.000}, {0.067, -0.712, 0.000}, {-0.321, -0.621, 0.000}, {0.233, -0.933, 0.000}, {-0.071, -0.975, 0.000}, {-0.200, -0.846, 0.000}, {-0.550, -0.737, 0.000}}"));
    }
  }

  public static void main(String[] args) {
    new PolygonCoordinatesDemo().setVisible(1300, 900);
  }
}
