package haven.automation;


import haven.*;

public class SliceCheeseTray implements Runnable, WItemDestroyCallback {
    private GameUI gui;
    private boolean cheesetraydone;
    private static final int TIMEOUT = 2000;
    private static final int DELAY = 8;

    public SliceCheeseTray(GameUI gui) {
        this.gui = gui;
    }

    @Override
    public void run() {
        WItem tray;
		while ((tray = Utils.findItemInInv(gui.maininv, "gfx/invobjs/cheesetray")) != null) {
        	cheesetraydone = false;
        	tray.registerDestroyCallback(this);
            FlowerMenu.setNextSelection("Slice up");
            gui.ui.lcc = tray.rootpos();
            tray.item.wdgmsg("iact", tray.c, 0);

            int timeout = 0;
            while (!cheesetraydone) {
                timeout += DELAY;
                if (timeout >= TIMEOUT)
                    return;
                try {
                    Thread.sleep(DELAY);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }

    @Override
    public void notifyDestroy() {
    	cheesetraydone = true;
    }
}
