package the_message_server;

public class CharacterData {

	public static boolean isCover(String chr) {
		switch (chr) {
		case "老鬼":
		case "老槍":
		case "老金":
		case "黑玫瑰":
		case "譯電員":
		case "峨嵋風":
		case "鋼鐵特工":
		case "浮萍":
		case "閃靈":
		case "黃雀":
			return true;
		case "六姐":
		case "大美女":
		case "致命香水":
		case "柒佰":
		case "禮服蒙面人":
		case "刀鋒":
		case "小馬哥":
		case "戴笠":
		case "職業殺手":
		case "蝮蛇":
		case "福爾摩斯":
		case "情報處長":
		case "小白":
		case "貝雷帽":
		case "怪盜九九":
			return false;
		default:
			new CharacterException("不存在的角色:" + chr).printStackTrace();
			return false;
		}
	}

	public static boolean isMale(Server.Player pl) {
		if (pl.isChrCov) // 蓋伏用女聲
			return false;
		switch (pl.chr) {
		case "老鬼":
		case "黑玫瑰":
		case "譯電員":
		case "浮萍":
		case "閃靈":
		case "黃雀":
		case "六姐":
		case "大美女":
		case "致命香水":
			return false;
		case "小白":
		case "老槍":
		case "老金":
		case "峨嵋風":
		case "鋼鐵特工":
		case "柒佰":
		case "禮服蒙面人":
		case "刀鋒":
		case "小馬哥":
		case "戴笠":
		case "職業殺手":
		case "蝮蛇":
		case "福爾摩斯":
		case "情報處長":
		case "貝雷帽":
		case "怪盜九九":
			return true;
		default:
			new CharacterException("不存在的角色:" + pl.chr).printStackTrace();
			return false;
		}
	}
	
	public static boolean isFeMale(Server.Player pl) {
		if (pl.isChrCov) // 蓋伏用女聲
			return false;
		switch (pl.chr) {
		case "老鬼":
		case "黑玫瑰":
		case "譯電員":
		case "浮萍":
		case "閃靈":
		case "黃雀":
		case "六姐":
		case "大美女":
		case "致命香水":
		case "小白":
			return true;
		case "老槍":
		case "老金":
		case "峨嵋風":
		case "鋼鐵特工":
		case "柒佰":
		case "禮服蒙面人":
		case "刀鋒":
		case "小馬哥":
		case "戴笠":
		case "職業殺手":
		case "蝮蛇":
		case "福爾摩斯":
		case "情報處長":
		case "貝雷帽":
		case "怪盜九九":
			return false;
		default:
			new CharacterException("不存在的角色:" + pl.chr).printStackTrace();
			return false;
		}
	}

	public static boolean isMaleBySound(Server.Player pl) {
		if (pl.isChrCov) // 蓋伏用女聲
			return false;
		switch (pl.chr) {
		case "老鬼":
		case "黑玫瑰":
		case "譯電員":
		case "浮萍":
		case "閃靈":
		case "黃雀":
		case "六姐":
		case "大美女":
		case "致命香水":
		case "小白": // 小白改為女角
			return false;
		case "老槍":
		case "老金":
		case "峨嵋風":
		case "鋼鐵特工":
		case "柒佰":
		case "禮服蒙面人":
		case "刀鋒":
		case "小馬哥":
		case "戴笠":
		case "職業殺手":
		case "蝮蛇":
		case "福爾摩斯":
		case "情報處長":
		case "貝雷帽":
		case "怪盜九九":
			return true;
		default:
			new CharacterException("不存在的角色:" + pl.chr).printStackTrace();
			return false;
		}
	}
}
