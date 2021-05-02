package org.vincentyeh.IMG2PDF.commandline.action;

import org.vincentyeh.IMG2PDF.SharedSpace;

public class MainAction implements Action {
    private final Action subAction;

    public MainAction(Action subAction) {
        this.subAction = subAction;
    }

    @Override
    public void start() throws Exception {
        System.out.println("##IMG2PDF##");
        System.out.printf("%s: %s", SharedSpace.getResString("public.info.developer"), SharedSpace.Configuration.DEVELOPER);
        System.out.printf("\n%s: %s\n", SharedSpace.getResString("public.info.version"), SharedSpace.Configuration.PROGRAM_VER);
        System.out.println("-----------------------");
        subAction.start();
    }

}
