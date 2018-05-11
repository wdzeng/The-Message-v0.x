package the_message_server;

import java.util.*;

public class Shuffle {

	public static final String[] chrs = { "老鬼", "老槍", "老金", "譯電員", "黃雀", "黑玫瑰", "鋼鐵特工", "閃靈", "浮萍", "峨嵋風", "六姐", "大美女",
			"致命香水", "柒佰", "禮服蒙面人", "刀鋒", "小馬哥", "戴笠", "職業殺手", "蝮蛇", "福爾摩斯", "情報處長", "小白", "貝雷帽", "怪盜九九" };

	public static ArrayList<String> getCharacterCards(int total) {

		ArrayList<Integer> cards0 = new ArrayList<>(25);
		for (int i = 0; i < 25; i++)
			cards0.add(i);
		ArrayList<Integer> A = new ArrayList<>(13);
		ArrayList<Integer> B = new ArrayList<>(12);
		for (int i = 1, x, y; i <= 25; i++) {
			x = new Random().nextInt(3) + 10;
			y = new Random().nextInt(5) + 5;
			for (int j = x + y, tmp; j >= y; j--) {
				tmp = cards0.get(j);
				cards0.remove(j);
				cards0.add(0, tmp);
			}
			A = new ArrayList<>(13);
			B = new ArrayList<>(12);
			for (int k = 0; k <= 12; k++)
				A.add(cards0.get(k));
			for (int k = 13; k <= 24; k++)
				B.add(cards0.get(k));
			cards0 = new ArrayList<>(25);
			for (int k = 0; k <= 11; k++) {
				cards0.add(A.get(k));
				cards0.add(B.get(k));
			}
			cards0.add(A.get(12));
		}

		ArrayList<String> cards = new ArrayList<>(total * 2);
		for (int i = 0; i < total * 2; i++)
			cards.add(chrs[(cards0.get(i))]);
		
		return cards;
	}

	public static ArrayList<Integer> getGameCards() {
		ArrayList<Integer> cards = new ArrayList<>(81);
		for (int i = 1; i <= 81; i++)
			cards.add(i);
		ArrayList<Integer> A = new ArrayList<>(41);
		ArrayList<Integer> B = new ArrayList<>(40);

		for (int i = 1, x, y; i <= 500; i++) {
			x = new Random().nextInt(20) + 10;
			y = new Random().nextInt(30) + 20;
			for (int j = x + y, tmp; j >= y; j--) {
				tmp = cards.get(j);
				cards.remove(j);
				cards.add(0, tmp);
			}
			A = new ArrayList<>(41);
			B = new ArrayList<>(40);
			for (int k = 0; k <= 40; k++)
				A.add(cards.get(k));
			for (int k = 41; k <= 80; k++)
				B.add(cards.get(k));
			cards = new ArrayList<>(81);
			for (int k = 0; k <= 39; k++) {
				cards.add(A.get(k));
				cards.add(B.get(k));
			}
			cards.add(A.get(40));
		}

		return cards;
	}

	public static ArrayList<Integer> getTeams(int total) {
		ArrayList<Integer> teams = new ArrayList<>();
		switch (total) {
		case 9:
			teams.add(3);
		case 8:
			teams.add(3);
		case 7:
			teams.add(1);
			teams.add(1);
			teams.add(2);
			teams.add(2);
		case 3:
			teams.add(3);
		case 2:
			teams.add(2);
			teams.add(1);
			break;
		case 6:
			teams.add(3);
		case 5:
			teams.add(3);
			teams.add(1);
			teams.add(2);
			teams.add(1);
			teams.add(2);
			break;
		case 4:
			teams.add(1);
			teams.add(2);
			teams.add(3);
			teams.add(3);
			break;
		default:
			new ShuffleException("不正確的玩家數量: " + total).printStackTrace();
			return null;
		}

		for (int i = 1, x, y, tmp; i <= 99; i++) {
			x = new Random().nextInt(total);
			y = new Random().nextInt(total);
			tmp = teams.get(x);
			teams.set(x, teams.get(y));
			teams.set(y, tmp);
		}

		return teams;
	}

	public static ArrayList<Integer> shuffle(ArrayList<Integer> list) {
		int num = list.size();
		for (int i = 1, x, y, tmp; i <= 6666; i++) {
			x = new Random().nextInt(num);
			y = new Random().nextInt(num);
			tmp = list.get(x);
			list.set(x, list.get(y));
			list.set(y, tmp);
		}
		return list;
	}

	public static ArrayList<Integer> getSeats(int total) {
		ArrayList<Integer> al = new ArrayList<>();
		for (int i = 0; i < total; i++)
			al.add(i);
		return shuffle(al);

	}

	public static int[] getRandom(int n, int t) {
		// n表示要幾個亂數，t表示亂數範圍從0～t-1
		if (n > t)
			new ShuffleException("使用 Shuffle.getRandom() 發生錯誤，因為 n > t，其中 n = " + n + ", t = " + t).printStackTrace();
		ArrayList<Integer> r = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			int rr;
			do {
				rr = new Random().nextInt(t);
			} while (r.contains(rr));
			r.add(rr);
		}
		int[] rs = new int[n];
		for (int i = 0; i < n; i++)
			rs[i] = r.get(i);
		return rs;
	}

	public static int random(ArrayList<Integer> list) {
		return list.get(new Random().nextInt(list.size()));
	}

	public static int randomEx(int p, int t) {
		int i;
		while (true)
			if ((i = new Random().nextInt(t)) != p)
				return i;
	}

	public static void main(String[] args) {

		for (int i = 1; i < 100; i++) {
			System.out.print(new Random().nextInt(25) + ",");
			if (i % 10 == 0)
				System.out.println();
		}

	}

}
