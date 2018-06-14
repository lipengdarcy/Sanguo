package org.darcy.gate.listener;

import java.io.File;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.darcy.gate.notice.NoticeManager;
import org.darcy.gate.server.ServerManager;
import org.darcy.gate.version.VersionManager;

public class FileListener extends FileAlterationListenerAdaptor {
	public void onFileChange(File file) {
		if (file.getName().equals("servers.xls")) {
			ServerManager.reload();
		} else if (file.getName().equals("versions.xls")) {
			VersionManager.reload();
		} else {
			if (!(file.getName().equals("notice.xls")))
				return;
			NoticeManager.reload();
		}
	}
}
