package haven.townspeaker;


import haven.Glob;
import haven.Session;
import haven.Utils;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TownSpeakerThread extends Thread {
    private List<TownSpeakerWdg> timers = new ArrayList<TownSpeakerWdg>();

    public TownSpeakerThread() {
        super("Timers Thread");
    }

    @Override
    public void run() {
        while (true) {
            synchronized (timers) {
                for (int i = 0; i < timers.size(); i++) {
                    TownSpeakerWdg timer = timers.get(i);
                    if (!timer.active || timer.ui == null)
                        continue;

                    Session sess = timer.ui.sess;
                    if (!sess.state.equals("") || sess.glob.serverEpoch == 0)
                        continue;

                    timer.elapsed = (long)(sess.glob.globtime() * 1000 / Glob.SERVER_TIME_RATIO) - timer.start;
                    timer.updateRemaining();

                    if (timer.elapsed >= timer.duration) {
                        timer.done();
                        i--;
                    }
                }
            }
            try {
                sleep(1000);
            } catch (InterruptedException e) {
            }
        }
    }

    public TownSpeakerWdg add(String name, long duration, String channelname, long start) {
        synchronized (timers) {
            TownSpeakerWdg timer = new TownSpeakerWdg(name, duration, channelname, start);
            timers.add(timer);
            return timer;
        }
    }

    public void remove(TownSpeakerWdg timer) {
        synchronized (timers) {
            timers.remove(timer);
        }
    }

    public List<TownSpeakerWdg> getall() {
        synchronized (timers) {
            return timers;
        }
    }

    public void save() {
        synchronized (timers) {
            JSONObject[] timersjson = new JSONObject[timers.size()];
            for (int i = 0; i < timers.size(); i++) {
                final TownSpeakerWdg timer = timers.get(i);
                timersjson[i] = new JSONObject()
                        .put("name", timer.name)
                        .put("duration", timer.duration)
                        .put("channelname", timer.channelname)
                        .put("start", timer.active ? timer.start : 0);
            }
            Utils.setprefjsona("townspeakers", timersjson);
        }
    }

    public void load() {
        JSONObject[] tstarr = Utils.getprefjsona("townspeakers", null);
        if (tstarr == null)
            return;
        for (int i = 0; i < tstarr.length; i++) {
            JSONObject t = tstarr[i];
            long start = 0;
            try {
                start = t.getLong("start");
            } catch (JSONException e) {
            }
            add(t.getString("name"), t.getLong("duration"), t.getString("channelname"), start);
        }
    }
}

