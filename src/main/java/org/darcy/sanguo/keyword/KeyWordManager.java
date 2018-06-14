package org.darcy.sanguo.keyword;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;

import org.darcy.ServerStartup;

/**
 * 敏感字屏蔽
 */
public class KeyWordManager {
	public static final String TREE_CHAT_KEYWORD = "blackword.txt";
	public static final String TREE_NAME_KEYWORD = "nameblackword.txt";
	public static HashMap<String, KeyWordTree> trees = new HashMap<String, KeyWordTree>();

	private void buildKeyWordTree(String fileName) {
		URL url = ServerStartup.class.getClassLoader().getResource("data/" + fileName);
		File file = new File(url.getFile());
		KeyWordTree tree = new KeyWordTree();
		BufferedReader br = null;
		try {
			String line;
			FileInputStream fin = new FileInputStream(file);
			InputStreamReader inr = new InputStreamReader(fin, Charset.forName("utf-8"));
			br = new BufferedReader(inr);
			while ((line = br.readLine()) != null) {
				tree.addKeyWord(line);
			}
			trees.put(fileName, tree);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public KeyWordManager() {
		buildKeyWordTree("blackword.txt");
		buildKeyWordTree("nameblackword.txt");
	}

	public String mark(String tree, String s) {
		KeyWordTree t = (KeyWordTree) trees.get(tree);
		if (t != null) {
			return t.mark(s);
		}
		return s;
	}

	public boolean contains(String tree, String s) {
		KeyWordTree t = (KeyWordTree) trees.get(tree);
		if (t != null) {
			return t.containsKeyWork(s);
		}
		return false;
	}

	public static void main(String[] args) {
		KeyWordManager km = new KeyWordManager();

		String s = km.mark("blackword.txt", "江泽民是个傻逼");

		System.out.println(s);
		System.out.println(km.contains("blackword.txt", "江泽民是个傻逼"));
	}
}
