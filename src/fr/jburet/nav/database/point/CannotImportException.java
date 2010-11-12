package fr.jburet.nav.database.point;

import java.io.FileNotFoundException;

public class CannotImportException extends Exception {

	public CannotImportException(String message) {
		super(message);
	}

	public CannotImportException(String message, Throwable t) {
		super(message, t);
	}

}
