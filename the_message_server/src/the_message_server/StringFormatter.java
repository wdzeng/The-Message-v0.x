package the_message_server;

public class StringFormatter {

	public static char numCh(int num) {
		switch (num) {
		case 1:
			return '一';
		case 2:
			return '二';
		case 3:
			return '三';
		case 4:
			return '四';
		case 5:
			return '五';
		case 6:
			return '六';
		case 7:
			return '七';
		case 8:
			return '八';
		case 9:
			return '九';
		case 10:
			return '十';
		}
		if (num < 1 || num > 10)
			new LogicException("太大或太小的數字: " + num).printStackTrace();
		return ' ';
	}
}
