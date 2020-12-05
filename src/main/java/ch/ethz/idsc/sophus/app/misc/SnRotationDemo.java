// code by jph
package ch.ethz.idsc.sophus.app.misc;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.img.ColorDataGradients;

public class SnRotationDemo extends AbstractDemo {
  private final JButton jButton = new JButton("shuffle");
  private final List<SnRotationChunk> list = new ArrayList<>();

  public SnRotationDemo() {
    // jButton.addActionListener(a -> setDimension(DIM));
    timerFrame.jToolBar.add(jButton);
    // setDimension(DIM);
    list.add(new SnRotationChunk(3, 200, 3, 0.3, ColorDataGradients.PARULA.deriveWithOpacity(RealScalar.of(0.3))));
    // list.add(new SnRotationChunk(3, 50, 20, 0.02, ColorDataGradients.SOLAR.deriveWithOpacity(RealScalar.of(0.5))));
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    // graphics.setColor(new Color(128, 128, 128, 64));
    list.forEach(SnRotationChunk::integrate);
    graphics.setStroke(new BasicStroke(1.5f));
    list.get(0).render(geometricLayer, graphics);
    // graphics.setStroke(new BasicStroke(2.5f));
    // list.get(1).render(geometricLayer, graphics);
  }

  public static void main(String[] args) {
    SnRotationDemo snRotationDemo = new SnRotationDemo();
    snRotationDemo.setVisible(800, 600);
  }
}
