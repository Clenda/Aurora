package haven.townspeaker;


import haven.*;
import haven.Button;
import haven.Window;

import java.awt.*;
import java.util.List;

public class TownSpeakerWnd extends Window {
    public final GameUI gui;
    public static final int WIDTH = 570;
    public final Scrollport port;
    private final static int MAX_ITEMS = 13;

    public TownSpeakerWnd(final GameUI gui) {
        super(Coord.z, "Town Speaker");
        this.gui = gui;

        Button btna = new Button(50, "Add") {
            public void click() {
                parent.parent.add(new TownSpeakerEditWnd("Create New Speech", gui), new Coord(gui.sz.x / 2 - 200, gui.sz.y / 2 - 200));
            }
        };
        add(btna, new Coord(20, 10));

        List<TownSpeakerWdg> timers = Glob.townspeakerThread.getall();
        if (timers.size() == 0)
            Glob.townspeakerThread.load();
        timers = Glob.townspeakerThread.getall();

        int portHeight = timers.size() > MAX_ITEMS ? TownSpeakerWdg.height * MAX_ITEMS : timers.size() * TownSpeakerWdg.height;
        port = new Scrollport(new Coord(WIDTH - 20 - 15, portHeight), TownSpeakerWdg.height) {
            @Override
            public void draw(GOut g) {
                g.chcolor(0, 0, 0, 128);
                g.frect(Coord.z, sz);
                g.chcolor();
                super.draw(g);
            }
        };
        add(port, new Coord(20, 50));

        for (int i = 0; i < timers.size(); i++)
            port.cont.add(timers.get(i), new Coord(0, i * TownSpeakerWdg.height));

        resize();
    }

    public void resize() {
        List<TownSpeakerWdg> timers = Glob.townspeakerThread.getall();
        int portHeight = timers.size() > MAX_ITEMS ? TownSpeakerWdg.height * MAX_ITEMS : timers.size() * TownSpeakerWdg.height;
        port.resize(port.sz.x, portHeight);
        port.cont.update();
        port.bar.resize(portHeight);
        port.bar.changed();
        super.resize(WIDTH, portHeight + 60);
    }

    @Override
    public void wdgmsg(Widget sender, String msg, Object... args) {
        if (sender == cbtn) {
            hide();
        } else {
            super.wdgmsg(sender, msg, args);
        }
    }

}
