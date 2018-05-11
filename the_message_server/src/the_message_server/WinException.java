package the_message_server;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class WinException extends Exception {

	ArrayList<Win> wins = new ArrayList<>();

	public WinException() {
	}

	public void set(Win win) {
		System.out.println("©I¥ssetwin¡Asize = " + wins.size());
		for (Win w : wins){
			System.out.println(w.chr.equals(win.chr));
			if (w.chr.equals(win.chr)){
				if (win.sayWin) {
					w.sayWin = true;
				}
				return;
			}
		}
		wins.add(win);
	}

	@Override
	public String getMessage() {
		int r = 0, b = 0, k = 0;
		String chrs = "";
		for (Win w : wins) {
			if (w.sayWin)
				switch (w.idy) {
				case 1:
					r = 1;
					break;
				case 2:
					b = 2;
					break;
				case 3:
					k = 3;
					break;
				}
			chrs += w.chr + "," + w.idy + ":";
		}
		int idy = r + b + k;
		return idy + ":" + chrs;
	}
	
	public static void main(String[] args){
		WinException e = new WinException();
		
		System.out.println(e.getMessage());
	}
	
}