package haven.townspeaker;


import haven.*;
import haven.Label;

import java.awt.*;
import java.util.List;

public class TownSpeakerWdg extends Widget {
    public final static int height = 31;
    private final static int txty = 8;
    public String name;
    public String channelname;
    public long start, duration, elapsed;
    public boolean active = false;
    private haven.Label lbltime, lblname, lblchannel;
    private haven.Button btnstart, btnstop, btnedit;
    private Label btndel;

    public TownSpeakerWdg(String name, long duration,String channel, long start) {
        this.name = name;
        this.duration = duration;
        this.channelname = channel;

        sz = new Coord(530, height);
        lblname = new haven.Label(name, Text.num12boldFnd, Color.WHITE);
        add(lblname, new Coord(3, txty));
        lblchannel = new haven.Label(channel, Text.num12boldFnd, Color.WHITE);
        add(lblchannel, new Coord(190, txty));
        
        lbltime = new haven.Label(timeFormat(duration), Text.num12boldFnd, Color.WHITE);

        add(lbltime, new Coord(300, txty));

        btnstart = new haven.Button(50, "Start") {
            @Override
            public void click() {
                start();
            }
        };
        btnstop = new haven.Button(50, "Stop") {
            @Override
            public void click() {
                stop();
            }
        };
        btnstop.hide();
        btndel = new Label("\u2718", Text.delfnd, Color.RED) {
            @Override
            public boolean mousedown(Coord c, int button) {
                delete();
                return true;
            }
        };
        btnedit = new haven.Button(50, "Edit") {
            @Override
            public void click() {
                edit();
            }
        };

        add(btnstart, new Coord(380, 3));
        add(btnstop, new Coord(380, 3));
        add(btnedit, new Coord(444, 3));
        add(btndel, new Coord(505, 6));

        if (start != 0)
            start(start);
    }

    public void updateRemaining() {
        lbltime.settext(timeFormat(duration - elapsed));
    }

    public void updateDuration() {
        lbltime.settext(timeFormat(duration));
    }

    public void updateChannelname() {
        lblchannel.settext(channelname.length() > 21 ? channelname.substring(0, 20) : channelname);
    }

    public void updateName() {
        lblname.settext(name.length() > 21 ? name.substring(0, 20) : name);
    }

    private String timeFormat(long time) {
        long ts = time / 1000;
        return String.format("%02d:%02d.%02d", (int) (ts / 3600), (int) ((ts % 3600) / 60), (int) (ts % 60));
    }

    public void start() {
        start = (long)(ui.sess.glob.globtime() * 1000 / Glob.SERVER_TIME_RATIO);
        btnstart.hide();
        btnstop.show();
        active = true;
        Glob.townspeakerThread.save();
    }

    public void start(long start) {
        this.start = start;
        btnstart.hide();
        btnstop.show();
        active = true;
    }

    public void delete() {
        active = false;
        Glob.townspeakerThread.remove(this);
        reqdestroy();
        int y = this.c.y;
        List<TownSpeakerWdg> timers = Glob.townspeakerThread.getall();
        for (TownSpeakerWdg timer : timers) {
            if (timer.c.y > y)
                timer.c.y -= height;
        }
        gameui().townspeakerwnd.resize();
        Glob.townspeakerThread.save();
    }

    public void stop() {
        active = false;
        btnstart.show();
        btnstop.hide();
        updateDuration();
        Glob.townspeakerThread.save();
    }
    
    public void speech()
    {
    	if (gameui().vhand == null) {   // do not highlight when walking with an item
            for (Widget w = gameui().chat.lchild; w != null; w = w.prev) {
                if (w instanceof ChatUI.MultiChat) {
                    ChatUI.MultiChat chat = (ChatUI.MultiChat) w;
                    if (chat.name().equals(Resource.getLocString(Resource.BUNDLE_LABEL, this.channelname))) {
                        chat.send(this.name);
                        break;
                    }
                }
            }
    	}
    }

    public void done() {
        stop();
        speech();
        start();
    }

    public void edit() {
        GameUI gui = ((TownSpeakerWnd) parent.parent.parent).gui;
        gui.add(new TownSpeakerEditWnd("Edit Speech", gui, name, duration, channelname , this), new Coord(gui.sz.x / 2 - 200, gui.sz.y / 2 - 200));
    }
}
