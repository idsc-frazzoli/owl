// code by jph
package ch.ethz.idsc.subare.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

/* package */ class BufferedHtmlUtf8 extends HtmlUtf8 {
  private final StringBuilder stringBuilder = new StringBuilder();

  protected BufferedHtmlUtf8(File file) {
    super(file);
  }

  @Override
  protected void private_append(Object object) {
    stringBuilder.append(object);
  }

  @Override
  public void close() {
    super.close();
    // ---
    try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file), CHARSET)) {
      outputStreamWriter.write(stringBuilder.toString());
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
