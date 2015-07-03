////Error reading included file Templates/Classes/../Licenses/license-gplv3classpath.txt
//package eu.mihosoft.vrl.system;
//
//import eu.mihosoft.vrl.visual.ActionGroup;
//import eu.mihosoft.vrl.visual.Canvas;
//import eu.mihosoft.vrl.visual.CanvasStyleManager;
//import eu.mihosoft.vrl.visual.Dock;
//import eu.mihosoft.vrl.visual.Action;
//import eu.mihosoft.vrl.visual.Style;
//
///**
// *
// * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
// */
//class DefaultAPI implements PluginAPI {
//
//    private Canvas canvas;
//
//    public DefaultAPI(Canvas canvas) {
//        this.canvas = canvas;
//    }
//
//    @Override
//    public Dock getDock() {
//        return canvas.getDock();
//    }
//
//    @Override
//    public Canvas getCanvas() {
//        return canvas;
//    }
//
//    @Override
//    public void addAction(Action a, String destination) {
//        if (!VRL.getActionDelegator().addAction(a, destination)) {
//            canvas.getActionDelegator().addAction(a, destination);
//        }
//    }
//
//    @Override
//    public void addSeparator(String destination) {
//        if (!VRL.getActionDelegator().addSeparator(destination)) {
//            canvas.getActionDelegator().addSeparator(destination);
//        }
//    }
//
//    @Override
//    public void addAction(ActionGroup a, String destination) {
//        if (!VRL.getActionDelegator().addAction(a, destination)) {
//            canvas.getActionDelegator().addAction(a, destination);
//        }
//    }
//
//    @Override
//    public CanvasStyleManager getStyleManager() {
//        return canvas.getStyleManager();
//    }
//
//    @Override
//    public void addStyle(Style style) {
//        VRL.addStyle(style);
//    }
//}
