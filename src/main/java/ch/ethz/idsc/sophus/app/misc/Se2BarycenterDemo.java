// code by jph
package ch.ethz.idsc.sophus.app.misc;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.bm.BiinvariantMean;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gds.ManifoldDisplay;
import ch.ethz.idsc.sophus.gui.win.ControlPointsDemo;
import ch.ethz.idsc.sophus.gui.win.DubinsGenerator;
import ch.ethz.idsc.sophus.math.Geodesic;
import ch.ethz.idsc.sophus.ply.Arrowhead;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.RotateRight;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.api.ScalarTensorFunction;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.red.Total;

/* package */ class Se2BarycenterDemo extends ControlPointsDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED_DRAW = ColorDataLists._097.cyclic().deriveWithAlpha(192);
  private static final ColorDataIndexed COLOR_DATA_INDEXED_FILL = ColorDataLists._097.cyclic().deriveWithAlpha(182);
  // ---
  private final JToggleButton axes = new JToggleButton("axes");

  public Se2BarycenterDemo() {
    super(false, GeodesicDisplays.SE2C_SE2);
    timerFrame.jToolBar.add(axes);
    Tensor tensor = DubinsGenerator.of(Tensors.vector(0, 0, 0), Tensors.fromString("{{5, 0, -1}}")) //
        .append(Tensors.vector(0, -1, 0)) //
        .append(Tensors.vector(0, 0, Math.PI / 7));
    setControlPointsSe2(tensor);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (axes.isSelected())
      AxesRender.INSTANCE.render(geometricLayer, graphics);
    RenderQuality.setQuality(graphics);
    renderControlPoints(geometricLayer, graphics);
    Tensor sequence = getControlPointsSe2();
    if (sequence.length() == 4)
      try {
        ManifoldDisplay geodesicDisplay = manifoldDisplay();
        // ---
        Geodesic geodesicInterface = geodesicDisplay.geodesicInterface();
        final ScalarTensorFunction curve = geodesicInterface.curve(sequence.get(0), sequence.get(1));
        {
          Tensor tensor = Subdivide.of(-0.5, 1.5, 55).map(curve);
          Path2D path2d = geometricLayer.toPath2D(Tensor.of(tensor.stream().map(geodesicDisplay::toPoint)));
          graphics.setColor(Color.BLUE);
          graphics.draw(path2d);
        }
        // ---
        BiinvariantMean biinvariantMean = geodesicDisplay.biinvariantMean();
        Tensor tX = Subdivide.of(-1, 1, 20);
        Tensor tY = Subdivide.of(-1, 1, 8);
        int n = tY.length();
        Tensor[][] array = new Tensor[tX.length()][tY.length()];
        {
          int c0 = 0;
          for (Tensor x : tX) {
            int c1 = 0;
            for (Tensor y : tY) {
              Scalar w = RationalScalar.HALF;
              Tensor weights = Tensors.of(w, x, y);
              weights.append(RealScalar.ONE.subtract(Total.ofVector(weights)));
              weights = RotateRight.of(weights, 1);
              Tensor mean = biinvariantMean.mean(sequence, weights);
              array[c0][c1] = mean;
              ++c1;
            }
            ++c0;
          }
        }
        // ---
        graphics.setColor(Color.LIGHT_GRAY);
        for (int c0 = 0; c0 < array.length; ++c0)
          for (int c1 = 1; c1 < n; ++c1)
            graphics.draw(geometricLayer.toPath2D(Tensors.of(array[c0][c1 - 1], array[c0][c1])));
        for (int c0 = 1; c0 < array.length; ++c0)
          for (int c1 = 0; c1 < n; ++c1)
            graphics.draw(geometricLayer.toPath2D(Tensors.of(array[c0 - 1][c1], array[c0][c1])));
        // ---
        for (int c0 = 0; c0 < array.length; ++c0)
          for (int c1 = 0; c1 < n; ++c1) {
            Tensor mean = array[c0][c1];
            geometricLayer.pushMatrix(geodesicDisplay.matrixLift(mean));
            Path2D path2d = geometricLayer.toPath2D(Arrowhead.of(0.1));
            path2d.closePath();
            graphics.fill(path2d);
            geometricLayer.popMatrix();
          }
        // ---
        {
          geometricLayer.pushMatrix(geodesicDisplay.matrixLift(curve.apply(RationalScalar.HALF)));
          Path2D path2d = geometricLayer.toPath2D(Arrowhead.of(0.5));
          path2d.closePath();
          graphics.setColor(COLOR_DATA_INDEXED_FILL.getColor(0));
          graphics.fill(path2d);
          graphics.setColor(COLOR_DATA_INDEXED_DRAW.getColor(0));
          graphics.draw(path2d);
          geometricLayer.popMatrix();
        }
      } catch (Exception exception) {
        exception.printStackTrace();
      }
  }

  public static void main(String[] args) {
    new Se2BarycenterDemo().setVisible(1200, 600);
  }
}
