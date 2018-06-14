package org.darcy.sanguo.log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class LogOnce extends Log {
	File fout;
	FileOutputStream out;
	String type;

	public LogOnce(String type) {
		super(type);
		this.type = type;
	}

	public void logCombat(Object obj) {
		try {
			this.fout = new File(this.type);
			this.out = new FileOutputStream(this.fout);
			this.out.write(obj.toString().getBytes(Charset.forName("utf-8")));
			this.out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void flush() {
	}
}
