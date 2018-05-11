package the_message_client;

import java.awt.Dimension;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class Initializer {

	private Setting set;
	private JLabel lb;

	public Initializer(Setting set, JLabel lb) throws FileNotFoundException, IOException, ClassNotFoundException {
		this.set = set;
		this.lb = lb;
	}

	@SuppressWarnings("unchecked")
	public void iniChrs() throws FileNotFoundException, IOException, ClassNotFoundException {
		final File characterDir = new File("image/character");
		if (!characterDir.exists())
			characterDir.mkdirs();
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("image/character/white.leo"))) {
			oos.writeObject(Tool.white(new Dimension(set.getChrWid(), set.getChrHei()), 153));
		}
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("image/character/black.leo"))) {
			oos.writeObject(Tool.black(new Dimension(set.getChrWid(), set.getChrHei()), 153));
		}
		Map<String, ImageIcon> characters;
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("res/characters.leo"))) {
			characters = (HashMap<String, ImageIcon>) ois.readObject();
		}
		Iterator<Entry<String, ImageIcon>> characterIter = characters.entrySet().iterator();
		while (characterIter.hasNext()) {
			Map.Entry<String, ImageIcon> entry = (Map.Entry<String, ImageIcon>) characterIter.next();
			lb.setText("正在設定" + entry.getKey());
			// 寫出一般角色人物圖
			try (ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream("image/character/" + entry.getKey() + ".leo"))) {
				oos.writeObject(Tool.fix(entry.getValue(), set.getChrWid(), set.getChrHei()));
			}
			// 寫出翻開角色動畫
			ArrayList<ImageIcon> openChr = new ArrayList<>();
			ImageIcon ig = characters.get("隱藏");
			for (int t = 0; t <= Setting.anmOpenChrTime / 2; t += Setting.anmSpc) {
				double deg = (double) t / Setting.anmOpenChrTime * Math.PI;
				int w = (int) Math.abs(set.getChrLng() * Math.cos(deg));
				if (w <= 0)
					w = 1;
				openChr.add(Tool.fix(ig, w, set.getChrHei()));
			}
			for (int t = 0; t <= Setting.anmOpenChrTime / 2; t += Setting.anmSpc) {
				double deg = Math.PI / 2 + (double) t / Setting.anmOpenChrTime * Math.PI;
				int w = (int) Math.abs(set.getChrWid() * Math.cos(deg));
				if (w <= 0)
					w = 1;
				openChr.add(Tool.fix(entry.getValue(), w, set.getChrHei()));
			}
			try (ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream("image/character/openChr" + entry.getKey() + ".leo"))) {
				oos.writeObject(openChr);
			}
			// 寫出死亡人物圖片
			try (ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream("image/character/dead" + entry.getKey() + ".leo"))) {
				oos.writeObject(Tool.fix(Tool.toGray(entry.getValue()), set.getChrWid(), set.getChrHei()));
			}
			// 寫出單人勝利人物圖片
			try (ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream("image/character/chrWin" + entry.getKey() + ".leo"))) {
				oos.writeObject(Tool.fix(entry.getValue(), set.getWinChrLng(), set.getWinChrLng()));
			}
		}
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("res/chrsWin.leo"))) {
			characters = (HashMap<String, ImageIcon>) ois.readObject();
		}
		characterIter = characters.entrySet().iterator();
		while (characterIter.hasNext()) {
			Map.Entry<String, ImageIcon> entry = (Map.Entry<String, ImageIcon>) characterIter.next();
			lb.setText("正在設定" + entry.getKey());
			// 寫出一般角色人物圖
			try (ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream("image/character/chrsWin" + entry.getKey() + ".leo"))) {
				oos.writeObject(Tool.fix(entry.getValue(), set.getChrWid() / 2, set.getChrHei()));
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void iniCards() throws FileNotFoundException, IOException, ClassNotFoundException {
		lb.setText("正在設定遊戲牌");
		final File gamecardDir = new File("image/gamecard");
		if (!gamecardDir.exists())
			gamecardDir.mkdirs();
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("image/gamecard/white.leo"))) {
			oos.writeObject(Tool.white(new Dimension(set.getFccWid(), set.getFccHei()), 153));
		}
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("image/gamecard/black.leo"))) {
			oos.writeObject(Tool.black(new Dimension(set.getFccWid(), set.getFccHei()), 153));
		}
		ArrayList<ImageIcon> gamecard;
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("res/gamecards.leo"))) {
			gamecard = (ArrayList<ImageIcon>) ois.readObject();
		}
		Iterator<ImageIcon> gamecardIter = gamecard.iterator();
		int gamecardId = 0;
		while (gamecardIter.hasNext()) {
			// 一般卡牌圖
			try (ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream("image/gamecard/" + gamecardId + ".leo"))) {
				oos.writeObject(Tool.fix(gamecardIter.next(), set.getFccWid(), set.getFccHei()));
			}
			gamecardId++;
		}
		gamecardIter = gamecard.iterator();
		gamecardId = 0;

		// 處理動畫前置
		ArrayList<ImageIcon> getItl97 = new ArrayList<>(), getItl98 = new ArrayList<>(), openMt = new ArrayList<>(),
				lotterycard = new ArrayList<>(); // 翻開情報前半
		ArrayList<Dimension> getItlD = new ArrayList<>(); // 情報浮起時的寬高
		ArrayList<Integer> lotteryW = new ArrayList<>(); // 真偽莫辨動畫寬度,

		int js = Setting.anmUpTime / Setting.anmSpc;
		float dh = (float) (set.getFccHei() - set.getItlHei()) / js;
		float dw = (float) (set.getFccWid() - set.getItlWid()) / js;
		double dd = Math.PI / js;
		for (int j = 0; j <= js; j++) {
			int h = Math.round(set.getItlHei() + dh * j); // 此時應有的h
			float w0 = set.getItlWid() + dw * j; // 不考慮翻轉效果時的寬度
			int w = Math.round(w0 * (float) Math.cos(dd * j)); // 考慮翻轉效果
			int lotterycardW = Math.round(set.getFccWid() * (float) Math.cos(dd * j));
			if (w > 0) {
				getItl97.add(Tool.fix(gamecard.get(42), w, h)); // 字樣
				getItl98.add(Tool.fix(gamecard.get(43), w, h));
				openMt.add(Tool.fix(gamecard.get(45), w, h));
				getItlD.add(new Dimension(w, h));
			} else if (w == 0)
				getItlD.add(new Dimension(-1, h));
			else
				getItlD.add(new Dimension(-w, h)); // 密電 直達字樣已經不見

			if (lotterycardW > 0) {
				lotteryW.add(lotterycardW);
				lotterycard.add(Tool.fix(gamecard.get(45), lotterycardW, h)); // 牌庫字樣
			} else if (lotterycardW == 0)
				lotteryW.add(1);
			else
				lotteryW.add(-lotterycardW);
		}

		while (gamecardIter.hasNext()) {
			// 各種大小的卡牌
			ImageIcon card = gamecardIter.next();
			HashMap<Integer, ImageIcon> card_size = new HashMap<>();
			for (int h = set.getImHei(); h <= set.getFccHei(); h++)
				card_size.put(h, Tool.fix(card, Math.round((float) h * set.getFccWid() / set.getFccHei()), h));
			try (ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream("image/gamecard/card_size" + gamecardId + ".leo"))) {
				oos.writeObject(card_size);
			}
			card_size = null;

			// 由密電而翻開的動畫
			ArrayList<ImageIcon> getItlBy97 = new ArrayList<>();
			getItlBy97.addAll(getItl97);
			int t = getItlD.size();
			for (int i = getItlBy97.size(); i < t; i++) {
				Dimension d = getItlD.get(i);
				getItlBy97.add(Tool.fix(card, d.width, d.height));
			}
			try (ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream("image/gamecard/getItlBy97" + gamecardId + ".leo"))) {
				oos.writeObject(getItlBy97);
			}
			getItlBy97 = null;

			// 由直達而翻開的動畫
			ArrayList<ImageIcon> getItlBy98 = new ArrayList<>();
			getItlBy98.addAll(getItl98);
			t = getItlD.size();
			for (int i = getItlBy98.size(); i < t; i++) {
				Dimension d = getItlD.get(i);
				getItlBy98.add(Tool.fix(card, d.width, d.height));
			}
			try (ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream("image/gamecard/getItlBy98" + gamecardId + ".leo"))) {
				oos.writeObject(getItlBy98);
			}
			getItlBy98 = null;

			// 由牌庫頂而翻開的動畫
			ArrayList<ImageIcon> openCardFromMt = new ArrayList<>();
			openCardFromMt.addAll(openMt);
			t = getItlD.size();
			for (int i = openCardFromMt.size(); i < t; i++) {
				Dimension d = getItlD.get(i);
				openCardFromMt.add(Tool.fix(card, d.width, d.height));
			}
			try (ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream("image/gamecard/openCardFromMt" + gamecardId + ".leo"))) {
				oos.writeObject(openCardFromMt);
			}
			openCardFromMt = null;

			// 由真偽翻開的動畫
			ArrayList<ImageIcon> cardByLottery = new ArrayList<>();
			cardByLottery.addAll(lotterycard);
			t = getItlD.size();
			for (int i = cardByLottery.size(); i < t; i++) {
				cardByLottery.add(Tool.fix(card, lotteryW.get(i), set.getFccHei()));
			}
			try (ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream("image/gamecard/cardByLottery" + gamecardId + ".leo"))) {
				oos.writeObject(cardByLottery);
			}
			gamecardId++;
		}
	}

	@SuppressWarnings("unchecked")
	public void iniLTDs() throws FileNotFoundException, IOException, ClassNotFoundException {
		lb.setText("正在設定人物狀態圖示");
		final File ltdDir = new File("image/ltd");
		if (!ltdDir.exists())
			ltdDir.mkdirs();
		ArrayList<ImageIcon> ls, ts, ds, list = new ArrayList<>();
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("res/l.leo"))) {
			ls = (ArrayList<ImageIcon>) ois.readObject();
		}
		for (int i = 0; i <= Setting.anmltdTime; i += Setting.anmSpc)
			list.add(Tool.fix(ls.get(Math.round((float) i * 100 / Setting.anmltdTime)), set.getChrLng(), set.getChrLng()));
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("image/ltd/l.leo"))) {
			oos.writeObject(list);
		}
		ls = null;
		list.clear();
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("res/t.leo"))) {
			ts = (ArrayList<ImageIcon>) ois.readObject();
		}
		for (int i = 0; i <= Setting.anmltdTime; i += Setting.anmSpc)
			list.add(Tool.fix(ts.get(Math.round((float) i * 100 / Setting.anmltdTime)), set.getChrLng(), set.getChrLng()));
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("image/ltd/t.leo"))) {
			oos.writeObject(list);
		}
		ts = null;
		list.clear();
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("res/d.leo"))) {
			ds = (ArrayList<ImageIcon>) ois.readObject();
		}
		for (int i = 0; i <= Setting.anmltdTime; i += Setting.anmSpc)
			list.add(Tool.fix(ds.get(Math.round((float) i * 100 / Setting.anmltdTime)), set.getChrLng(), set.getChrLng()));
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("image/ltd/d.leo"))) {
			oos.writeObject(list);
		}
		ds = null;
		list = null;
	}

	@SuppressWarnings("unchecked")
	public void iniSkills() throws FileNotFoundException, IOException, ClassNotFoundException {
		lb.setText("正在設定技能");
		final File skillDir = new File("image/skill");
		if (!skillDir.exists())
			skillDir.mkdirs();
		Map<String, ImageIcon> skills;
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("res/skills.leo"))) {
			skills = (HashMap<String, ImageIcon>) ois.readObject();
		}
		Iterator<Entry<String, ImageIcon>> skillIter = skills.entrySet().iterator();
		while (skillIter.hasNext()) {
			Map.Entry<String, ImageIcon> entry = (Map.Entry<String, ImageIcon>) skillIter.next();
			float whrate = (float) set.getSkillTxtF().width / set.getSkillTxtF().height;
			if (Skill.isSkill(entry.getKey())) {
				HashMap<Integer, ImageIcon> sk = new HashMap<>();
				for (int h = set.getSkillTxtFh(); h <= set.getSkillTxtSh(); h++) {
					sk.put(h,
							Tool.fix(
									Tool.setAlpha(entry.getValue(),
											255 - Math.round((float) 255 * (h - set.getSkillTxtFh())
													/ (set.getSkillTxtSh() - set.getSkillTxtFh()))),
									Math.round(h * whrate), h));
				}
				try (ObjectOutputStream oos = new ObjectOutputStream(
						new FileOutputStream("image/skill/" + entry.getKey() + ".leo"))) {
					oos.writeObject(sk);
				}
			} else {
				try (ObjectOutputStream oos = new ObjectOutputStream(
						new FileOutputStream("image/skill/" + entry.getKey() + ".leo"))) {
					oos.writeObject(Tool.fix(entry.getValue(), set.getFccWid(), set.getFccHei()));
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void iniOthers() throws ClassNotFoundException, IOException {
		lb.setText("正在布置其他設定");
		// 死亡標誌
		final File deadIconDir = new File("image/deadicon");
		if (!deadIconDir.exists())
			deadIconDir.mkdirs();
		ArrayList<ImageIcon> deadicons;
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("res/deadicons.leo"))) {
			deadicons = (ArrayList<ImageIcon>) ois.readObject();
		}
		Iterator<ImageIcon> deadiconIter = deadicons.iterator();
		int deadIconId = 0;
		while (deadiconIter.hasNext()) {
			try (ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream("image/deadicon/" + deadIconId + ".leo"))) {
				oos.writeObject(Tool.fix(deadiconIter.next(), set.getChrLng(), set.getChrLng()));
				deadIconId++;
			}
		}
		final File roomDir = new File("image/room");
		if (!roomDir.exists())
			roomDir.mkdirs();
		String[] roomImage = { "ready", "chief", "closed" };
		for (String imagename : roomImage)
			try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("res/" + imagename + ".leo"))) {
				try (ObjectOutputStream oos = new ObjectOutputStream(
						new FileOutputStream("image/room/" + imagename + ".leo"))) {
					oos.writeObject(Tool.fix((ImageIcon) ois.readObject(), set.getChrLng(), set.getChrLng()));
				}
			}
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("res/closeBtnOnExited.leo"))) {
			try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("image/room/closeBtnOnExited.leo"))) {
				oos.writeObject(Tool.fix((ImageIcon) ois.readObject(), set.getItlFldTtHei(), set.getItlFldTtHei()));
			}
		}
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("res/closeBtnOnEntered.leo"))) {
			try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("image/room/closeBtnOnEntered.leo"))) {
				oos.writeObject(Tool.fix((ImageIcon) ois.readObject(), set.getItlFldTtHei(), set.getItlFldTtHei()));
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void iniTeams() throws FileNotFoundException, IOException, ClassNotFoundException {
		lb.setText("正在設定身分");
		final File teamDir = new File("image/team");
		if (!teamDir.exists())
			teamDir.mkdirs();
		ArrayList<ImageIcon> teams;
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("res/teams.leo"))) {
			teams = (ArrayList<ImageIcon>) ois.readObject();
		}
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("image/team/0.leo"))) {
			oos.writeObject(teams.get(0));
		}
		for (int i = 1; i < 4; i++) {
			// 不透明身分圖片
			try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("image/team/" + i + ".leo"))) {
				oos.writeObject(Tool.fix(teams.get(i), set.getTeamWid(), set.getTeamHei()));
			}
			// 透明身分圖片
			try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("image/team/" + (i + 3) + ".leo"))) {
				oos.writeObject(Tool.fix(Tool.setAlpha(teams.get(i), 255 / 3), set.getTeamWid(), set.getTeamHei()));
			}
			// 身分動畫
			ArrayList<ImageIcon> anmTeamNoAlpha = new ArrayList<>();
			ArrayList<ImageIcon> anmTeamAlpha = new ArrayList<>();
			for (int t = 0; t <= Setting.showTeamCard; t += Setting.anmSpc) {
				double deg = (double) t / Setting.showTeamCard * Math.PI;
				int w = (int) Math.abs(set.getTeamWid() * Math.cos(deg));
				if (w <= 0)
					w = 1;
				int noAlpha, alpha;
				if (deg >= 0 && deg <= Math.PI * 5 / 6)
					noAlpha = (int) (255 * deg / (Math.PI * 5 / 6));
				else
					noAlpha = 255;
				alpha = (int) (255 * deg / (Math.PI * 3));
				if (deg <= Math.PI / 2) {
					anmTeamNoAlpha.add(Tool.fix(Tool.setAlpha(teams.get(0), noAlpha), w, set.getTeamHei()));
					anmTeamAlpha.add(Tool.fix(Tool.setAlpha(teams.get(0), alpha), w, set.getTeamHei()));
				} else {
					anmTeamNoAlpha.add(Tool.fix(Tool.setAlpha(teams.get(i), noAlpha), w, set.getTeamHei()));
					anmTeamAlpha.add(Tool.fix(Tool.setAlpha(teams.get(i), alpha), w, set.getTeamHei()));
				}
			}
			try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("image/team/anm" + i + ".leo"))) {
				oos.writeObject(anmTeamNoAlpha);
			}
			try (ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream("image/team/anm" + (i + 3) + ".leo"))) {
				oos.writeObject(anmTeamAlpha);
			}
		}
		ArrayList<ImageIcon> win;
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("res/win.leo"))) {
			win = (ArrayList<ImageIcon>) ois.readObject();
		}
		int i = 1;
		for (ImageIcon img : win) {
			try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("image/team/win" + i + ".leo"))) {
				oos.writeObject(Tool.fix(img, set.getWinTitleWid(), set.getWinTitleHei()));
			}
			i++;
		}
	}

	@SuppressWarnings("unchecked")
	public void iniBtns() throws FileNotFoundException, IOException, ClassNotFoundException {
		lb.setText("正在設定按鈕");
		final File btnDir = new File("image/btn");
		if (!btnDir.exists())
			btnDir.mkdirs();
		HashMap<String, ImageIcon> btns;
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("res/btns.leo"))) {
			btns = (HashMap<String, ImageIcon>) ois.readObject();
		}
		Iterator<Entry<String, ImageIcon>> btnIter = btns.entrySet().iterator();
		while (btnIter.hasNext()) {
			Map.Entry<String, ImageIcon> entry = (Map.Entry<String, ImageIcon>) btnIter.next();
			try (ObjectOutputStream oos = new ObjectOutputStream(
					new FileOutputStream("image/btn/" + entry.getKey() + ".leo"))) {
				oos.writeObject(Tool.fix(entry.getValue(), set.getBtnWid(), set.getBtnHei()));
			}
		}
	}

	public void initialize() throws FileNotFoundException, IOException, ClassNotFoundException {
		long t1 = Calendar.getInstance().getTimeInMillis();
		System.out.println("正在設定角色");
		iniChrs();
		System.out.println("正在設定遊戲牌");
		iniCards();
		System.out.println("正在設定技能");
		iniSkills();
		System.out.println("正在設定身分");
		iniTeams();
		System.out.println("正在設定人物狀態");
		iniLTDs();
		System.out.println("正在設定按鈕");
		iniBtns();
		System.out.println("正在設定其他");
		iniOthers();
		System.out.println("完成");
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("setting.dll"))) {
			oos.writeObject(set);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		long t2 = Calendar.getInstance().getTimeInMillis();
		System.out.println("耗時" + (t2 - t1) + "毫秒");
		lb.setText("初始化完成、重新開啟遊戲");
	}

	public static void main(String[] args) {
		try {
			new Initializer(new Setting(1440, 810), new JLabel());
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}
