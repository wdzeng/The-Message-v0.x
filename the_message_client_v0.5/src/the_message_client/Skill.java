package the_message_client;

import javax.swing.text.StyledDocument;

public class Skill {

	public static final int count = 149;

	public static boolean isRed(int id) {
		if (id < 100 || id > count)
			new LogicException("錯誤的技能ID: " + id).printStackTrace();
		switch (id) {
		case 100:
		case 101:
		case 102:
		case 103:
		case 105:
		case 106:
		case 108:
		case 110:
		case 113:
		case 114:
		case 116:
		case 118:
		case 119:
		case 120:
		case 121:
		case 123:
		case 124:
		case 125:
		case 126:
		case 127:
		case 128:
		case 129:
		case 130:
		case 131:
		case 132:
		case 133:
		case 134:
		case 135:
		case 136:
		case 137:
		case 138:
		case 139:
		case 140:
		case 141:
		case 142:
		case 143:
		case 145:
		case 146:
		case 147:
		case 148:
		case 149:
		case 151:
		case 152:
		case 153:
		case 154:
		case 155:
		case 157:
		case 158:
		case 159:
			return true;
		}
		return false;
	}

	public static String getSkillName(int id) {
		switch (id) {
		case 100:
		case 103:
			return "就計";
		case 101:
			return "城府";
		case 102:
			return "遺志";
		case 104:
			return "合謀";
		case 105:
			return "棄卒保帥";
		case 106:
			return "釜底抽薪";
		case 107:
			return "速譯";
		case 108:
			return "洞悉";
		case 109:
			return "潛藏伏擊";
		case 110:
			return "黃雀在后";
		case 111:
			return "血染玫瑰";
		case 112:
			return "警覺";
		case 113:
			return "掩護";
		case 114:
			return "狙擊";
		case 115:
			return "先機";
		case 116:
			return "後著";
		case 117:
			return "截上截";
		case 118:
			return "計中計";
		case 119:
			return "拷問";
		case 120:
			return "搜取";
		case 121:
			return "精明";
		case 122:
			return "愛情";
		case 123:
			return "攻守兼備";
		case 124:
			return "臨危受命";
		case 125:
			return "敏銳";
		case 126:
			return "聯動";
		case 127:
			return "風度";
		case 128:
			return "英雄";
		case 129:
			return "救美";
		case 130:
			return "亮劍";
		case 131:
			return "嗜血";
		case 132:
			return "英雄本色";
		case 133:
			return "笑裡藏刀";
		case 134:
			return "運籌帷幄";
		case 135:
			return "預謀";
		case 136:
			return "連擊";
		case 137:
			return "聲東擊西";
		case 138:
			return "彈藥無限";
		case 139:
			return "揭露";
		case 140:
			return "真相";
		case 141:
			return "隔牆有耳";
		case 142:
			return "偷龍轉鳳";
		case 143:
			return "喬裝";
		case 144:
			return "收買";
		case 145:
			return "敗露";
		case 146:
			return "換日";
		case 147:
			return "偷天";
		case 148:
			return "陰謀";
		case 149:
			return "陽謀";
		case 150:
			return "光影浮華";
		case 151:
			return "歲月留痕";
		case 152:
			return "定點抹除";
		case 153:
			return "槍下留情";
		case 154:
			return "化劍為犁";
		case 155:
			return "斗轉星移";
		case 156:
			return "移花接木";
		case 157:
			return "金蟬脫鞘";
		case 158:
			return "心智迷茫";
		case 159:
			return "香氣傳遞";
		}
		new LogicException("錯誤的ID: " + id).printStackTrace();
		return null;
	}

	public static String getChr(int id) {
		switch (id) {
		case 100:
		case 101:
		case 102:
			return "老鬼";
		case 103:
		case 104:
			return "老槍";
		case 105:
		case 106:
			return "老金";
		case 107:
		case 108:
			return "譯電員";
		case 109:
		case 110:
			return "黃雀";
		case 111:
			return "黑玫瑰";
		case 112:
		case 113:
			return "鋼鐵特工";
		case 114:
			return "閃靈";
		case 115:
		case 116:
			return "浮萍";
		case 117:
		case 118:
			return "峨嵋風";
		case 119:
		case 120:
			return "六姐";
		case 121:
		case 122:
			return "大美女";
		case 123:
		case 124:
			return "致命香水";
		case 125:
		case 126:
		case 127:
			return "柒佰";
		case 128:
		case 129:
			return "禮服蒙面人";
		case 130:
		case 131:
			return "刀鋒";
		case 132:
			return "小馬哥";
		case 133:
		case 134:
			return "戴笠";
		case 135:
		case 136:
			return "職業殺手";
		case 137:
		case 138:
			return "蝮蛇";
		case 139:
		case 140:
			return "福爾摩斯";
		case 141:
		case 142:
			return "情報處長";
		case 143:
		case 144:
		case 145:
			return "小白";
		case 146:
		case 147:
			return "貝雷帽";
		case 148:
		case 149:
			return "怪盜九九";
		case 150:
		case 151:
			return "血牡丹";
		case 152:
		case 153:
			return "出雲龍";
		case 154:
		case 155:
			return "情報獵手";
		case 156:
		case 157:
			return "議長";
		case 158:
		case 159:
			return "千金小姐";
		}
		new LogicException("錯誤的ID: " + id).printStackTrace();
		return null;
	}

	public static boolean getRed(String txt) {
		for (int i = 100; i <= count; i++)
			if (isRed(i))
				return true;
		return false;
	}

	public static StyledDocument getDoc(int id, Setting sett) {
		Doc doc = new Doc(sett);
		switch (id) {
		case 100:
		case 103:
			doc.add(1, "就計　");
			doc.add(0, "當你被試探或鎖定時，可以翻開此角色，然後抽二張牌。");
			break;
		case 101:
			doc.add(1, "城府　");
			doc.add(0, "你被試探時可以隱瞞身分。");
			break;
		case 102:
			doc.add(1, "遺志　");
			doc.add(0, "當你死亡時，可以把全部手牌面朝下遞給一位玩家，他隨機選擇其中的三張作為情報。");
			break;
		case 104:
			doc.add(2, "合謀　");
			doc.add(0, "翻開你的身分牌，然後蓋伏另一位玩家的角色牌。");
			break;
		case 105:
			doc.add(1, "棄卒保帥　");
			doc.add(0, "當你的卡牌被識破時，可以翻開此角色牌，抽五張牌，然後把二張手牌按任意順序放回排庫頂。");
			break;
		case 106:
			doc.add(1, "釜底抽薪　");
			doc.add(0, "當你獲得假情報時，可以再放置一張假情報到自己面前，然後蓋伏此角色牌，並在任意位玩家面前各放置一張情報。");
			break;
		case 107:
			doc.add(2, "速譯　");
			doc.add(0, "情報傳遞時，可以翻開此角色牌，然後檢視一張傳遞中的情報，並抽一張牌。");
			break;
		case 108:
			doc.add(1, "洞悉　");
			doc.add(0, "當情報被破譯時，你可以蓋伏此角色牌。");
			break;
		case 109:
			doc.add(2, "潛藏伏擊　");
			doc.add(0, "翻開此角色牌，然後把另一位玩家面前的一張情報放回牌庫頂。");
			break;
		case 110:
			doc.add(1, "黃雀在后　");
			doc.add(0, "當另一位玩家死亡時，可以翻開你的身分牌，然後在另一位玩家面前放置最多兩張假情報。");
			break;
		case 111:
			doc.add(2, "血染玫瑰");
			doc.add(0, "翻開此角色牌，指定另一位玩家，然後展示牌庫頂的第一張牌，若那張牌是黑色，你可以在他面前放置最多三張假情報，否則你只能放置一張。");
			break;
		case 112:
			doc.add(2, "警覺　");
			doc.add(0, "翻開此角色牌，然後視為使用了識破。");
			break;
		case 113:
			doc.add(1, "掩護　");
			doc.add(0, "你可以獲得另一位玩家面前的一張假情報，然後蓋伏此角色牌。");
			break;
		case 114:
			doc.add(1, "狙擊　");
			doc.add(0, "翻開此角色牌，然後燒毀另一位玩家面前最多三張情報。");
			break;
		case 115:
			doc.add(2, "先機　");
			doc.add(0, "翻開此角色牌，抽取一位玩家的三張手牌，然後他抽一張牌。");
			break;
		case 116:
			doc.add(1, "後著　");
			doc.add(0, "當你死亡時，可以檢視另一位玩家的手牌，然後把其中一張放置在一位玩家面前。");
			break;
		case 117:
			doc.add(1, "截上截　");
			doc.add(0, "他人回合情報傳遞時，可以翻開此角色牌，然後將一張傳遞置中的情報放置在你面前。");
			break;
		case 118:
			doc.add(1, "計中計　");
			doc.add(0, "當一位玩家獲得你傳出的真情報時，你可以燒毀他面前一張假情報。");
			break;
		case 119:
			doc.add(1, "拷問　");
			doc.add(0, "當你試探一位玩家時，你可以抽取他的一張手牌。");
			break;
		case 120:
			doc.add(1, "搜取　");
			doc.add(0, "當一位玩家死亡時，你隨機檢視他的一張手牌，然後可以將該牌加入手牌或做為情報。");
			break;
		case 121:
			doc.add(1, "精明　");
			doc.add(0, "你的抽牌階段改為抽三張牌，然後選擇一張手牌放回牌庫頂。");
			break;
		case 122:
			doc.add(2, "愛情　");
			doc.add(0, "翻開你的身分牌，然後燒毀二張假情報。");
			break;
		case 123:
			doc.add(1, "攻守兼備　");
			doc.add(0, "當你使用鎖定或調虎離山時，抽二張牌，然後選擇一張手牌放回牌庫頂。");
			break;
		case 124:
			doc.add(1, "臨危受命　");
			doc.add(0, "當另一位玩家死亡時，在其翻開身分牌前，你可以把你的身分牌面朝下移出遊戲，然後得到他的身分牌，並且將一位玩家面前的一張情報與牌庫頂第一張牌調換。");
			break;
		case 125:
			doc.add(1, "敏銳　");
			doc.add(0, "你可以在他人回合中使用鎖定。");
			break;
		case 126:
			doc.add(1, "聯動　");
			doc.add(0, "當你使用鎖定時，抽二張牌，然後選擇一張手牌分給另一位玩家。");
			break;
		case 127:
			doc.add(1, "風度　");
			doc.add(0, "當你宣告勝利時，選擇一位女性一同勝利。");
			break;
		case 128:
			doc.add(1, "英雄　");
			doc.add(0, "當你獲得假情報時，你可以抽二張牌。");
			break;
		case 129:
			doc.add(1, "救美　");
			doc.add(0, "2; 當一位女性死亡時，可以翻開你的身分牌，然後燒毀自己與她面前所有的假情報來拯救她。");
			break;
		case 130:
			doc.add(1, "亮劍　");
			doc.add(0, "你的試探和退回可以當鎖定使用。");
			break;
		case 131:
			doc.add(1, "嗜血　");
			doc.add(0, "當另一位玩家死亡時，你可以抽四張牌，然後選擇一張手牌分給另一位玩家。");
			break;
		case 132:
			doc.add(1, "英雄本色　");
			doc.add(0, "當你獲得假情報時，抽一張牌，然後可以在一位玩家面前放置一張假情報。");
			break;
		case 133:
			doc.add(1, "笑裡藏刀　");
			doc.add(0, "當另一位玩家獲得你傳出的真情報時，你可以在他面前放置一張假情報。");
			break;
		case 134:
			doc.add(1, "運籌帷幄　");
			doc.add(0, "當你獲得真情報時，你可以抽一張牌。");
			break;
		case 135:
			doc.add(1, "預謀　");
			doc.add(0, "你的調虎離山可以當作鎖定使用。");
			break;
		case 136:
			doc.add(1, "連擊　");
			doc.add(0, "當一位玩家獲得你傳出的假情報時，你抽一張牌，然後可以在他面前再放置一張假情報。");
			break;
		case 137:
			doc.add(1, "聲東擊西　");
			doc.add(0, "蓋伏此角色牌，然後視為使用了調虎離山。");
			break;
		case 138:
			doc.add(1, "彈藥無限　");
			doc.add(0, "若你沒有手牌，可以翻開此角色牌，然後抽一張牌。");
			break;
		case 139:
			doc.add(1, "揭露　");
			doc.add(0, "情報傳遞時，可以丟棄一張黑色手牌，然後翻開一張傳遞中的密電，若為假情報，則抽二張牌。");
			break;
		case 140:
			doc.add(1, "真相　");
			doc.add(0, "當一位玩家使用試探時，你可以棄一張手牌先檢視該試探。");
			break;
		case 141:
			doc.add(1, "隔牆有耳　");
			doc.add(0, "當你試探一位玩家時，你可以抽二張牌，然後選擇一張手牌放回牌庫頂。");
			break;
		case 142:
			doc.add(1, "偷龍轉鳳　");
			doc.add(0, "當情報到達你時，你可以丟棄一張手牌，然後將該情報與牌庫頂的第一張牌調換。");
			break;
		case 143:
			doc.add(1, "喬裝　");
			doc.add(0, "此角色既視為男性也視為女性。");
			break;
		case 144:
			doc.add(2, "收買　");
			doc.add(0, "把你的四張手牌交給一位玩家，然後獲得他面前的一張情報。");
			break;
		case 145:
			doc.add(1, "敗露　");
			doc.add(0, "當你獲得六張或以上的情報時，你輸掉這場遊戲。");
			break;
		case 146:
			doc.add(1, "換日　");
			doc.add(0, "你的黑色手牌可以當作鎖定使用。");
			break;
		case 147:
			doc.add(1, "偷天　");
			doc.add(0, "當一位玩家獲得你傳出的假情報時，你可以把他面前的一張真情報加入到任意一位玩家的手牌中。");
			break;
		case 148:
			doc.add(1, "陰謀　");
			doc.add(0, "當另一位玩家獲得你傳出的假情報時，你可以抽取任意一位玩家的一張手牌。");
			break;
		case 149:
			doc.add(1, "陽謀　");
			doc.add(0, "當你獲得傳出的假情報時，你可以抽取任意一位玩家的一張手牌。");
			break;
		}
		return doc.get();
	}

	public StyledDocument getDoc(String chr, Setting sett) {
		Doc doc = new Doc(sett);

		return doc.get();
	}

	public static boolean isSkill(String skillName) {
		switch (skillName) {
		case "就計":
		case "城府":
		case "遺志":
		case "合謀":
		case "棄卒保帥":
		case "釜底抽薪":
		case "速譯":
		case "洞悉":
		case "潛藏伏擊":
		case "黃雀在后":
		case "血染玫瑰":
		case "警覺":
		case "掩護":
		case "狙擊":
		case "先機":
		case "後著":
		case "截上截":
		case "計中計":
		case "拷問":
		case "搜取":
		case "精明":
		case "愛情":
		case "攻守兼備":
		case "臨危受命":
		case "敏銳":
		case "聯動":
		case "風度":
		case "英雄":
		case "救美":
		case "亮劍":
		case "嗜血":
		case "英雄本色":
		case "笑裡藏刀":
		case "運籌帷幄":
		case "預謀":
		case "連擊":
		case "聲東擊西":
		case "彈藥無限":
		case "揭露":
		case "真相":
		case "隔牆有耳":
		case "偷龍轉鳳":
		case "喬裝":
		case "收買":
		case "敗露":
		case "換日":
		case "偷天":
		case "陰謀":
		case "陽謀":
		case "光影浮華":
		case "歲月留痕":
		case "定點抹除":
		case "槍下留情":
		case "化劍為犁":
		case "斗轉星移":
		case "移花接木":
		case "金蟬脫鞘":
		case "心智迷茫":
		case "香氣傳遞":
			return true;
		}
		return false;
	}

	public static void main(String[] args) {
		for (int i = 100; i <= 159; i++) {
			System.out.println("case \"" + getSkillName(i) + "\":");
		}
	}

}
