package by.shyshaliaksey.webproject.controller.command.redirect;

import by.shyshaliaksey.webproject.controller.command.Command;
import by.shyshaliaksey.webproject.controller.command.FilePath;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RedirectRegisterCommand extends Command {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) {
		super.redirect(request, response, FilePath.REGISTER_JSP);
	}

}
