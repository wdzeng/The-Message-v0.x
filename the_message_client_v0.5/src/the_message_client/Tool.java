package the_message_client;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Tool {

	public static void main(String[] args) {
		toGray((ImageIcon) Client.in("character/老鬼"));
	}

	public Tool() {
	}

	public static ImageIcon fix(ImageIcon ii, int w, int h) {
		if (ii == null)
			return null;
		return new ImageIcon((ii.getImage()).getScaledInstance(w, h, Image.SCALE_SMOOTH));
	}

	public static ImageIcon goodfix(ImageIcon ii, int w, int h) {
		int iw = ii.getIconWidth();
		int ih = ii.getIconHeight();
		float irate = (float) iw / ih;
		float rate = (float) w / h;
		if (irate < rate)
			return new ImageIcon((ii.getImage()).getScaledInstance(Math.round(h * irate), h, Image.SCALE_SMOOTH));
		else
			return new ImageIcon((ii.getImage()).getScaledInstance(w, Math.round(w / irate), Image.SCALE_SMOOTH));
	}

	public static int toFileId(int id) {
		if (id <= 18 && id >= 0)
			return id;
		switch (id) {
		case 19:
		case 20:
		case 21:
		case 22:
		case 23:
			return 19;
		case 24:
		case 25:
		case 26:
		case 27:
		case 28:
			return 20;
		case 29:
		case 30:
		case 31:
		case 32:
			return 21;
		case 33:
		case 34:
			return 22;
		case 35:
		case 36:
			return 23;
		case 37:
		case 38:
		case 39:
		case 40:
			return 24;
		case 41:
		case 42:
		case 43:
			return 28;
		case 44:
		case 45:
		case 46:
			return 29;
		case 47:
			return 30;
		case 48:
		case 49:
			return 31;
		case 50:
		case 51:
			return 32;
		case 52:
		case 53:
			return 33;
		case 54:
			return 40;
		case 55:
			return 41;
		case 56:
			return 25;
		case 57:
			return 26;
		case 58:
		case 59:
		case 60:
		case 61:
		case 62:
		case 63:
			return 27;
		case 64:
			return 34;
		case 65:
			return 35;
		case 66:
		case 67:
		case 68:
		case 69:
			return 36;
		case 70:
		case 71:
		case 72:
			return 37;
		case 73:
		case 74:
		case 75:
			return 38;
		case 76:
		case 77:
		case 78:
		case 79:
		case 80:
		case 81:
			return 39;
		case 97:
			return 42;
		case 98:
			return 43;
		case 99:
			return 44;
		case 100:
			return 45;
		}
		new LogicException("id不對: " + id).printStackTrace();
		return -1;
	}

	public static ImageIcon setAlpha(ImageIcon img, int alpha) {

		BufferedImage bi = new BufferedImage(img.getIconWidth(), img.getIconHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2D = (Graphics2D) bi.getGraphics();
		g2D.drawImage(img.getImage(), 0, 0, img.getImageObserver());

		for (int j1 = bi.getMinY(); j1 < bi.getHeight(); j1++) {
			for (int j2 = bi.getMinX(); j2 < bi.getWidth(); j2++) {
				Color c = new Color(bi.getRGB(j2, j1), true);
				Color color;
				if (c.getAlpha() == 0)
					color = new Color(c.getRed(), c.getGreen(), c.getBlue(), 0);
				else
					color = new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
				bi.setRGB(j2, j1, color.getRGB());
			}
		}
		g2D.drawImage(bi, 0, 0, img.getImageObserver());
		return new ImageIcon(bi);

	}

	public static ImageIcon toGray(ImageIcon image) {
		Image img = image.getImage();
		BufferedImage bi = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_BYTE_GRAY);
		Graphics g = bi.getGraphics();
		g.drawImage(img, 0, 0, null);
		g.dispose();
		return new ImageIcon(bi);
	}

	public static ImageIcon cut(ImageIcon img, Rectangle loc) {
		return new ImageIcon(new BufferedImage(img.getIconWidth(), img.getIconHeight(), BufferedImage.TYPE_4BYTE_ABGR)
				.getSubimage(loc.x, loc.y, loc.width, loc.height));
	}

	public static ImageIcon transparent(Color color, Dimension d) {
		BufferedImage bi = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = bi.createGraphics();
		bi = g2d.getDeviceConfiguration().createCompatibleImage(d.width, d.height, Transparency.TRANSLUCENT);
		g2d.dispose();
		g2d = bi.createGraphics();
		g2d.setColor(color);
		g2d.fillRect(0, 0, d.width, d.height);
		g2d.dispose();
		return new ImageIcon(bi);
	}

	public static ImageIcon white(Dimension d, int alpha) {
		return transparent(new Color(255, 255, 255, alpha), d);
	}

	public static ImageIcon black(Dimension d, int alpha) {
		return transparent(new Color(0, 0, 0, alpha), d);
	}

	public static ImageIcon todaysChr() {
		String chr = null;
		switch (Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
		case 1:
		case 31:
			chr = "老鬼";
			break;
		case 2:
			chr = "老槍";
			break;
		case 3:
			chr = "老金";
			break;
		case 4:
			chr = "譯電員";
			break;
		case 5:
			chr = "黑玫瑰";
			break;
		case 6:
			chr = "血牡丹";
			break;
		case 7:
			chr = "峨嵋風";
			break;
		case 8:
			chr = "鋼鐵特工";
			break;
		case 9:
			chr = "情報獵手";
			break;
		case 10:
			chr = "議長";
			break;
		case 11:
			chr = "浮萍";
			break;
		case 12:
			chr = "閃靈";
			break;
		case 13:
			chr = "黃雀";
			break;
		case 14:
			chr = "六姐";
			break;
		case 15:
			chr = "柒佰";
			break;
		case 16:
			chr = "貝雷帽";
			break;
		case 17:
			chr = "職業殺手";
			break;
		case 18:
			chr = "刀鋒";
			break;
		case 19:
			chr = "大美女";
			break;
		case 20:
			chr = "怪盜九九";
			break;
		case 21:
			chr = "戴笠";
			break;
		case 22:
			chr = "小馬哥";
			break;
		case 23:
			chr = "小白";
			break;
		case 24:
			chr = "千金小姐";
			break;
		case 25:
			chr = "禮服蒙面人";
			break;
		case 26:
			chr = "情報處長";
			break;
		case 27:
			chr = "蝮蛇";
			break;
		case 28:
			chr = "福爾摩斯";
			break;
		case 29:
			chr = "出雲龍";
			break;
		case 30:
			chr = "致命香水";
			break;
		}
		return (ImageIcon) Client.in("character/" + chr);
	}

}
