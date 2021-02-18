// code by jph
package ch.ethz.idsc.tensor.demo;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.qty.SimpleUnitSystem;
import ch.ethz.idsc.tensor.qty.UnitSystem;
import ch.ethz.idsc.tensor.qty.UnitSystems;

public class UnitSystemDemo {
  private static final Font FONT = new Font(Font.MONOSPACED, Font.BOLD, 26);
  private static final Font FONT2 = new Font(Font.DIALOG, Font.PLAIN, 22);
  private final JFrame jFrame = new JFrame();
  private final JTextArea jTextArea = new JTextArea();
  private final JLabel jLabel = new JLabel();
  // ---
  UnitSystem unitSystem;

  public UnitSystemDemo() {
    unitSystem = SimpleUnitSystem.from(ResourceData.properties("/demo/si.properties"));
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    JPanel jPane = new JPanel(new BorderLayout());
    JPanel jPanel = new JPanel(new BorderLayout());
    {
      JToolBar jToolBar = new JToolBar();
      jToolBar.setFloatable(false);
      JTextField jTextFieldA = new JTextField();
      jTextFieldA.setFont(FONT);
      jToolBar.add(jTextFieldA);
      JTextField jTextFieldB = new JTextField();
      jTextFieldB.setFont(FONT);
      jToolBar.add(jTextFieldB);
      {
        JButton jButton = new JButton("substitute");
        jButton.addActionListener(l -> {
          String prev = jTextFieldA.getText();
          String next = jTextFieldB.getText();
          try {
            update(UnitSystems.rotate(unitSystem, prev, next));
          } catch (Exception e) {
            e.printStackTrace();
          }
        });
        jToolBar.add(jButton);
      }
      {
        JButton jButton = new JButton("reset");
        jButton.addActionListener(l -> {
          update(UnitSystem.SI());
        });
        jToolBar.add(jButton);
      }
      jPanel.add("North", jToolBar);
    }
    update(unitSystem);
    jTextArea.setFont(FONT2);
    jPanel.add("Center", new JScrollPane(jTextArea));
    jPane.add("Center", jPanel);
    jLabel.setFont(FONT);
    jPane.add("North", jLabel);
    jFrame.setContentPane(jPane);
    jFrame.setBounds(100, 100, 600, 900);
  }

  private String format() {
    // NavigableMap<String, Scalar> navigableMap = new TreeMap<>(unitSystem.map());
    StringBuilder stringBuilder = new StringBuilder();
    unitSystem.map().keySet().stream().sorted(String::compareToIgnoreCase).forEach(key -> {
      Scalar value = unitSystem.map().get(key);
      stringBuilder.append(key + '\t' + value);
      stringBuilder.append('\n');
    });
    return stringBuilder.toString();
  }

  private void update(UnitSystem unitSystem) {
    this.unitSystem = unitSystem;
    jTextArea.setText(format());
    jTextArea.setCaretPosition(0);
    jLabel.setText("base: " + UnitSystems.base(unitSystem).toString());
  }

  public static void main(String[] args) {
    UnitSystemDemo unitSystemDemo = new UnitSystemDemo();
    unitSystemDemo.jFrame.setVisible(true);
  }
}
