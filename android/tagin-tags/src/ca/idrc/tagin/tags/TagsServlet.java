package ca.idrc.tagin.tags;

import java.io.IOException;
import javax.servlet.http.*;

import ca.idrc.tagin.tags.dao.TagsDao;
import ca.idrc.tagin.tags.dao.TagsEntityManager;

@SuppressWarnings("serial")
public class TagsServlet extends HttpServlet {
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		System.out.println("Creating new tag");
		
		TagsDao dao = new TagsEntityManager();
		String urn = req.getParameter("urn");
		String label = req.getParameter("label");
		dao.assignLabel(urn, label);
		
		resp.setContentType("application/json");
		resp.getWriter().println(label);
	}
}
