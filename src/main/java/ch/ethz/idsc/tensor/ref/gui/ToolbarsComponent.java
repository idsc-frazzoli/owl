// code by jph
package ch.ethz.idsc.tensor.ref.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;

public class ToolbarsComponent {
  public static final int WEST_WIDTH = 140;
  public static final int HEIGHT = 30;
  public static final int HEIGHT_CBOX = 15;
  public static final String UNKNOWN = "<unknown>";
  // ---
  private final JPanel jPanel = new JPanel(new BorderLayout());
  private final RowPanel rowTitle = new RowPanel();
  private final RowPanel rowActor = new RowPanel();

  public ToolbarsComponent() {
    jPanel.add(rowTitle.jPanel, BorderLayout.WEST);
    jPanel.add(rowActor.jPanel, BorderLayout.CENTER);
  }

  protected void addSeparator() {
    JLabel jLabelW = new JLabel();
    jLabelW.setBackground(Color.GRAY);
    jLabelW.setOpaque(true);
    JLabel jLabelC = new JLabel();
    jLabelC.setBackground(Color.GRAY);
    jLabelC.setOpaque(true);
    addPair(jLabelW, jLabelC, 5);
  }

  protected JToolBar createRow(String title) {
    return createRow(title, HEIGHT);
  }

  protected JToolBar createRow(String title, int height) {
    JToolBar jToolBar1 = new JToolBar();
    JToolBar jToolBar2 = new JToolBar();
    jToolBar1.setFloatable(false);
    jToolBar1.setLayout(new FlowLayout(FlowLayout.RIGHT, 3, 0));
    JLabel jLabel = new JLabel(title);
    jLabel.setPreferredSize(new Dimension(jLabel.getPreferredSize().width, height));
    jToolBar1.add(jLabel);
    jToolBar2.setFloatable(false);
    jToolBar2.setLayout(new FlowLayout(FlowLayout.LEFT, 3, 0));
    addPair(jToolBar1, jToolBar2, height);
    return jToolBar2;
  }

  private void addPair(JComponent west, JComponent center) {
    addPair(west, center, HEIGHT);
  }

  private void addPair(JComponent west, JComponent center, int height) {
    int width;
    // width = west.getPreferredSize().width;
    west.setPreferredSize(new Dimension(WEST_WIDTH, height));
    west.setSize(new Dimension(WEST_WIDTH, height));
    rowTitle.add(west);
    // ---
    width = center.getPreferredSize().width;
    center.setPreferredSize(new Dimension(width, height));
    rowActor.add(center);
  }

  /***************************************************/
  /** @param title
   * @return editable text field that allows user modification */
  protected JTextField createEditing(String title) {
    JTextField jTextField = new JTextField(20);
    jTextField.setText(UNKNOWN);
    JToolBar jToolBar1 = new JToolBar();
    jToolBar1.setFloatable(false);
    jToolBar1.setLayout(new FlowLayout(FlowLayout.RIGHT, 3, 0));
    JLabel jLabel = new JLabel(title);
    jLabel.setPreferredSize(new Dimension(jLabel.getPreferredSize().width, HEIGHT));
    jToolBar1.add(jLabel);
    addPair(jToolBar1, jTextField);
    return jTextField;
  }

  /***************************************************/
  /** @param title
   * @return non-editable text field to display values */
  protected JTextField createReading(String title) {
    JTextField jTextField = createEditing(title);
    jTextField.setEditable(false);
    jTextField.setEnabled(false);
    jTextField.setDisabledTextColor(Color.BLACK);
    return jTextField;
  }

  protected JCheckBox createReadingCheckbox(String title) {
    JCheckBox jTextField = new JCheckBox(title);
    jTextField.setEnabled(false);
    JLabel jLabel = new JLabel(" ");
    jLabel.setPreferredSize(new Dimension(jLabel.getPreferredSize().width, HEIGHT_CBOX));
    addPair(jLabel, jTextField, HEIGHT_CBOX);
    return jTextField;
  }

  /***************************************************/
  public JComponent getScrollPane() {
    JPanel jPanel = new JPanel(new BorderLayout());
    jPanel.add(this.jPanel, BorderLayout.NORTH);
    return new JScrollPane(jPanel);
  }
}
