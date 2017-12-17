// code by jph
package ch.ethz.idsc.owl.gui.win;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.tensor.Tensors;

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
    jToolBar.setFloatable(false);
    {
      JButton jButton = new JButton("save2png");
      jButton.setToolTipText("file is created in Pictures/...");
      jButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
          try {
            BufferedImage bufferedImage = offscreen();
            ImageIO.write(bufferedImage, IMAGE_FORMAT, UserHome.Pictures( //
                String.format("owl_%d.%s", System.currentTimeMillis(), IMAGE_FORMAT)));
          } catch (Exception exception) {
            exception.printStackTrace();
          }
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
