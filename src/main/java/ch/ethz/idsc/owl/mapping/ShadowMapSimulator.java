// code by ynager
package ch.ethz.idsc.owl.mapping;

import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Supplier;

import ch.ethz.idsc.owl.math.state.StateTime;

public final class ShadowMapSimulator {
  private final Timer increaserTimer = new Timer("MapUpdateTimer");
  private final ShadowMapCV shadowMap;
  private final Supplier<StateTime> stateTimeSupplier;
  // ---
  private boolean isPaused = false;

  public ShadowMapSimulator(ShadowMapCV shadowMap, Supplier<StateTime> stateTimeSupplier) {
    this.shadowMap = shadowMap;
    this.stateTimeSupplier = stateTimeSupplier;
  }

  /** @param updateRate_Hz */
  public void startNonBlocking(int updateRate_Hz) {
    float period = Math.max(1.0f / updateRate_Hz, shadowMap.getMinTimeDelta());
    TimerTask timerTask = new TimerTask() {
      @Override
      public void run() {
        if (!isPaused)
          shadowMap.updateMap(stateTimeSupplier.get(), period);
      }
    };
    increaserTimer.scheduleAtFixedRate(timerTask, 10, Math.round(1000 * period));
  }

  public void flagShutdown() {
    increaserTimer.cancel();
  }

  public void pause() {
    isPaused = true;
  }

  public void resume() {
    isPaused = false;
  }
}
