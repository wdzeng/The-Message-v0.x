package the_message_client;

public class CardLocation {
	// 此類別作為移動牌的資訊

	int who, count;
	String where;

	public CardLocation(int who, String where, int count) {
		this.who = who;
		this.where = where; // r,b,k,h,i
		this.count = count; // 更新後數量 -1表示不變
	}

}
