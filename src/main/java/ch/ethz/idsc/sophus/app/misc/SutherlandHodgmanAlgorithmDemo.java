// code by jph
package ch.ethz.idsc.sophus.app.misc;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.JToggleButton;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.lev.AbstractPlaceDemo;
import ch.ethz.idsc.sophus.app.lev.LeversRender;
import ch.ethz.idsc.sophus.gds.GeodesicDisplays;
import ch.ethz.idsc.sophus.gui.ren.PathRender;
import ch.ethz.idsc.sophus.gui.ren.PointsRender;
import ch.ethz.idsc.sophus.hs.r2.Se2Bijection;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.ply.PolygonCentroid;
import ch.ethz.idsc.sophus.ply.SutherlandHodgmanAlgorithm;
import ch.ethz.idsc.sophus.ply.SutherlandHodgmanAlgorithm.PolyclipResult;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.io.ScalarArray;
import ch.ethz.idsc.tensor.lie.Cross;
import ch.ethz.idsc.tensor.lie.r2.CirclePoints;
import ch.ethz.idsc.tensor.red.Mean;

/* package */ class SutherlandHodgmanAlgorithmDemo extends AbstractPlaceDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.strict();
  private static final Tensor CIRCLE = CirclePoints.of(7).multiply(RealScalar.of(2));
  private static final SutherlandHodgmanAlgorithm POLYGON_CLIP = SutherlandHodgmanAlgorithm.of(CIRCLE);
  // ---
  private final JToggleButton jToggleButton = new JToggleButton("move");

  public SutherlandHodgmanAlgorithmDemo() {
    super(true, GeodesicDisplays.R2_ONLY);
    timerFrame.jToolBar.add(jToggleButton);
    setControlPointsSe2(Tensor.of(CirclePoints.of(4).stream().map(row -> row.append(RealScalar.ZERO))));
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    boolean isMoving = jToggleButton.isSelected();
    setPositioningEnabled(!isMoving);
    RenderQuality.setQuality(graphics);
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    if (isMoving) {
      Se2Bijection se2Bijection = new Se2Bijection(geometricLayer.getMouseSe2State().pmul(Tensors.vector(1, 1, 0.3)));
      Tensor sequence = Tensor.of(getGeodesicControlPoints().stream().map(se2Bijection.forward()));
      new PathRender(COLOR_DATA_INDEXED.getColor(0), 1.5f).setCurve(sequence, true).render(geometricLayer, graphics);
      new PathRender(COLOR_DATA_INDEXED.getColor(3), 1.5f).setCurve(CIRCLE, true).render(geometricLayer, graphics);
      PolyclipResult polyclipResult = POLYGON_CLIP.apply(sequence);
      graphics.setColor(new Color(128, 255, 128, 128));
      Tensor result = polyclipResult.tensor();
      graphics.fill(geometricLayer.toPath2D(result));
      new PathRender(COLOR_DATA_INDEXED.getColor(1), 3.5f).setCurve(result, true).render(geometricLayer, graphics);
      {
        for (int index = 0; index < result.length(); ++index) {
          int cind = polyclipResult.belong().Get(index).number().intValue();
          Color color = COLOR_DATA_INDEXED.getColor(cind);
          PointsRender pointsRender = new PointsRender(color, Color.BLACK);
          pointsRender.show( //
              manifoldDisplay()::matrixLift, //
              manifoldDisplay().shape().multiply(RealScalar.of(2)), //
              Tensors.of(result.get(index))) //
              .render(geometricLayer, graphics);
        }
      }
      Tensor nsum = Array.zeros(2);
      {
        graphics.setColor(Color.DARK_GRAY);
        Scalar[] prop = ScalarArray.ofVector(polyclipResult.belong());
        Tensor[] array = result.stream().toArray(Tensor[]::new);
        for (int index = 0; index < result.length(); ++index) {
          int iprev = Math.floorMod(index - 1, result.length());
          Tensor a = array[iprev];
          Tensor b = array[index];
          int ap = prop[iprev].number().intValue();
          int bp = prop[index].number().intValue();
          Tensor point = Mean.of(Tensors.of(a, b));
          Tensor norma = Cross.of(b.subtract(a)).multiply(RealScalar.of(0.3));
          if (ap == 1 && bp == 1)
            norma = norma.negate();
          nsum = nsum.add(norma);
          geometricLayer.pushMatrix(Se2Matrix.translation(point));
          graphics.draw(geometricLayer.toLine2D(norma));
          geometricLayer.popMatrix();
        }
      }
      if (0 < result.length()) {
        Tensor centroid = PolygonCentroid.of(result);
        geometricLayer.pushMatrix(Se2Matrix.translation(centroid));
        graphics.setColor(Color.BLACK);
        graphics.setStroke(new BasicStroke(2f));
        graphics.draw(geometricLayer.toLine2D(nsum));
        geometricLayer.popMatrix();
      }
      LeversRender leversRender = LeversRender.of(manifoldDisplay(), result, null, geometricLayer, graphics);
      leversRender.renderIndexP();
    } else {
      Tensor sequence = getGeodesicControlPoints();
      new PathRender(COLOR_DATA_INDEXED.getColor(0), 1.5f).setCurve(sequence, true).render(geometricLayer, graphics);
      renderControlPoints(geometricLayer, graphics);
      LeversRender leversRender = LeversRender.of(manifoldDisplay(), sequence, null, geometricLayer, graphics);
      leversRender.renderIndexP();
    }
    // RenderQuality.setDefault(graphics);
    // new PathRender(COLOR_DATA_INDEXED.getColor(1), 2.5f).setCurve(HILBERT, false).render(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    new SutherlandHodgmanAlgorithmDemo().setVisible(1000, 600);
  }
}
