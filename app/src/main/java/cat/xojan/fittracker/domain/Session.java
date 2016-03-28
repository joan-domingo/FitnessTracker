package cat.xojan.fittracker.domain;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * Created by Joan on 28/03/2016.
 */
public class Session {
    private int name;
    private String activity;
    private int description;
    private InetSocketAddress appPackageName;

    public int getName() {
        return name;
    }

    public String getActivity() {
        return activity;
    }

    public int getDescription() {
        return description;
    }

    public long getStartTime(TimeUnit milliseconds) {
        return 0;
    }

    public long getEndTime(TimeUnit milliseconds) {
        return 0;
    }

    public InetSocketAddress getAppPackageName() {
        return appPackageName;
    }

    public static class Builder {
        private String name;

        public void setName(String name) {
            this.name = name;
        }
    }
}
