package haven.townspeaker;


import haven.*;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class TownSpeakerEditWnd extends Window {

    public TownSpeakerEditWnd(String cap, final GameUI gui, String timername, long duration, String channelName, final TownSpeakerWdg timer) {
        super(new Coord(455, 100), cap);
        
        add(new Label("Speech"), new Coord(15, 10));
        final TextEntry txtname = new TextEntry(200, timername != null ? timername : "");
        add(txtname, new Coord(15, 30));

        long ds = duration / 1000;
        int hours = (int) (ds / 3600);
        int minutes = (int) ((ds % 3600) / 60);

        add(new Label("Hours"), new Coord(225, 10));
        final TextEntry txthours = new TextEntry(50, hours != 0 ? hours + "" : "") {
        };
        add(txthours, new Coord(225, 30));

        add(new Label("Minutes"), new Coord(285, 10));
        final TextEntry txtminutes = new TextEntry(50, minutes != 0 ? minutes + "" : "") {
        };
        add(txtminutes, new Coord(285, 30));
        
        add(new Label("channel"), new Coord(345,10));
        final TextEntry txtchannelname = new TextEntry(100, channelName != null ? channelName : "Area Chat");
        add(txtchannelname, new Coord(345, 30));

        Button add;
        if (timer != null) {
            add = new Button(60, "Save") {
                @Override
                public void click() {
                    if (timer.active)
                        timer.stop();
                    timer.name = txtname.text;
                    timer.channelname = txtchannelname.text;
                    int hours = Integer.parseInt(txthours.text.equals("") ? "0" : txthours.text);
                    int minutes = Integer.parseInt(txtminutes.text.equals("") ? "0" : txtminutes.text);
                    timer.duration = (60 * hours + minutes) * 60 * 1000;
                    timer.updateName();
                    timer.updateChannelname();
                    timer.updateDuration();
                    Glob.townspeakerThread.save();
                    parent.reqdestroy();
                }
            };
        } else {
            add = new Button(60, "Add") {
                @Override
                public void click() {
                    long hours = Long.parseLong(txthours.text.equals("") ? "0" : txthours.text);
                    long minutes = Long.parseLong(txtminutes.text.equals("") ? "0" : txtminutes.text);
                    long duration = (60 * hours + minutes) * 60 * 1000;
                    int y = 0;
                    List<TownSpeakerWdg> timers = Glob.townspeakerThread.getall();
                    for (TownSpeakerWdg timer : timers) {
                        if (timer.c.y + TownSpeakerWdg.height > y)
                            y = timer.c.y + TownSpeakerWdg.height;
                    }
                    gui.townspeakerwnd.port.cont.add(Glob.townspeakerThread.add(txtname.text, duration,txtchannelname.text, 0), new Coord(0, y));
                    Glob.townspeakerThread.save();
                    gui.townspeakerwnd.resize();
                    parent.reqdestroy();
                }
            };
        }
        add(add, new Coord(15, 70));

        Button cancel = new Button(60, "Cancel") {
            @Override
            public void click() {
                parent.reqdestroy();
            }
        };
        add(cancel, new Coord(275, 70));
    }

    @Override
    public void wdgmsg(Widget sender, String msg, Object... args) {
        if (sender == cbtn)
            reqdestroy();
        else
            super.wdgmsg(sender, msg, args);
    }

    public TownSpeakerEditWnd(String cap, final GameUI gui) {
        this(cap, gui, null, 0,null, null);
    }
}
