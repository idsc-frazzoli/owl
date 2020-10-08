// code by jph
package ch.ethz.idsc.owl.bot.util;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import ch.ethz.idsc.tensor.ext.HomeDirectory;
import ch.ethz.idsc.tensor.io.AnimationWriter;
import ch.ethz.idsc.tensor.io.GifAnimationWriter;

enum ExportAnimation {
  ;
  public static void main(String[] args) throws Exception {
    String name = "MODIY FILENAME LOCALLY BUT DO NOT COMMIT THIS CODE";
    name = "...";
    File directory = HomeDirectory.Pictures(name);
    try (AnimationWriter animationWriter = //
        new GifAnimationWriter(HomeDirectory.Pictures(name + ".gif"), 100, TimeUnit.MILLISECONDS)) {
      List<File> list = Stream.of(directory.listFiles()) //
          .filter(File::isFile) //
          .sorted() //
          .collect(Collectors.toList());
      for (File file : list) {
        System.out.println(file);
        animationWriter.write(ImageIO.read(file));
      }
    }
  }
}
