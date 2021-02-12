// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import javax.swing.JTextField;

import ch.ethz.idsc.java.awt.RenderQuality;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.gui.win.AbstractDemo;
import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.ext.Integers;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.itp.DeBoor;

/* package */ class DeBoorCustomDemo extends AbstractDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED = ColorDataLists._097.cyclic().deriveWithAlpha(192);
  // ---
  private final JTextField jTextField = new JTextField(30);

  public DeBoorCustomDemo() {
    jTextField.setPreferredSize(new Dimension(200, 28));
    jTextField.setText("{0, 1}");
    timerFrame.jToolBar.add(jTextField);
    // ---
    timerFrame.geometricComponent.addRenderInterface(AxesRender.INSTANCE);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    RenderQuality.setQuality(graphics);
    graphics.setStroke(new BasicStroke(1.25f));
    {
      graphics.setFont(new Font(Font.DIALOG, Font.PLAIN, 10));
      try {
        Tensor domain = Subdivide.of(0, 1, 100);
        Tensor domahi = Subdivide.of(1, 2, 100);
        Tensor knots = Tensors.fromString(jTextField.getText());
        if (Integers.isEven(knots.length())) {
          int degree = knots.length() >> 1;
          int length = degree + 1;
          // ---
          graphics.setColor(Color.LIGHT_GRAY);
          {
            Path2D path2d = geometricLayer.toPath2D(Tensors.fromString("{{0, 1}, {0, 0}, {1, 0}}"));
            graphics.setStroke(new BasicStroke(2f));
            graphics.setColor(Color.RED);
            graphics.draw(path2d);
          }
          for (int k_th = 0; k_th < length; ++k_th) {
            graphics.setColor(COLOR_DATA_INDEXED.getColor(k_th));
            DeBoor deBoor = DeBoor.of(RnGeodesic.INSTANCE, knots, UnitVector.of(length, k_th));
            {
              graphics.setStroke(new BasicStroke(1.25f));
              Tensor values = domain.map(deBoor);
              Tensor tensor = Transpose.of(Tensors.of(domain, values));
              graphics.draw(geometricLayer.toPath2D(tensor));
            }
            {
              graphics.setStroke(new BasicStroke(1.25f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0));
              Tensor values = domahi.map(deBoor);
              Tensor tensor = Transpose.of(Tensors.of(domahi, values));
              graphics.draw(geometricLayer.toPath2D(tensor));
            }
          }
        }
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }
    graphics.setStroke(new BasicStroke(1f));
  }

  public static void main(String[] args) {
    new DeBoorCustomDemo().setVisible(1000, 800);
  }
}
