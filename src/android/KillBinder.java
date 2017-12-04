package com.mokto.audiocontroller;

import android.app.Service;
import android.os.Binder;

public class KillBinder extends Binder {
	public final Service service;

	public KillBinder(Service service) {
		this.service = service;
	}
}
