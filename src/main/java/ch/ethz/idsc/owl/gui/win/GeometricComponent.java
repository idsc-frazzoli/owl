// code by jph
package ch.ethz.idsc.owl.gui.win;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JComponent;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.mat.Det;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;
import ch.ethz.idsc.tensor.mat.LinearSolve;
import ch.ethz.idsc.tensor.sca.ArcTan;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Power;
import ch.ethz.idsc.tensor.sca.Round;

public final class GeometricComponent {
  private static final Font DEFAULT_FONT = new Font(Font.DIALOG, Font.PLAIN, 12);
  private static final double WHEEL_ANGLE = Math.PI / 12;
  /** initial model to pixel matrix */
  private static final Tensor MODEL2PIXEL_INITIAL = Tensors.matrix(new Number[][] { //
      { 60, 0, 300 }, //
      { 0, -60, 300 }, //
      { 0, 0, 1 }, //
  }).unmodifiable();
  /***************************************************/
  /** public access to final JComponent: attach mouse listeners, get/set properties, ... */
  public final JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics graphics) {
      render((Graphics2D) graphics, getSize());
      { // display frame rate only when rendering in component
        long period = System.nanoTime() - lastRepaint;
        lastRepaint = System.nanoTime();
        graphics.setFont(DEFAULT_FONT);
        graphics.setColor(Color.LIGHT_GRAY);
        graphics.drawString(String.format("%4.1f Hz", 1.0e9 / period), 0, 10);
      }
    }
  };
  // 3x3 affine matrix that maps model to pixel coordinates
  private Tensor model2pixel = MODEL2PIXEL_INITIAL.copy();
  private Tensor mouseLocation = Array.zeros(2);
  private final List<RenderInterface> renderBackground = new CopyOnWriteArrayList<>();
  private final List<RenderInterface> renderInterfaces = new CopyOnWriteArrayList<>();
  private long lastRepaint = System.nanoTime();
  private int mouseWheel = 0;
  private boolean isZoomable = true;
  private int buttonDrag = MouseEvent.BUTTON3;

  public GeometricComponent() {
    jComponent.addMouseWheelListener(event -> {
      final int delta = -event.getWheelRotation(); // either 1 or -1
      final int mods = event.getModifiersEx();
      final int mask = MouseWheelEvent.CTRL_DOWN_MASK; // 128 = 2^7
      if ((mods & mask) == 0) { // ctrl pressed?
        mouseWheel += delta;
      } else //
      if (isZoomable) {
        Scalar factor = Power.of(RealScalar.of(2), delta);
        Tensor scale = DiagonalMatrix.of(factor, factor, RealScalar.ONE);
        Tensor shift = Tensors.vector(event.getX(), event.getY());
        shift = shift.subtract(shift.multiply(factor));
        scale.set(shift.Get(0), 0, 2);
        scale.set(shift.Get(1), 1, 2);
        model2pixel = scale.dot(model2pixel);
      }
      jComponent.repaint();
    });
    {
      MouseInputListener mouseInputListener = new MouseInputAdapter() {
        private Point down = null;
        private Tensor center = null;

        @Override
        public void mouseMoved(MouseEvent mouseEvent) {
          mouseLocation = toModel(mouseEvent.getPoint());
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
          if (mouseEvent.getButton() == buttonDrag) {
            down = mouseEvent.getPoint();
            Dimension dimension = jComponent.getSize();
            center = toModel(new Point(dimension.width / 2, dimension.height / 2)).unmodifiable();
          }
        }

        @Override
        public void mouseDragged(MouseEvent mouseEvent) {
          if (Objects.nonNull(down)) {
            Point now = mouseEvent.getPoint();
            int dx = now.x - down.x;
            int dy = now.y - down.y;
            // ---
            Dimension dimension = jComponent.getSize();
            Scalar a1 = ArcTan.of(now.x - dimension.width / 2, now.y - dimension.height / 2);
            Scalar a2 = ArcTan.of(down.x - dimension.width / 2, down.y - dimension.height / 2);
            // ---
            down = now;
            final int mods = mouseEvent.getModifiersEx();
            final int mask = MouseWheelEvent.CTRL_DOWN_MASK; // 128 = 2^7
            if ((mods & mask) == 0) {
              model2pixel.set(scalar -> scalar.add(RealScalar.of(dx)), 0, 2);
              model2pixel.set(scalar -> scalar.add(RealScalar.of(dy)), 1, 2);
              // System.out.println(Pretty.of(model2pixel.map(Round._3)));
            } else {
              Tensor t1 = Se2Utils.toSE2Translation(center.negate());
              Tensor t2 = Se2Utils.toSE2Matrix(center.copy().append(a2.subtract(a1)));
              model2pixel = model2pixel.dot(t2).dot(t1);
            }
            jComponent.repaint();
          }
        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {
          down = null;
          center = null;
        }
      };
      jComponent.addMouseMotionListener(mouseInputListener);
      jComponent.addMouseListener(mouseInputListener);
    }
    {
      MouseListener mouseListener = new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent mouseEvent) {
          System.out.println(getMouseSe2State().map(Round._3));
        }
      };
      jComponent.addMouseListener(mouseListener);
    }
  }

  /** determines if mouse wheel + ctrl change magnification
   * 
   * @param isZoomable */
  public void setZoomable(boolean isZoomable) {
    this.isZoomable = isZoomable;
  }

  /** @param button for instance MouseEvent.BUTTON1 */
  public void setButtonDrag(int button) {
    buttonDrag = button;
  }

  /** function only clears render interfaces in the foreground.
   * the background is unchanged
   * 
   * @param collection */
  public void setRenderInterfaces(Collection<RenderInterface> collection) {
    renderInterfaces.clear();
    renderInterfaces.addAll(collection);
  }

  public void addRenderInterface(RenderInterface renderInterface) {
    renderInterfaces.add(renderInterface);
  }

  /** @return {px, py, angle} in model space */
  Tensor getMouseSe2State() {
    return mouseLocation.copy().append(RealScalar.of(mouseWheel * WHEEL_ANGLE));
  }

  public void addRenderInterfaceBackground(RenderInterface renderInterface) {
    renderBackground.add(renderInterface);
  }

  /***************************************************/
  /** @param model2pixel with dimensions 3 x 3 */
  public void setModel2Pixel(Tensor model2pixel) {
    Scalar det = Det.of(model2pixel);
    System.out.println(det);
    if (Chop._08.allZero(det))
      System.err.println("model2pixel must not be singular");
    else
      this.model2pixel = model2pixel.copy();
  }

  public Tensor getModel2Pixel() {
    return model2pixel.copy();
  }

  /** @param vector of length at least 2 */
  void setOffset(Tensor vector) {
    model2pixel.set(vector.Get(0), 0, 2);
    model2pixel.set(vector.Get(1), 1, 2);
  }

  void render(Graphics2D graphics, Dimension dimension) {
    graphics.setColor(Color.WHITE);
    graphics.fillRect(0, 0, dimension.width, dimension.height);
    // ---
    renderBackground.forEach(renderInterface -> renderInterface.render(createLayer(), graphics));
    renderInterfaces.forEach(renderInterface -> renderInterface.render(createLayer(), graphics));
  }

  /***************************************************/
  private GeometricLayer createLayer() {
    return new GeometricLayer(model2pixel, getMouseSe2State());
  }

  /** transforms point in pixel space to coordinates of model space
   * 
   * @param point
   * @return tensor of length 2 */
  private Tensor toModel(Point point) {
    return LinearSolve.of(model2pixel, Tensors.vector(point.x, point.y, 1)).extract(0, 2);
  }
}
