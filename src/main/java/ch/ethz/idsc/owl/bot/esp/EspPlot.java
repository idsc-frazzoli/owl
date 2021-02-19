// code by jph
package ch.ethz.idsc.owl.bot.esp;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;

import javax.imageio.ImageIO;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.io.Import;

/* package */ enum EspPlot {
  ;
  static final int RES = 64;

  public static void main(String[] args) throws ClassNotFoundException, IOException, DataFormatException {
    List<StateTime> list = Import.object(HomeDirectory.file("esp.object"));
    System.out.println(list.size());
    int index = 0;
    File folder = HomeDirectory.Pictures("ESP");
    folder.mkdir();
    // KlotskiPlot klotskiPlot = new KlotskiPlot(klotskiProblem, TRAFFIC_JAM);
    for (StateTime stateTime : list) {
      BufferedImage bufferedImage = new BufferedImage(320, 320, BufferedImage.TYPE_INT_ARGB);
      GeometricLayer geometricLayer = GeometricLayer.of(Tensors.matrix(new Number[][] { //
          { 0, RES, 0 }, //
          { RES, 0, 0 }, //
          { 0, 0, 1 } }));
      Graphics2D graphics = bufferedImage.createGraphics();
      graphics.setColor(Color.WHITE);
      graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
      new EspRender(stateTime.state()).render(geometricLayer, graphics);
      graphics.setColor(Color.DARK_GRAY);
      // graphics.setFont(new Font(Font.DIALOG, Font.PLAIN, RES / 2));
      // graphics.drawString("move " + index, RES / 8, RES / 2);
      ImageIO.write(bufferedImage, "png", new File(folder, String.format("%03d.png", index)));
      ++index;
    }
  }
}
