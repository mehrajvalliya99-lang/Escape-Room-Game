import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Manages the countdown game timer across all four escape room levels.
 * Automatically initializes time limits based on chosen Difficulty,
 * updates display strings, and safely fires timeout callbacks on Swing's EDT.
 */
public class GameTimer {

    private Timer swingTimer;
    private int remainingSeconds;
    private int totalSeconds;
    private boolean running;
    private Runnable onTimeoutCallback;
    private Runnable onTickCallback;

    public GameTimer() {
        this.remainingSeconds = 15 * 60;
        this.totalSeconds = 15 * 60;
        this.running = false;
    }

    /**
     * Starts the countdown timer for the given difficulty setting.
     * Safe against multiple active timers.
     */
    public void startTimer(Difficulty difficulty, Runnable onTimeout, Runnable onTick) {
        stopTimer();

        int minutes = (difficulty != null) ? difficulty.getDurationMinutes() : 15;
        this.totalSeconds = minutes * 60;
        this.remainingSeconds = this.totalSeconds;
        this.onTimeoutCallback = onTimeout;
        this.onTickCallback = onTick;

        this.swingTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (remainingSeconds > 0) {
                    remainingSeconds--;
                    if (onTickCallback != null) {
                        onTickCallback.run();
                    }
                } else {
                    stopTimer();
                    if (onTimeoutCallback != null) {
                        SwingUtilities.invokeLater(onTimeoutCallback);
                    }
                }
            }
        });

        this.swingTimer.start();
        this.running = true;
    }

    /**
     * Pauses the timer without resetting remaining time.
     */
    public void pauseTimer() {
        if (swingTimer != null && swingTimer.isRunning()) {
            swingTimer.stop();
        }
        this.running = false;
    }

    /**
     * Resumes the paused timer.
     */
    public void resumeTimer() {
        if (swingTimer != null && !swingTimer.isRunning() && remainingSeconds > 0) {
            swingTimer.start();
            this.running = true;
        }
    }

    /**
     * Stops the timer completely.
     */
    public void stopTimer() {
        if (swingTimer != null) {
            swingTimer.stop();
            swingTimer = null;
        }
        this.running = false;
    }

    public int getRemainingSeconds() {
        return remainingSeconds;
    }

    public int getTotalSeconds() {
        return totalSeconds;
    }

    public int getElapsedSeconds() {
        return Math.max(0, totalSeconds - remainingSeconds);
    }

    /**
     * Formats remaining time as MM:SS for top HUD.
     */
    public String getFormattedRemainingTime() {
        int m = remainingSeconds / 60;
        int s = remainingSeconds % 60;
        return String.format("%02d:%02d", m, s);
    }

    /**
     * Formats elapsed time as MM:SS for Win results.
     */
    public String getFormattedElapsedTime() {
        int elapsed = getElapsedSeconds();
        int m = elapsed / 60;
        int s = elapsed % 60;
        return String.format("%02d:%02d", m, s);
    }

    public boolean isRunning() {
        return running && swingTimer != null && swingTimer.isRunning();
    }

    public boolean isExpired() {
        return remainingSeconds <= 0;
    }

    public void reset() {
        stopTimer();
        this.remainingSeconds = 15 * 60;
        this.totalSeconds = 15 * 60;
    }
}
