// code by jph
package ch.ethz.idsc.sophus.app.curve;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.ethz.idsc.owl.bot.util.UserHome;

enum DatahakiResources {
  ;
  public static void main(String[] args) throws IOException {
    if (!UserHome.file("").getName().equals("datahaki"))
      throw new RuntimeException();
    List<String> list = new ArrayList<>();
    File root = UserHome.file("Projects/ephemeral/src/main/resources/dubilab/app/pose");
    Properties properties = new Properties();
    for (File folder : Stream.of(root.listFiles()).sorted().collect(Collectors.toList()))
      for (File file : Stream.of(folder.listFiles()).sorted().collect(Collectors.toList())) {
        String name = file.getName();
        String total = folder.getName() + "/" + name.substring(0, name.length() - 4);
        System.out.println(total);
        list.add(total);
        properties.setProperty(total, "");
      }
    try (FileWriter fileWriter = new FileWriter(new File("src/main/resources/dubilab/app/pose/index.properties"))) {
      for (String string : list)
        fileWriter.write(string + '\n');
    }
  }
}
