/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.ui.codevisualization;

import java.util.regex.Pattern;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;
import jfxtras.scene.control.window.CloseIcon;
import jfxtras.scene.control.window.Window;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class VCodeEditor {

    private Window window;
    private TextArea textArea;

    private static final String[] KEYWORDS = new String[]{
        "abstract", "assert", "boolean", "break", "byte",
        "case", "catch", "char", "class", "const",
        "continue", "default", "do", "double", "else",
        "enum", "extends", "final", "finally", "float",
        "for", "goto", "if", "implements", "import",
        "instanceof", "int", "interface", "long", "native",
        "new", "package", "private", "protected", "public",
        "return", "short", "static", "strictfp", "super",
        "switch", "synchronized", "this", "throw", "throws",
        "transient", "try", "void", "volatile", "while"
    };

    private static final Pattern KEYWORD_PATTERN = Pattern.compile("\\b(" + String.join("|", KEYWORDS) + ")\\b");

    public VCodeEditor(String code) {
        init(code);
    }

    private void init(String code) {
        window = new Window("Code-Editor") {

        };
        window.getLeftIcons().add(new CloseIcon(window));

        textArea = new TextArea(code);
        textArea.setCacheHint(CacheHint.SPEED);

//        textArea.setBackground(Background.EMPTY);
//        textArea.s
        textArea.setStyle(".txtarea .scroll-pane .content{-fx-background-color: red; -fx-text-fill: white;}");

        Node content = textArea.lookup("scroll-pane");
        if (content !=null) {
            ((Region) content).setBackground(Background.EMPTY);
            System.out.println("CONTENT");
            System.exit(0);
        }
//        textArea.get

//        textArea.replaceText(code);
//        textArea.setStyle(".keyword {\n"
//                + "    -fx-fill: brown;\n"
//                + "    -fx-font-weight: bold;\n"
//                + "}");
//        textArea.textProperty().addListener((ObservableValue<? extends String> observable, String oldText, String newText) -> {
//            Matcher matcher = KEYWORD_PATTERN.matcher(newText);
//            int lastKwEnd = 0;
//            StyleSpansBuilder<Collection<String>> spansBuilder
//                    = new StyleSpansBuilder<>();
//            while (matcher.find()) {
//                spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
//                spansBuilder.add(Collections.singleton("keyword"), matcher.end() - matcher.start());
//                lastKwEnd = matcher.end();
//            }
//            spansBuilder.add(Collections.emptyList(), newText.length() - lastKwEnd);
//            textArea.setStyleSpans(0, spansBuilder.create());
//        });
//        textArea.setPrefSize(400, 300);
        window.setPrefSize(400, 300);
//        textArea.setManaged(false);

//        window.addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
//
//            @Override
//            public void handle(MouseEvent t) {
//                 textArea.setManaged(true);
//            }
//        });
//        window.setContentPane(new AnchorPane());
        window.getContentPane().getChildren().add(textArea);

    }

    public Window getNode() {
        return window;
    }

    public TextArea getTextArea() {
        return textArea;
    }
}
