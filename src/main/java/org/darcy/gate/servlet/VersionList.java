package org.darcy.gate.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.darcy.gate.version.VersionManager;

import net.sf.json.JSONArray;

public class VersionList extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static HashMap<String, String> tmpVersionList = new HashMap<String, String>();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		String cv = request.getParameter("v");
		if (cv == null)
			return;
		try {
			String jsonStr = (String) tmpVersionList.get(cv);
			if (jsonStr == null) {
				List<?> list = VersionManager.getUps(cv);
				JSONArray jsonArray = JSONArray.fromObject(list);
				jsonStr = jsonArray.toString();
				tmpVersionList.put(cv, jsonStr);
				if (tmpVersionList.size() > 100) {
					tmpVersionList.clear();
				}
			}

			response.setCharacterEncoding("utf-8");

			response.getWriter().write(jsonStr);
		} catch (Exception e) {
			response.setCharacterEncoding("utf-8");

			response.getWriter().write(e.toString());
			response.flushBuffer();
		}
	}
}
