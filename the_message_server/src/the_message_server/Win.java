package the_message_server;

public class Win {

	public String chr;
	public int idy;
	public boolean sayWin;
	public Server.Player pl;

	public Win(Server.Player p, boolean s) {
		pl = p;
		chr = p.chr;
		idy = p.idy;
		sayWin = s;
	}
	
	public Win(String c, int i, boolean s){
		chr = c; idy = i; sayWin = s;
	}
}
