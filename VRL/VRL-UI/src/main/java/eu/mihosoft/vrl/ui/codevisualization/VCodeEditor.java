/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.ui.codevisualization;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.concurrent.Task;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;
import jfxtras.scene.control.window.CloseIcon;
import jfxtras.scene.control.window.Window;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;

/**
 *
 * @author Michael Hoffer &lt;info@michaelhoffer.de&gt;
 */
public class VCodeEditor {

    private Window window;
    private CodeArea codeArea;

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

    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = "\\;";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
            + "|(?<PAREN>" + PAREN_PATTERN + ")"
            + "|(?<BRACE>" + BRACE_PATTERN + ")"
            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
            + "|(?<STRING>" + STRING_PATTERN + ")"
            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
    );

    private ExecutorService executor;

    private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
        String text = codeArea.getText();
        Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
            @Override
            protected StyleSpans<Collection<String>> call() throws Exception {
                return computeHighlighting(text);
            }
        };
        executor.execute(task);
        return task;
    }

    private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
        codeArea.setStyleSpans(0, highlighting);
    }

    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        while (matcher.find()) {
            String styleClass
                    = matcher.group("KEYWORD") != null ? "keyword"
                    : matcher.group("PAREN") != null ? "paren"
                    : matcher.group("BRACE") != null ? "brace"
                    : matcher.group("BRACKET") != null ? "bracket"
                    : matcher.group("SEMICOLON") != null ? "semicolon"
                    : matcher.group("STRING") != null ? "string"
                    : matcher.group("COMMENT") != null ? "comment"
                    : null;
            /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    public VCodeEditor(String code) {
        init(code);
    }

    private void init(String code) {
        window = new Window("Code-Editor") {

        };
        window.getLeftIcons().add(new CloseIcon(window));

        codeArea = new CodeArea(code);
        codeArea.setCacheHint(CacheHint.SPEED);

        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
                .successionEnds(Duration.ofMillis(500))
                .supplyTask(this::computeHighlightingAsync)
                .awaitLatest(codeArea.richChanges())
                .filterMap(t -> {
                    if (t.isSuccess()) {
                        return Optional.of(t.get());
                    } else {
                        t.getFailure().printStackTrace();
                        return Optional.empty();
                    }
                })
                .subscribe(this::applyHighlighting);

        codeArea.setStyle(".txtarea .scroll-pane .content{-fx-background-color: red; -fx-text-fill: white;}");

        Node content = codeArea.lookup("scroll-pane");
        if (content != null) {
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
        window.getContentPane().getChildren().add(codeArea);

    }

    public Window getNode() {
        return window;
    }

    public CodeArea getTextArea() {
        return codeArea;
    }
}
