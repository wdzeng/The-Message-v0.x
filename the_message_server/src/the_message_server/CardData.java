package the_message_server;

import java.util.ArrayList;

public class CardData {

	public static String getCardFunc(int id) {
		String s = null;
		switch (id) {
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
		case 8:
		case 9:
		case 10:
		case 11:
		case 12:
		case 13:
		case 14:
		case 15:
		case 16:
		case 17:
		case 18:
			s = "試探";
			break;
		case 19:
		case 20:
		case 21:
		case 22:
		case 23:
		case 24:
		case 25:
		case 26:
		case 27:
		case 28:
		case 29:
		case 30:
		case 31:
		case 32:
			s = "鎖定";
			break;
		case 33:
		case 34:
		case 35:
		case 36:
		case 37:
		case 38:
		case 39:
		case 40:
			s = "調虎離山";
			break;
		case 41:
		case 42:
		case 43:
		case 44:
		case 45:
		case 46:
		case 47:
			s = "破譯";
			break;
		case 48:
		case 49:
		case 50:
		case 51:
		case 52:
		case 53:
			s = "退回";
			break;
		case 54:
		case 55:
			s = "真偽莫辨";
			break;
		case 56:
		case 57:
		case 58:
		case 59:
		case 60:
		case 61:
		case 62:
		case 63:
			s = "截獲";
			break;
		case 64:
		case 65:
		case 66:
		case 67:
		case 68:
		case 69:
			s = "燒燬";
			break;
		case 70:
		case 71:
		case 72:
		case 73:
		case 74:
		case 75:
		case 76:
		case 77:
		case 78:
		case 79:
		case 80:
		case 81:
			s = "識破";
			break;
		default:
			new CardException("不正確的id: " + id).printStackTrace();
		}
		return s;
	}

	public static String getCardColor(int id) {

		// color為中文，一字

		String s = null;
		if (id <= 18) {
			if (id % 3 == 1)
				s = "r";
			else if (id % 3 == 2)
				s = "b";
			else
				s = "k";
		} else {
			switch (id) {
			case 19:
			case 20:
			case 21:
			case 22:
			case 23:
				s = "r";
				break;
			case 24:
			case 25:
			case 26:
			case 27:
			case 28:
				s = "b";
				break;
			case 29:
			case 30:
			case 31:
			case 32:
				s = "k";
				break;
			case 33:
			case 34:
				s = "r";
				break;
			case 35:
			case 36:
				s = "b";
				break;
			case 37:
			case 38:
			case 39:
			case 40:
				s = "k";
				break;
			case 41:
			case 42:
			case 43:
				s = "r";
				break;
			case 44:
			case 45:
			case 46:
				s = "b";
				break;
			case 47:
				s = "k";
				break;
			case 48:
			case 49:
				s = "r";
				break;
			case 50:
			case 51:
				s = "b";
				break;
			case 52:
			case 53:
				s = "k";
				break;
			case 54:
				s = "r";
				break;
			case 55:
				s = "b";
				break;
			case 56:
				s = "r";
				break;
			case 57:
				s = "b";
				break;
			case 58:
			case 59:
			case 60:
			case 61:
			case 62:
			case 63:
				s = "k";
				break;
			case 64:
				s = "r";
				break;
			case 65:
				s = "b";
				break;
			case 66:
			case 67:
			case 68:
			case 69:
				s = "k";
				break;
			case 70:
			case 71:
			case 72:
				s = "r";
				break;
			case 73:
			case 74:
			case 75:
				s = "b";
				break;
			case 76:
			case 77:
			case 78:
			case 79:
			case 80:
			case 81:
				s = "k";
				break;
			}
		}
		return s;
	}

	public static String getCardName(int id) {
		String s = null;
		if (id <= 18)
			s = String.valueOf(id);
		else {
			switch (id) {
			case 19:
			case 20:
			case 21:
			case 22:
			case 23:
				s = "紅鎖定";
				break;
			case 24:
			case 25:
			case 26:
			case 27:
			case 28:
				s = "藍鎖定";
				break;
			case 29:
			case 30:
			case 31:
			case 32:
				s = "黑鎖定";
				break;
			case 33:
			case 34:
				s = "紅調虎";
				break;
			case 35:
			case 36:
				s = "藍調虎";
				break;
			case 37:
			case 38:
			case 39:
			case 40:
				s = "黑調虎";
				break;
			case 41:
			case 42:
			case 43:
				s = "紅破譯";
				break;
			case 44:
			case 45:
			case 46:
				s = "藍破譯";
				break;
			case 47:
				s = "黑破譯";
				break;
			case 48:
			case 49:
				s = "紅退回";
				break;
			case 50:
			case 51:
				s = "藍退回";
				break;
			case 52:
			case 53:
				s = "黑退回";
				break;
			case 54:
				s = "紅真偽";
				break;
			case 55:
				s = "藍真偽";
				break;
			case 56:
				s = "紅截獲";
				break;
			case 57:
				s = "藍截獲";
				break;
			case 58:
			case 59:
			case 60:
			case 61:
			case 62:
			case 63:
				s = "黑截獲";
				break;
			case 64:
				s = "紅燒燬";
				break;
			case 65:
				s = "藍燒燬";
				break;
			case 66:
			case 67:
			case 68:
			case 69:
				s = "黑燒燬";
				break;
			case 70:
			case 71:
			case 72:
				s = "紅識破";
				break;
			case 73:
			case 74:
			case 75:
				s = "藍識破";
				break;
			case 76:
			case 77:
			case 78:
			case 79:
			case 80:
			case 81:
				s = "黑識破";
				break;
			case 99:
				s = "99";
				break;
			}
		}
		return s;
	}

	public static String getCardsByFunc(ArrayList<Integer> cards, String type) {
		String res = "";
		for (int card : cards)
			if (getCardFunc(card).equals(type))
				res += card + ",";
		return res;
	}

	public static String getCardsByColor(ArrayList<Integer> cards, String color) {
		String res = "";
		for (int card : cards)
			if (getCardColor(card).equals(color))
				res += card + ",";
		return res;
	}

	public static String getCards(ArrayList<Integer> cards) {
		String res = "";
		for (int card : cards)
			res += card + ",";
		return res;
	}

	public static String getCardsInColorOrder(String cards) {
		String[] cs = cards.split(",");
		ArrayList<Integer> r = new ArrayList<>(), b = new ArrayList<>(), k = new ArrayList<>();
		for (String c : cs) {
			int id = Integer.parseInt(c);
			switch (getCardColor(id)) {
			case "r":
				r.add(id);
				break;
			case "b":
				b.add(id);
				break;
			case "k":
				k.add(id);
				break;
			}
		}
		return getCards(r) + getCards(b) + getCards(k);
	}

	public static boolean isTrue(int id) {// 此情報是否為真
		String color = getCardColor(id);
		if (color.equals("k"))
			return false;
		else
			return true;
	}

	public static boolean isFalse(int[] ids) {// 此情報是否為真
		for (int id : ids)
			if (!isTrue(id))
				return true;
		return false;
	}
	
	public static boolean isTrue(int[] ids) {// 此情報是否為真
		for (int id : ids)
			if (isTrue(id))
				return true;
		return false;
	}

	public static int getColorCount(ArrayList<Integer> cards, String color) {

		// color一律用中文，一字

		int num = 0;
		if (color.equals("假"))
			color = "k";
		if (color.equals("r") || color.equals("b") || color.equals("k")) {
			for (int cardId : cards)
				if (getCardColor(cardId).equals(color))
					num++;
		} else if (color.equals("真")) {
			for (int cardId : cards)
				if (isTrue(cardId))
					num++;
		} else
			new CardException("傳入錯誤的引數: " + color).printStackTrace();
		return num;
	}

	public static int getFuncCount(ArrayList<Integer> cards, String func) {
		int num = 0;
		for (int id : cards) {
			if (getCardFunc(id).equals(func))
				num++;
		}
		return num;
	}

	public static String getIntelligenceType(int id) {
		if (id <= 47 && id > 0)
			return "密電";
		else if (id <= 55 && id > 47)
			return "文本";
		else if (id > 55 && id <= 81)
			return "直達";
		else
			new LogicException("不正確的卡牌編號: id = " + id);
		return null;
	}

}
