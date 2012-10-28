////Error reading included file Templates/Classes/../Licenses/license-gplv3classpath.txt
//package eu.mihosoft.vrl.io.vrlx;
//
//import eu.mihosoft.vrl.visual.Canvas;
//import java.util.Collection;
//import java.util.HashMap;
//
///**
// *
// * @author Michael Hoffer <info@michaelhoffer.de>
// */
//public final class VLoadTaskProvider implements LoadTaskProvider {
//    private HashMap<String,LoadTask> loadingTasks =
//            new HashMap<String, LoadTask>();
//
//    public void addTask(String entryName, LoadTask t) {
//        loadingTasks.put(entryName, t);
//    }
//
//    public LoadTask getTask(String entryName) {
//        return loadingTasks.get(entryName);
//    }
//
//    @Override
//    public void perFormTasks(Canvas canvas, Collection<SessionEntry> entries) {
//        for (SessionEntry sE : entries) {
//            LoadTask t = getTask(sE.getName());
//            if (t!=null) {
//                t.load(canvas, sE);
//            }
//        }
//    }
//}
