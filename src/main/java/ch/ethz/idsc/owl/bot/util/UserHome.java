// code by jph
package ch.ethz.idsc.owl.bot.util;

import java.io.File;

public enum UserHome {
  ;
  /** for the special input filename == "" the function returns the user home directory
   * 
   * @param filename
   * @return */
  public static File file(String filename) {
    return new File(System.getProperty("user.home"), filename);
  }

  /** @param filename
   * @return */
  public static File Pictures(String filename) {
    File directory = file("Pictures");
    directory.mkdir();
    return new File(directory, filename);
  }
}
