package ca.idrc.tagin.tags;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ca.idrc.tagin.tags.dao.TagsDao;
import ca.idrc.tagin.tags.dao.TagsEntityManager;

import com.google.gson.Gson;

@SuppressWarnings("serial")
public class TagsServlet extends HttpServlet {
	
	private final String PARAM_URN = "urn";
	private final String PARAM_LABEL = "label";
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String urn = req.getParameter(PARAM_URN);
		String label = req.getParameter(PARAM_LABEL);

		if (urn != null && label != null && !urn.isEmpty() && !label.isEmpty()) {
			TagsDao dao = new TagsEntityManager();
			dao.assignLabel(urn, label);
			dao.close();
			resp.setContentType("application/json");
			resp.setCharacterEncoding("UTF-8");
			resp.getWriter().println(label);
		}
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String urn = req.getParameter(PARAM_URN);
		if (urn != null && !urn.isEmpty()) {
			TagsDao dao = new TagsEntityManager();
			List<String> labels = dao.getLabels(urn);
			dao.close();
			resp.setContentType("application/json");
			resp.setCharacterEncoding("UTF-8");
			resp.getWriter().println(new Gson().toJson(labels));
		}
	}
}
