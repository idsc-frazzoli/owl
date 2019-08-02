// code by jph
package ch.ethz.idsc.owl.bot.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

import javax.swing.WindowConstants;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.region.ImageRender;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.AffineTransforms;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.gui.win.TimerFrame;
import ch.ethz.idsc.sophus.app.util.LazyMouse;
import ch.ethz.idsc.sophus.app.util.LazyMouseListener;
import ch.ethz.idsc.sophus.lie.se2.Se2Utils;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ class ErodedMapDemo {
  private final TimerFrame timerFrame1 = new TimerFrame();
  private final TimerFrame timerFrame2 = new TimerFrame();

  public ErodedMapDemo() {
    BufferedImage bufferedImage = new BufferedImage(64, 48, BufferedImage.TYPE_BYTE_GRAY);
    Tensor //
    matrix = Tensors.fromString("{{0.1, 0, 3}, {0, 0.1, 1}, {0, 0, 1}}") //
        .dot(Se2Utils.toSE2Matrix(Tensors.vector(0, 0, 0.3)));
    // matrix = IdentityMatrix.of(3);
    EroMap eroMap = new EroMap(bufferedImage, matrix, 3);
    // GeometricLayer geometricLayer = GeometricLayer.of(Inverse.of(matrix));
    timerFrame1.geometricComponent.addRenderInterface(new ImageRender(bufferedImage, matrix, true));
    LazyMouseListener lazyMouseListener = new LazyMouseListener() {
      @Override
      public void lazyClicked(MouseEvent mouseEvent) {
        Tensor tensor = timerFrame1.geometricComponent.getMouseSe2State();
        System.out.println(tensor);
        eroMap.setPixel(tensor, mouseEvent.getButton() <= 1);
      }

      @Override
      public void lazyDragged(MouseEvent mouseEvent) {
        System.out.println("drag " + mouseEvent.getButton());
        lazyClicked(mouseEvent);
      }
    };
    new LazyMouse(lazyMouseListener).addListenersTo(timerFrame1.geometricComponent.jComponent);
    timerFrame1.geometricComponent.addRenderInterfaceBackground(AxesRender.INSTANCE);
    timerFrame1.configCoordinateOffset(10, 500);
    timerFrame1.jFrame.setBounds(100, 100, 600, 600);
    timerFrame1.jFrame.setVisible(true);
    timerFrame1.jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    // ---
    timerFrame2.geometricComponent.addRenderInterface(new RenderInterface() {
      @Override
      public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
        geometricLayer.pushMatrix(matrix);
        graphics.drawImage(eroMap.updateErodedMap(), //
            AffineTransforms.toAffineTransform(geometricLayer.getMatrix()), null);
        graphics.setColor(Color.RED);
        Path2D path2d = geometricLayer.toPath2D(Tensors.of( //
            Tensors.vector(0, 0), //
            Tensors.vector(bufferedImage.getWidth(), 0), //
            Tensors.vector(bufferedImage.getWidth(), bufferedImage.getHeight()), //
            Tensors.vector(0, bufferedImage.getHeight())), true);
        graphics.draw(path2d);
        geometricLayer.popMatrix();
      }
    });
    timerFrame2.geometricComponent.addRenderInterfaceBackground(AxesRender.INSTANCE);
    timerFrame2.configCoordinateOffset(10, 500);
    timerFrame2.jFrame.setBounds(800, 100, 600, 600);
    timerFrame2.jFrame.setVisible(true);
    timerFrame2.jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  }

  public static void main(String[] args) {
    new ErodedMapDemo();
  }
}
