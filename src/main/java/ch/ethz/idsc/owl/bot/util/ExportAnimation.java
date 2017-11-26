// code by jph
package ch.ethz.idsc.owl.bot.util;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import ch.ethz.idsc.tensor.io.AnimationWriter;

enum ExportAnimation {
  ;
  public static void main(String[] args) throws Exception {
    String name = "MODIY FILENAME LOCALLY BUT DO NOT COMMIT THIS CODE";
    name = "...";
    File directory = UserHome.Pictures(name);
    AnimationWriter animationWriter = AnimationWriter.of(UserHome.Pictures(name + ".gif"), 100);
    List<File> list = Stream.of(directory.listFiles()) //
        .filter(File::isFile) //
        .sorted() //
        .collect(Collectors.toList());
    for (File file : list) {
      System.out.println(file);
      animationWriter.append(ImageIO.read(file));
    }
    animationWriter.close();
  }
}
