// code by jph
package ch.ethz.idsc.owl.gui.win;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.ResourceData;

/** base class for {@link OwlyFrame} and {@link OwlyAnimationFrame} */
public class BaseFrame {
  protected static final String IMAGE_FORMAT = "png";
  // ---
  public final JFrame jFrame = new JFrame();
  private final JPanel jPanel = new JPanel(new BorderLayout());
  public final JToolBar jToolBar = new JToolBar();
  public final GeometricComponent geometricComponent = new GeometricComponent();
  protected final JLabel jStatusLabel = new JLabel();

  protected BaseFrame() {
    jFrame.setBounds(100, 50, 800, 800);
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jToolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
    jToolBar.setFloatable(false);
    {
      JButton jButton = new JButton("save2png");
      try {
        ImageIcon imageIcon = new ImageIcon(ResourceData.bufferedImage("/icon/camera.gif"));
        jButton = new JButton(imageIcon);
      } catch (Exception exception) {
        System.err.println(exception);
      }
      jButton.setToolTipText("snapshot is stored in ~/Pictures/...");
      jButton.addActionListener(actionEvent -> {
        try {
          File file = HomeDirectory.Pictures(String.format("owl_%d.%s", System.currentTimeMillis(), IMAGE_FORMAT));
          ImageIO.write(offscreen(), IMAGE_FORMAT, file);
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      });
      jToolBar.add(jButton);
    }
    jToolBar.addSeparator();
    jPanel.add(jToolBar, BorderLayout.NORTH);
    jPanel.add(geometricComponent.jComponent, BorderLayout.CENTER);
    jPanel.add(jStatusLabel, BorderLayout.SOUTH);
    jFrame.setContentPane(jPanel);
  }

  public final BufferedImage offscreen() {
    Dimension dimension = geometricComponent.jComponent.getSize();
    BufferedImage bufferedImage = //
        new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB);
    geometricComponent.render(bufferedImage.createGraphics(), dimension);
    return bufferedImage;
  }

  public final void configCoordinateOffset(int px, int py) {
    geometricComponent.setOffset(Tensors.vector(px, py));
  }

  public final void close() {
    jFrame.setVisible(false);
    jFrame.dispose();
  }
}
