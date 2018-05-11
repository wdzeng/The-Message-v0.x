package the_message_client;

import java.awt.*;
import javax.swing.text.*;

public class Doc {

	StyledDocument doc;

	public StyledDocument get() {
		return doc;
	}

	public Doc(Setting sett) {
		doc = new DefaultStyledDocument();
		newStyle("1", sett.getCompTextTitle(), true, Setting.skRed1);
		newStyle("2", sett.getCompTextTitle(), true, Setting.skBlue1);
		newStyle("3", sett.getCompTextTitle(), true, Setting.team3_col);
		newStyle("4", sett.getCompTextTitle(), true, Color.WHITE);
		newStyle("0", sett.getCompText(), false, Color.WHITE);
		newStyle("5", sett.getCompText(), true, Color.YELLOW);
		newStyle("6", sett.getCompText(), false, Color.RED);
		MutableAttributeSet attr = new SimpleAttributeSet();
		StyleConstants.setLineSpacing(attr, 0.12f);
		doc.setParagraphAttributes(0, doc.getLength() - 1, attr, false);
	}

	public void add(int style, String str) {
		try {
			doc.insertString(doc.getLength(), str, doc.getStyle(String.valueOf(style)));
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public void newStyle(String styleName, int size, boolean bold, Color color) {
		Style sys = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		try {
			doc.removeStyle(styleName);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Style s = doc.addStyle(styleName, sys); 
		StyleConstants.setFontSize(s, size); 
		StyleConstants.setBold(s, bold); 
		StyleConstants.setItalic(s, false); 
		StyleConstants.setUnderline(s, false); 
		StyleConstants.setForeground(s, color); 
		StyleConstants.setFontFamily(s, "å¾®è?Ÿæ­£é»‘é??");
	}
}