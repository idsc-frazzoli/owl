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
  public static final Color BACKGROUND_1 = new Color(248, 248, 248, 128);
  public static final Color BACKGROUND_0 = new Color(248, 248, 248, 64);
  private static final int BORDER_WIDTH_MIN = 9;
  private static final int BORDER_WIDTH_MAX = 16;
  // ---
  private boolean mouseInside = false;
  private Point myLastMouse = new Point();
  private int border_width = 0;
  final List<SpinnerListener<Type>> mySpinnerListeners = new LinkedList<>();
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
      if (isOverArrows(myLastMouse) && enabled) {
        graphics.setColor(Color.white);
        graphics.fillRect(dimension.width - border_width, 0, border_width, dimension.height);
      } else {
        graphics.setColor(Colors.alpha128(Color.gray));
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
      graphics.setColor(Color.white);
      graphics.drawLine(0, piy, dimension.width, piy);
      if (insideActive) {
        graphics.setColor(Colors.withAlpha(Color.lightGray, 96));
        graphics.drawLine(0, piy + 1, dimension.width, piy + 1);
      }
      // Point2D start = new Point2D.Float(0, 0);
      // Point2D end = new Point2D.Float(0, myDimension.height - 1);
      // float[] dist = { 0f, (numel() - index) / (float) (numel() + 1), 1f };
      // Color[] colors = { Colors.alpha064(Color.lightGray),
      // Colors.alpha128(Color.white), Colors.alpha064(Color.lightGray) };
      // LinearGradientPaint p = new LinearGradientPaint(start, end, dist, colors);
      // myGraphics.setPaint(p);
      // ---
      graphics.setColor(insideActive ? Colors.SELECTION : Colors.alpha064(Color.lightGray));
      final int w = 3;
      final int r = dimension.width - 2 * w - 1;
      final int h = dimension.height - w - 1;
      {
        Path2D myPath2D = new Path2D.Double();
        myPath2D.moveTo(r, 1 + w);
        myPath2D.lineTo(r + 2 * w - 1, 1 + w);
        myPath2D.lineTo(r + w, 1);
        myPath2D.closePath();
        graphics.fill(myPath2D);
      }
      {
        Path2D myPath2D = new Path2D.Double();
        myPath2D.moveTo(r, h);
        myPath2D.lineTo(r + w, h + w);
        myPath2D.lineTo(r + 2 * w - 1, h);
        myPath2D.closePath();
        graphics.fill(myPath2D);
      }
      // ---
      super.paintComponent(graphics);
    }
  };
  private boolean isMenuEnabled = true;
  private boolean isMenuHover = false;
  LazyMouseListener myLazyMouseListener = myMouseEvent -> {
    if (myMouseEvent.getButton() == MouseEvent.BUTTON1) {
      if (jLabel.isEnabled()) {
        Dimension myDimension = jLabel.getSize();
        Point myPoint = myMouseEvent.getPoint();
        if (isOverArrows(myPoint))
          increment(myPoint.y < myDimension.height / 2 ? -1 : 1); // sign of difference
        else {
          if (isMenuEnabled)
            new SpinnerMenu<>(this, isMenuHover).showRight(jLabel);
        }
      }
    }
  };

  public void setMenuEnabled(boolean isMenuEnabled) {
    this.isMenuEnabled = isMenuEnabled;
  }

  public void setMenuHover(boolean hover) {
    this.isMenuHover = hover;
  }

  int value = 0;
  boolean cyclic = false;
  JSpinner myJSpinner = new JSpinner(new SpinnerNumberModel(value, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));
  int index = -1;
  List<Type> list;

  // protected boolean isUsingArrows = true;
  // public void setUsingArrows(boolean myBoolean) {
  // isUsingArrows = myBoolean;
  // }
  public boolean isOverArrows(Point myPoint) {
    Dimension myDimension = jLabel.getSize();
    return mouseInside && myDimension.width - border_width < myPoint.x;
  }

  public SpinnerLabel() {
    // myJLabel.setOpaque(true);
    // myJLabel.setBackground(background);
    jLabel.setHorizontalAlignment(SwingConstants.CENTER);
    jLabel.setOpaque(false);
    jLabel.addMouseWheelListener(myMouseWheelEvent -> {
      if (jLabel.isEnabled())
        increment(myMouseWheelEvent.getWheelRotation());
    });
    MouseAdapter myMouseAdapter = new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent myMouseEvent) {
        mouseInside = true;
        myLastMouse = myMouseEvent.getPoint();
        jLabel.repaint();
      }

      @Override
      public void mouseExited(MouseEvent myMouseEvent) {
        mouseInside = false;
        jLabel.repaint();
      }

      @Override
      public void mouseMoved(MouseEvent myMouseEvent) {
        myLastMouse = myMouseEvent.getPoint();
        // if (isUsingArrows)
        jLabel.repaint(); // not very efficient
      }
    };
    jLabel.addMouseListener(myMouseAdapter);
    jLabel.addMouseMotionListener(myMouseAdapter);
    new LazyMouse(myLazyMouseListener).addListenersTo(jLabel);
    // myJSpinner.setFocusable(false); // does not have effect
    myJSpinner.setPreferredSize(new Dimension(16, 28));
    myJSpinner.addChangeListener(myChangeEvent -> {
      int delta = (Integer) myJSpinner.getValue() - value;
      increment(delta);
      value = (Integer) myJSpinner.getValue();
    });
  }

  public SpinnerLabel(SpinnerListener<Type> mySpinnerListener) {
    this();
    addSpinnerListener(mySpinnerListener);
  }

  public void addSpinnerListener(SpinnerListener<Type> mySpinnerListener) {
    mySpinnerListeners.add(mySpinnerListener);
  }

  public void setCyclic(boolean myBoolean) {
    cyclic = myBoolean;
  }

  public boolean getCyclic() {
    return cyclic;
  }

  public void setEnabled(boolean myBoolean) {
    jLabel.setEnabled(myBoolean);
    myJSpinner.setEnabled(myBoolean);
  }

  private void increment(int delta) {
    int prev = index;
    if (cyclic)
      index = mod(index + delta, numel());
    else
      index = Math.min(Math.max(0, index + delta), numel() - 1);
    if (index != prev) {
      updateLabel();
      reportToAll();
    }
  }

  private static int mod(int index, int size) {
    int value = index % size;
    return value < 0 ? size + value : value;
  }

  public void reportToAll() {
    Type myType = getValue();
    mySpinnerListeners.forEach(mySpinnerListener -> mySpinnerListener.actionPerformed(myType));
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

  // public void setIterable(Iterable<Type> myIterable) {
  // setList(ListUtils.copyIterator(myIterable));
  // }
  public void setArray(@SuppressWarnings("unchecked") Type... values) {
    setList(Arrays.asList(values));
  }

  public Type getValue() {
    if (0 <= index && index < numel())
      return list.get(index);
    return null;
  }

  public int getIndex() {
    return index;
  }

  public int numel() {
    return list == null ? 0 : list.size();
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
    myJSpinner.setToolTipText(myString);
  }

  public String stringFormat(Type myType) {
    return myType == null ? "" : myType.toString();
  }

  private void updateLabel() {
    jLabel.setText(stringFormat(getValue()));
    myJSpinner.setEnabled(1 < list.size()); // added recently to indicate that there is nothing to scroll
  }

  public JLabel getLabelComponent() {
    return jLabel;
  }

  public JComponent getSpinnerComponent() {
    return myJSpinner;
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

  public void setVisible(boolean myBoolean) {
    jLabel.setVisible(myBoolean);
    myJSpinner.setVisible(myBoolean);
  }
}
