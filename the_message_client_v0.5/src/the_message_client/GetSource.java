package the_message_client;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.MalformedURLException;
import java.net.URL;

public class GetSource {

	public static final boolean isJar = false;

	public static AudioClip getAudio(String path) {
		if (isJar)
			return Applet.newAudioClip(new GetSource().getClass().getResource("/" + path));
		else
			try {
				return Applet.newAudioClip(new URL("file:" + System.getProperty("user.dir") + "/" + path));
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return null;
			}
	}

	public GetSource() {
	}

}
