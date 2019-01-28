// code by jph
package ch.ethz.idsc.sophus.app.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

/** selector in gui for easy scrolling through a list with mouse-wheel but no pull-down menu
 * 
 * @param <Type> */
public class SpinnerLabel<Type> {
  private static final Color BACKGROUND_1 = new Color(248, 248, 248, 128);
  private static final Color BACKGROUND_0 = new Color(248, 248, 248, 64);
  private static final int BORDER_WIDTH_MIN = 9;
  private static final int BORDER_WIDTH_MAX = 16;
  // ---
  private boolean mouseInside = false;
  private Point lastMouse = new Point();
  private int border_width = 0;
  final List<SpinnerListener<Type>> spinnerListeners = new LinkedList<>();
  private final JLabel jLabel = new JLabel("", SwingConstants.RIGHT) {
    @Override
    protected void paintComponent(Graphics _graphics) {
      final boolean enabled = isEnabled();
      final boolean insideActive = mouseInside && enabled;
      Graphics2D graphics = (Graphics2D) _graphics;
      Dimension dimension = getSize(); // myJLabel.
      border_width = Math.min(Math.max(BORDER_WIDTH_MIN, BORDER_WIDTH_MIN - 2 + dimension.width / 10), BORDER_WIDTH_MAX);
      // ---
      if (insideActive) {
        graphics.setColor(BACKGROUND_1);
        setForeground(Colors.LABEL);
      } else {
        graphics.setColor(BACKGROUND_0);
        setForeground(new Color(51 + 32, 51 + 32, 51 + 32));
      }
      graphics.fillRect(0, 0, dimension.width, dimension.height);
      // ---
      if (isOverArrows(lastMouse) && enabled) {
        graphics.setColor(Color.WHITE);
        graphics.fillRect(dimension.width - border_width, 0, border_width, dimension.height);
      } else {
        graphics.setColor(Colors.alpha128(Color.GRAY));
        final int b = dimension.width - 1;
        graphics.drawLine(b, 0, b, dimension.height - 1);
      }
      // ---
      final int piy;
      if (numel() < 2)
        piy = dimension.height / 2;
      else {
        double num = numel() - 1;
        piy = (int) Math.round((dimension.height - 1) * index / num);
      }
      graphics.setColor(Color.WHITE);
      graphics.drawLine(0, piy, dimension.width, piy);
      if (insideActive) {
        graphics.setColor(Colors.withAlpha(Color.LIGHT_GRAY, 96));
        graphics.drawLine(0, piy + 1, dimension.width, piy + 1);
      }
      // ---
      graphics.setColor(insideActive ? Colors.SELECTION : Colors.alpha064(Color.LIGHT_GRAY));
      final int w = 3;
      final int r = dimension.width - 2 * w - 1;
      final int h = dimension.height - w - 1;
      {
        Path2D path2D = new Path2D.Double();
        path2D.moveTo(r, 1 + w);
        path2D.lineTo(r + 2 * w - 1, 1 + w);
        path2D.lineTo(r + w, 1);
        path2D.closePath();
        graphics.fill(path2D);
      }
      {
        Path2D path2D = new Path2D.Double();
        path2D.moveTo(r, h);
        path2D.lineTo(r + w, h + w);
        path2D.lineTo(r + 2 * w - 1, h);
        path2D.closePath();
        graphics.fill(path2D);
      }
      // ---
      super.paintComponent(graphics);
    }
  };
  private boolean isMenuEnabled = true;
  private boolean isMenuHover = false;
  private LazyMouseListener lazyMouseListener = mouseEvent -> {
    if (mouseEvent.getButton() == MouseEvent.BUTTON1 && jLabel.isEnabled()) {
      Dimension myDimension = jLabel.getSize();
      Point myPoint = mouseEvent.getPoint();
      if (isOverArrows(myPoint))
        increment(myPoint.y < myDimension.height / 2 ? -1 : 1); // sign of difference
      else //
      if (isMenuEnabled)
        new SpinnerMenu<>(this, isMenuHover).showRight(jLabel);
    }
  };

  public void setMenuEnabled(boolean isMenuEnabled) {
    this.isMenuEnabled = isMenuEnabled;
  }

  public void setMenuHover(boolean hover) {
    this.isMenuHover = hover;
  }

  private int value = 0;
  private boolean cyclic = false;
  private JSpinner jSpinner = new JSpinner(new SpinnerNumberModel(value, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
  private int index = -1;
  List<Type> list;

  public boolean isOverArrows(Point myPoint) {
    Dimension dimension = jLabel.getSize();
    return mouseInside && dimension.width - border_width < myPoint.x;
  }

  public SpinnerLabel() {
    jLabel.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel.setOpaque(false);
    jLabel.addMouseWheelListener(mouseWheelEvent -> {
      if (jLabel.isEnabled())
        increment(mouseWheelEvent.getWheelRotation());
    });
    MouseAdapter mouseAdapter = new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent mouseEvent) {
        mouseInside = true;
        lastMouse = mouseEvent.getPoint();
        jLabel.repaint();
      }

      @Override
      public void mouseExited(MouseEvent mouseEvent) {
        mouseInside = false;
        jLabel.repaint();
      }

      @Override
      public void mouseMoved(MouseEvent mouseEvent) {
        lastMouse = mouseEvent.getPoint();
        jLabel.repaint(); // not very efficient
      }
    };
    jLabel.addMouseListener(mouseAdapter);
    jLabel.addMouseMotionListener(mouseAdapter);
    new LazyMouse(lazyMouseListener).addListenersTo(jLabel);
    // myJSpinner.setFocusable(false); // does not have effect
    jSpinner.setPreferredSize(new Dimension(16, 28));
    jSpinner.addChangeListener(myChangeEvent -> {
      int delta = (Integer) jSpinner.getValue() - value;
      increment(delta);
      value = (Integer) jSpinner.getValue();
    });
  }

  public SpinnerLabel(SpinnerListener<Type> spinnerListener) {
    this();
    addSpinnerListener(spinnerListener);
  }

  public void addSpinnerListener(SpinnerListener<Type> spinnerListener) {
    spinnerListeners.add(spinnerListener);
  }

  public void setCyclic(boolean cyclic) {
    this.cyclic = cyclic;
  }

  public boolean getCyclic() {
    return cyclic;
  }

  public void setEnabled(boolean enabled) {
    jLabel.setEnabled(enabled);
    jSpinner.setEnabled(enabled);
  }

  private void increment(int delta) {
    int prev = index;
    index = cyclic //
        ? Math.floorMod(index + delta, numel())
        : Math.min(Math.max(0, index + delta), numel() - 1);
    if (index != prev) {
      updateLabel();
      reportToAll();
    }
  }

  public void reportToAll() {
    Type type = getValue();
    spinnerListeners.forEach(mySpinnerListener -> mySpinnerListener.actionPerformed(type));
  }

  /** @param list
   * is used by reference. Any modification to myList is discouraged
   * and (eventually) reflected in the {@link SpinnerLabel}. */
  public void setList(List<Type> list) {
    this.list = list;
  }

  public void setStream(Stream<Type> stream) {
    setList(stream.collect(Collectors.toList()));
  }

  public void setArray(@SuppressWarnings("unchecked") Type... values) {
    setList(Arrays.asList(values));
  }

  public Type getValue() {
    return 0 <= index && index < numel() //
        ? list.get(index)
        : null;
  }

  public int getIndex() {
    return index;
  }

  public int numel() {
    return list == null //
        ? 0
        : list.size();
  }

  /** does not invoke call backs
   * 
   * @param type */
  public void setValue(Type type) {
    index = list.indexOf(type);
    updateLabel();
  }

  public void setValueSafe(Type myType) {
    try {
      setValue(myType);
    } catch (Exception exception) {
      exception.printStackTrace();
      if (!list.isEmpty())
        setValue(list.get(0));
    }
  }

  public void setIndex(int index) {
    this.index = index;
    updateLabel();
  }

  public void setToolTipText(String myString) {
    jLabel.setToolTipText(myString);
    jSpinner.setToolTipText(myString);
  }

  private void updateLabel() {
    jLabel.setText(stringFormat(getValue()));
    jSpinner.setEnabled(1 < list.size()); // added recently to indicate that there is nothing to scroll
  }

  public JLabel getLabelComponent() {
    return jLabel;
  }

  public JComponent getSpinnerComponent() {
    return jSpinner;
  }

  public void addToComponent(JComponent jComponent, Dimension dimension, String toolTip) {
    addToComponentReduced(jComponent, dimension, toolTip);
    jComponent.add(getSpinnerComponent());
  }

  public void addToComponentReduced(JComponent jComponent, Dimension dimension, String toolTip) {
    jLabel.setToolTipText(toolTip == null || toolTip.isEmpty() ? null : toolTip);
    jLabel.setPreferredSize(dimension);
    jComponent.add(jLabel);
  }

  public void setVisible(boolean visible) {
    jLabel.setVisible(visible);
    jSpinner.setVisible(visible);
  }

  private static <Type> String stringFormat(Type type) {
    return Objects.isNull(type) ? "" : type.toString();
  }
}
