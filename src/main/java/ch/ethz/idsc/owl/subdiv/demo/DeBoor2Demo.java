// code by jph
package ch.ethz.idsc.owl.subdiv.demo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Arrays;

import javax.swing.JTextField;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.gui.win.TimerFrame;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.alg.UnitVector;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.opt.DeBoor;

/* package */ class DeBoor2Demo {
  private final TimerFrame timerFrame = new TimerFrame();

  DeBoor2Demo() {
    timerFrame.jFrame.setTitle(getClass().getSimpleName());
    SpinnerLabel<Integer> spinnerDegree = new SpinnerLabel<>();
    JTextField jTextField = new JTextField(30);
    jTextField.setPreferredSize(new Dimension(200, 28));
    jTextField.setText("{0, 1}");
    timerFrame.jToolBar.add(jTextField);
    // ---
    timerFrame.geometricComponent.addRenderInterface(new RenderInterface() {
      @Override
      public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
        GraphicsUtil.setQualityHigh(graphics);
        graphics.setStroke(new BasicStroke(1.25f));
        geometricLayer.pushMatrix(DiagonalMatrix.of(3, 3, 1));
        {
          ColorDataIndexed cyclic = ColorDataLists._097.cyclic().deriveWithAlpha(192);
          graphics.setFont(new Font(Font.DIALOG, Font.PLAIN, 10));
          // control length
          try {
            Tensor domain = Subdivide.of(0, 1, 100);
            Tensor domahi = Subdivide.of(1, 2, 100);
            Tensor knots = Tensors.fromString(jTextField.getText());
            if (knots.length() % 2 == 0) {
              int degree = knots.length() >> 1;
              int length = degree + 1;
              //
              graphics.setColor(Color.LIGHT_GRAY);
              {
                Path2D path2d = geometricLayer.toPath2D(Tensors.fromString("{{0,1},{0,0},{1,0}}"));
                graphics.draw(path2d);
              }
              for (int k_th = 0; k_th < length; ++k_th) {
                graphics.setColor(cyclic.getColor(k_th));
                DeBoor deBoor = DeBoor.of(knots, UnitVector.of(length, k_th));
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
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
        geometricLayer.popMatrix();
        graphics.setStroke(new BasicStroke(1f));
      }
    });
    {
      spinnerDegree.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6));
      spinnerDegree.setValue(1);
      spinnerDegree.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "degree");
    }
  }

  public static void main(String[] args) {
    DeBoor2Demo deBoor2Demo = new DeBoor2Demo();
    deBoor2Demo.timerFrame.jFrame.setBounds(100, 100, 1000, 800);
    deBoor2Demo.timerFrame.jFrame.setVisible(true);
  }
}
