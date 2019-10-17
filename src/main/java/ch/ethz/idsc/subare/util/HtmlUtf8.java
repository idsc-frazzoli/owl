// code by jph
package ch.ethz.idsc.subare.util;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/** HtmlUtf8 exports strings to html pages in utf-8 encoding. All logs of MissionControl are exported with HtmlUtf8. */
public abstract class HtmlUtf8 implements AutoCloseable {
  protected static final Charset CHARSET = Charset.forName("UTF-8");

  /** @param file
   * @return */
  public static HtmlUtf8 page(File file) {
    if (file.exists())
      file.delete();
    String string;
    string = "<!DOCTYPE html>\n<html>\n";
    string += "<head>\n";
    string += "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />\n";
    string += "</head>\n<body>\n";
    HtmlUtf8 htmlUtf8 = new BufferedHtmlUtf8(file);
    htmlUtf8.append(string);
    return htmlUtf8;
  }

  /** @param file
   * @param title
   * @param split for instance: cols="300,*"
   * @param fileStringL
   * @param nameStringL
   * @param fileStringR
   * @param nameStringR */
  public static void index(File file, String title, //
      String split, // cols="300,*"
      String fileStringL, String nameStringL, //
      String fileStringR, String nameStringR) {
    try {
      StringBuilder stringBuffer = new StringBuilder();
      stringBuffer.append("<html>\n");
      if (title != null && !title.isEmpty())
        stringBuffer.append("<head><title>" + title + "</title></head>\n");
      stringBuffer.append("<frameset " + split + ">\n");
      stringBuffer.append("<frame src=\"" + fileStringL + "\" name=\"" + nameStringL + "\">\n");
      stringBuffer.append("<frame src=\"" + fileStringR + "\" name=\"" + nameStringR + "\">\n");
      stringBuffer.append("</frameset>\n</html>\n");
      try (OutputStream outputStream = new FileOutputStream(file)) {
        outputStream.write(stringBuffer.toString().getBytes(CHARSET));
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  public static String color(Color color) {
    return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
  }
  // ---

  public final File file;

  protected HtmlUtf8(File file) {
    this.file = file;
  }

  public void append(Object object) {
    private_append(object);
  }

  public void appendln(Object object) {
    private_append(object + "\n");
  }

  public void appendln() {
    append("\n");
  }

  protected abstract void private_append(Object object);

  @Override
  public void close() {
    append("</body>\n</html>\n");
  }
}
