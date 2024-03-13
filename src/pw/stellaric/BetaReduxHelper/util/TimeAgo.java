package pw.stellaric.BetaReduxHelper.util;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

// StackOverflow
public class TimeAgo {
    public static final List<Long> times = Arrays.asList(
            TimeUnit.DAYS.toSeconds(365),
            TimeUnit.DAYS.toSeconds(30),
            TimeUnit.DAYS.toSeconds(1),
            TimeUnit.HOURS.toSeconds(1),
            TimeUnit.MINUTES.toSeconds(1),
            TimeUnit.SECONDS.toSeconds(1)
    );
    public static final List<String> timesString = Arrays.asList("y", "mo", "d", "h", "m", "s");

    public static String toDuration(long duration) {

        StringBuffer res = new StringBuffer();
        for (int i = 0; i < TimeAgo.times.size(); i++) {
            Long current = TimeAgo.times.get(i);
            long temp = duration / current;
            if (temp > 0) {
                res.append(temp).append(TimeAgo.timesString.get(i)).append(" ago"); // .append(" ")
                break;
            }
        }
        if (res.toString().isEmpty())
            return "0s ago";
        else
            return res.toString();
    }
}
