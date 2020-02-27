// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.GraphicsUtil;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.S2GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.SnBarycentricCoordinates;
import ch.ethz.idsc.sophus.hs.sn.SnBiinvariantCoordinate;
import ch.ethz.idsc.sophus.hs.sn.SnInverseDistanceCoordinate;
import ch.ethz.idsc.sophus.math.win.BarycentricCoordinate;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.ArrayReshape;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/** transfer weights from barycentric coordinates defined by set of control points
 * in the square domain (subset of R^2) to means in non-linear spaces */
/* package */ class S2ScatteredSetCoordinateDemo extends ScatteredSetCoordinateDemo implements ActionListener {
  private final JToggleButton jToggleLower = new JToggleButton("lower");
  private final JToggleButton jToggleAxes = new JToggleButton("axes");
  private final JButton jButtonExport = new JButton("export");

  public S2ScatteredSetCoordinateDemo() {
    super(true, GeodesicDisplays.S2_ONLY, SnBarycentricCoordinates.values());
    {
      jToggleLower.setSelected(true);
      timerFrame.jToolBar.add(jToggleLower);
    }
    {
      jToggleAxes.setSelected(true);
      timerFrame.jToolBar.add(jToggleAxes);
    }
    {
      jButtonExport.addActionListener(this);
      timerFrame.jToolBar.add(jButtonExport);
    }
    setControlPointsSe2(Tensors.fromString("{{-0.5, 0.3, 0}, {0.3, 0.5, 0}, {-0.4, -0.3, 0}, {0.2, -0.3, 0}}"));
    setMidpointIndicated(false);
    Tensor model2pixel = timerFrame.geometricComponent.getModel2Pixel();
    timerFrame.geometricComponent.setModel2Pixel(Tensors.vector(5, 5, 1).pmul(model2pixel));
    timerFrame.configCoordinateOffset(500, 500);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (jToggleAxes.isSelected())
      AxesRender.INSTANCE.render(geometricLayer, graphics);
    ColorDataGradient colorDataGradient = colorDataGradient();
    renderControlPoints(geometricLayer, graphics);
    graphics.setFont(ArrayPlotRender.FONT);
    graphics.setColor(Color.BLACK);
    graphics.drawString("" + spinnerBarycentric.getValue(), 0, 10 + 17);
    final Tensor origin = getGeodesicControlPoints();
    S2GeodesicDisplay s2GeodesicDisplay = (S2GeodesicDisplay) geodesicDisplay();
    for (int index = 0; index < origin.length(); ++index) {
      Tensor xyz = origin.get(index);
      geometricLayer.pushMatrix(s2GeodesicDisplay.matrixLift(xyz));
      Point2D point2d = geometricLayer.toPoint2D(0, 0);
      graphics.drawString("  " + index, (int) point2d.getX(), (int) point2d.getY());
      geometricLayer.popMatrix();
    }
    // ---
    if (s2GeodesicDisplay.dimensions() < origin.length()) {
      // Tensor sX = Subdivide.of(-1.0, +1.0, refinement());
      // Tensor sY = Subdivide.of(-1.0, +1.0, refinement());
      // int n = sX.length();
      // boolean lower = jToggleLower.isSelected();
      // BarycentricCoordinate barycentricCoordinate = barycentricCoordinate();
      // Tensor wgs = Array.of(l -> DoubleScalar.INDETERMINATE, lower ? n * 2 : n, n, origin.length());
      // IntStream.range(0, n).parallel().forEach(c0 -> {
      // Scalar x = sX.Get(c0);
      // int c1 = 0;
      // for (Tensor y : sY) {
      // Optional<Tensor> optionalP = S2GeodesicDisplay.optionalZ(Tensors.of(x, y, RealScalar.ONE));
      // if (optionalP.isPresent()) {
      // Tensor point = optionalP.get();
      // wgs.set(barycentricCoordinate.weights(origin, point), n - c1 - 1, c0);
      // if (lower) {
      // point.set(Scalar::negate, 2);
      // wgs.set(barycentricCoordinate.weights(origin, point), n + n - c1 - 1, c0);
      // }
      // }
      // ++c1;
      // }
      // });
      // ---
      // if (jToggleHeatmap.isSelected())
      { // render basis functions
        Tensor wgs = compute(barycentricCoordinate(), refinement());
        List<Integer> dims = Dimensions.of(wgs);
        Tensor _wgp = ArrayReshape.of(Transpose.of(wgs, 0, 2, 1), dims.get(0), dims.get(1) * dims.get(2));
        GraphicsUtil.setQualityHigh(graphics);
        new ArrayPlotRender(_wgp, colorDataGradient, 0, 32, magnification()).render(geometricLayer, graphics);
      }
    }
  }

  @Override
  public void released() {
    System.out.println("RELEASED");
  }

  public Tensor compute(BarycentricCoordinate barycentricCoordinate, int refinement) {
    Tensor sX = Subdivide.of(-1.0, +1.0, refinement);
    Tensor sY = Subdivide.of(-1.0, +1.0, refinement);
    int n = sX.length();
    boolean lower = jToggleLower.isSelected();
    final Tensor origin = getGeodesicControlPoints();
    Tensor wgs = Array.of(l -> DoubleScalar.INDETERMINATE, lower ? n * 2 : n, n, origin.length());
    IntStream.range(0, n).parallel().forEach(c0 -> {
      Scalar x = sX.Get(c0);
      int c1 = 0;
      for (Tensor y : sY) {
        Optional<Tensor> optionalP = S2GeodesicDisplay.optionalZ(Tensors.of(x, y, RealScalar.ONE));
        if (optionalP.isPresent()) {
          Tensor point = optionalP.get();
          wgs.set(barycentricCoordinate.weights(origin, point), n - c1 - 1, c0);
          if (lower) {
            point.set(Scalar::negate, 2);
            wgs.set(barycentricCoordinate.weights(origin, point), n + n - c1 - 1, c0);
          }
        }
        ++c1;
      }
    });
    return wgs;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    BarycentricCoordinate[] bc = { SnInverseDistanceCoordinate.SQUARED, SnBiinvariantCoordinate.SQUARED };
    for (BarycentricCoordinate barycentricCoordinate : bc) {
      System.out.print("computing...");
      Tensor wgs = compute(barycentricCoordinate, 120);
      List<Integer> dims = Dimensions.of(wgs);
      Tensor _wgp = ArrayReshape.of(Transpose.of(wgs, 0, 2, 1), dims.get(0), dims.get(1) * dims.get(2));
      ArrayPlotRender arrayPlotRender = new ArrayPlotRender(_wgp, colorDataGradient(), 0, 0, 1);
      BufferedImage bufferedImage = arrayPlotRender.export();
      try {
        ImageIO.write(bufferedImage, "png", HomeDirectory.Pictures(barycentricCoordinate.toString() + ".png"));
      } catch (Exception exception) {
        exception.printStackTrace();
      }
      System.out.println("done");
    }
  }

  public static void main(String[] args) {
    new S2ScatteredSetCoordinateDemo().setVisible(1200, 900);
  }
}
