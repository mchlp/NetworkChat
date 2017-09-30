package chat;

import java.io.Serializable;

public class Name implements Serializable {

	private String nameText = "";

	public Name(String inputName) {
		nameText = inputName.toUpperCase();
	}

	public String getName() {
		return nameText;
	}

}
