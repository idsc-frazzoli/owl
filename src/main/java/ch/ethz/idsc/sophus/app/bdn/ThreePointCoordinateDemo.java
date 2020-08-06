// code by jph
package ch.ethz.idsc.sophus.app.bdn;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;

import ch.ethz.idsc.java.awt.SpinnerListener;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.ArrayPlotRender;
import ch.ethz.idsc.sophus.app.api.GeodesicArrayPlot;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.H2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.LogWeighting;
import ch.ethz.idsc.sophus.app.api.R2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.S2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.ThreePointCoordinates;
import ch.ethz.idsc.sophus.hs.Biinvariants;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.ArrayReshape;
import ch.ethz.idsc.tensor.alg.ConstantArray;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Drop;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

/** transfer weights from barycentric coordinates defined by set of control points
 * in the square domain (subset of R^2) to means in non-linear spaces */
/* package */ class ThreePointCoordinateDemo extends A2ScatteredSetCoordinateDemo //
    implements SpinnerListener<GeodesicDisplay> {
  private static final int REFINEMENT = 160; // presentation 60
  private static final Tensor DOMAIN = Drop.tail(Subdivide.of(0.0, 1.0, 10), 1);

  public ThreePointCoordinateDemo() {
    super(ThreePointCoordinates.list());
    // ---
    GeodesicDisplay geodesicDisplay = R2GeodesicDisplay.INSTANCE;
    actionPerformed(geodesicDisplay);
    addSpinnerListener(this);
    addSpinnerListener(l -> recompute());
    recompute();
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    super.render(geometricLayer, graphics);
    // ---
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor sequence = getGeodesicControlPoints();
    Tensor all = Tensors.empty();
    for (int index = 0; index < sequence.length(); ++index) {
      Tensor prev = sequence.get(Math.floorMod(index - 1, sequence.length()));
      Tensor next = sequence.get(index);
      DOMAIN.map(geodesicDisplay.geodesicInterface().curve(prev, next)).stream() //
          .map(geodesicDisplay::toPoint) //
          .forEach(all::append);
    }
    Path2D path2d = geometricLayer.toPath2D(all);
    graphics.setColor(new Color(192, 192, 128, 64));
    graphics.fill(path2d);
    graphics.setColor(new Color(192, 192, 128, 192));
    graphics.draw(path2d);
  }

  @Override
  public void actionPerformed(ActionEvent actionEvent) {
    File root = HomeDirectory.Pictures( //
        getClass().getSimpleName(), //
        geodesicDisplay().toString());
    root.mkdirs();
    for (LogWeighting logWeighting : ThreePointCoordinates.list()) {
      Tensor sequence = getGeodesicControlPoints();
      TensorUnaryOperator tensorUnaryOperator = logWeighting.operator( //
          Biinvariants.METRIC, //
          geodesicDisplay().vectorLogManifold(), //
          variogram(), //
          sequence);
      System.out.print("computing " + logWeighting);
      GeodesicArrayPlot geodesicArrayPlot = geodesicDisplay().geodesicArrayPlot();
      Tensor fallback = ConstantArray.of(DoubleScalar.INDETERMINATE, sequence.length());
      Tensor wgs = geodesicArrayPlot.raster(REFINEMENT, tensorUnaryOperator, fallback);
      List<Integer> dims = Dimensions.of(wgs);
      Tensor _wgp = ArrayReshape.of(Transpose.of(wgs, 0, 2, 1), dims.get(0), dims.get(1) * dims.get(2));
      ArrayPlotRender arrayPlotRender = ArrayPlotRender.rescale(_wgp, colorDataGradient(), 1);
      BufferedImage bufferedImage = arrayPlotRender.export();
      try {
        File file = new File(root, logWeighting.toString() + ".png");
        ImageIO.write(bufferedImage, "png", file);
      } catch (Exception exception) {
        exception.printStackTrace();
      }
      System.out.println(" done");
    }
    System.out.println("all done");
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
          "{{-0.892, -0.300, 0.000}, {-0.583, 0.733, 0.000}, {0.800, 0.325, 0.000}, {0.817, -0.233, 0.000}, {-0.008, -0.808, 0.000}}"));
    }
  }

  public static void main(String[] args) {
    new ThreePointCoordinateDemo().setVisible(1300, 900);
  }
}
