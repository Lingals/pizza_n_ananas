package insset.com.utils;

import android.util.Log;

import com.librato.metrics.BatchResult;
import com.librato.metrics.HttpPoster;
import com.librato.metrics.LibratoBatch;
import com.librato.metrics.PostResult;
import com.librato.metrics.Sanitizer;

import java.util.concurrent.TimeUnit;

import insset.com.librato.DefaultHttpPoster;

/**
 * Created by quentin on 20/10/16.
 */
public class Lilibrato {

    static String email = "p.pavone59@gmail.com";
    static String apiToken = "d77e3da0ff40b4d1949ad99702fdf5675f4a2aeed3c6f8f6acd59ffcb6832e8d";
    static String apiUrl = "https://metrics-api.librato.com/v1/metrics";
    static HttpPoster poster;

    String classe = "";

    public Lilibrato(String classe) {
        this.classe = classe;
    }

    public void setTimes(long timeStart, long timeEnd) {
        Log.e("ENVOI", apiUrl + " " + email + " " + apiToken);
        poster = new DefaultHttpPoster(apiUrl, email, apiToken);

        int batchSize = 300;
        long timeout = 10L;
        TimeUnit timeoutUnit = TimeUnit.SECONDS;
        Sanitizer sanitizer = Sanitizer.NO_OP;
        LibratoBatch batch = new LibratoBatch(batchSize, sanitizer, timeout, timeoutUnit, null, poster);

        int times = (int) (timeEnd - timeStart);

        batch.addGaugeMeasurement(this.classe + "." + "times", times);
        batch.addCounterMeasurement("bytes-in", (long) 42);


        long epoch = System.currentTimeMillis() / 1000;
        String source = "Android";
        BatchResult result = batch.post(source, epoch);
        if (!result.success()) {
            for (PostResult post : result.getFailedPosts()) {
                Log.e("Not POST to Librato", post.toString() + "");
            }
        }
    }

    public void setStatus(int status) {
        Log.e("ENVOI", apiUrl + " " + email + " " + apiToken);
        poster = new DefaultHttpPoster(apiUrl, email, apiToken);

        int batchSize = 300;
        long timeout = 10L;
        TimeUnit timeoutUnit = TimeUnit.SECONDS;
        Sanitizer sanitizer = Sanitizer.NO_OP;
        LibratoBatch batch = new LibratoBatch(batchSize, sanitizer, timeout, timeoutUnit, null, poster);

        batch.addGaugeMeasurement(this.classe + "." + "status" + Integer.toString(status), status);


        long epoch = System.currentTimeMillis() / 1000;
        String source = "Android";
        BatchResult result = batch.post(source, epoch);
        if (!result.success()) {
            for (PostResult post : result.getFailedPosts()) {
                Log.e("Not POST to Librato", post.toString() + "");
            }
        }
    }
}
