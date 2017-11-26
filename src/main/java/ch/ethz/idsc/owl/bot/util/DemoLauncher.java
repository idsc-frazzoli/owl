// code by jph
package ch.ethz.idsc.owl.bot.util;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import lcm.util.ClassDiscovery;
import lcm.util.ClassPaths;
import lcm.util.ClassVisitor;

/** scans repository for classes that implement {@link DemoInterface}
 * DemoLauncher creates a gui that allows to start these classes. */
public enum DemoLauncher {
  ;
  private static final int BUTTON_HEIGHT = 24;
  private static final Comparator<Class<?>> CLASSNAMECOMPARATOR = new Comparator<Class<?>>() {
    @Override
    public int compare(Class<?> c1, Class<?> c2) {
      return c1.getName().compareToIgnoreCase(c2.getName());
    }
  };

  public static void main(String[] args) {
    List<Class<?>> demos = new LinkedList<>();
    ClassVisitor classVisitor = new ClassVisitor() {
      @Override
      public void classFound(String jarfile, Class<?> cls) {
        if (DemoInterface.class.isAssignableFrom(cls))
          if (!Modifier.isAbstract(cls.getModifiers()))
            demos.add(cls);
      }
    };
    ClassDiscovery.execute(ClassPaths.getDefault(), classVisitor);
    // ---
    Collections.sort(demos, CLASSNAMECOMPARATOR);
    JFrame jFrame = new JFrame();
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    JPanel jComponent = new JPanel(new BorderLayout());
    {
      JPanel jPanel = new JPanel(new GridLayout(demos.size(), 1));
      for (Class<?> cls : demos) {
        JButton jButton = new JButton(cls.getSimpleName());
        jButton.addActionListener(event -> {
          try {
            DemoInterface demoInterface = (DemoInterface) cls.newInstance();
            demoInterface.start();
          } catch (Exception exception) {
            exception.printStackTrace();
          }
        });
        jPanel.add(jButton);
      }
      jComponent.add(jPanel, BorderLayout.NORTH);
    }
    jFrame.setContentPane(new JScrollPane(jComponent, //
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, //
        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
    jFrame.setBounds(1200, 100, 250, Math.min(40 + demos.size() * BUTTON_HEIGHT, 800));
    jFrame.setVisible(true);
  }
}
