package org.darcy.sanguo.hotfix;

import java.io.File;
import java.io.FileInputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;

import org.darcy.sanguo.Platform;

public class HotSwap {
	public static Instrumentation inst;

	public static boolean runThread(String clazzName) {
		try {
			Platform.getLog().logWorld("start load class");
			Class clazz = Class.forName(clazzName);
			Runnable r = (Runnable) clazz.newInstance();
			new Thread(r).start();
			Platform.getLog().logWorld("load ok");
			return true;
		} catch (Exception e) {
			Platform.getLog().logWarn(e);
		}
		return false;
	}

	public static boolean redefineClass(String name) {
		int index = name.lastIndexOf(46);
		String simpleName = null;
		if (index != -1)
			simpleName = name.substring(index + 1);
		else {
			simpleName = name;
		}
		File file = new File("./hotfix/" + simpleName + ".class");
		Platform.getLog().logWorld("hotswap file path:" + file.getPath());
		if (!(file.exists())) {
			return false;
		}
		// Instrumentation inst = inst;
		// if (inst == null) {
		// Platform.getLog().logWorld("inst is null");
		// return false;
		// }
		try {
			FileInputStream is = new FileInputStream(file);

			byte[] data = new byte[(int) file.length()];
			is.read(data);
			ClassDefinition def = new ClassDefinition(Class.forName(name), data);
			inst.redefineClasses(new ClassDefinition[] { def });
			Platform.getLog().logWorld("redefine ok.\n");
			is.close();
			return true;
		} catch (Exception e) {
			Platform.getLog().logWarn(e);
		}
		return false;
	}
}