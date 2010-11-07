package fr.jburet.nav.utils;

import android.content.Intent;

public interface ResultCallback {
	public void resultOk(Intent data);

	public void resultCancel(Intent data);

}
