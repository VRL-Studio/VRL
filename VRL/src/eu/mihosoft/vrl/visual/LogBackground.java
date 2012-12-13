/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.mihosoft.vrl.visual;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import javax.swing.UIManager;

/**
 * EXPERIMENTAL! MIGHT REPLACE FullscreenLogView which is JTextArea based.
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class LogBackground implements GlobalBackgroundPainter {
    
    private Canvas mainCanvas;
    private String text;

    public LogBackground(Canvas mainCanvas) {
        this.mainCanvas = mainCanvas;
    }
    
    public void paint(Graphics g, int x, int y, String input) {

        Graphics2D g2d = (Graphics2D) g;

        int width = g.getClipBounds().width;

        String[] paragraphs = input.split("\n");
        for (String p : paragraphs) {
            AttributedString attributedString = new AttributedString(p);
            attributedString.addAttribute(TextAttribute.FONT, (Font) UIManager
                    .get("Label.font"));
            Color color = (Color) UIManager.get("Label.foreground");

            attributedString.addAttribute(TextAttribute.FOREGROUND, color);

            AttributedCharacterIterator characterIterator = attributedString
                    .getIterator();
            FontRenderContext fontRenderContext = g2d.getFontRenderContext();
            LineBreakMeasurer measurer = new LineBreakMeasurer(characterIterator,
                    fontRenderContext);
            while (measurer.getPosition() < characterIterator.getEndIndex()) {
                TextLayout textLayout = measurer.nextLayout(width);
                y += textLayout.getAscent();
                textLayout.draw(g2d, x, y);
                y += textLayout.getDescent() + textLayout.getLeading();
            }
        }
    }

    @Override
    public void paintGlobal(Graphics g) {
        paint(g, 10, 10, text);
    }
    
    public void setText(String text) {

        this.text = text;
        
        mainCanvas.repaint();
    }
}
