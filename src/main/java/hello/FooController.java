package hello;

import javax.annotation.security.RolesAllowed;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Sephiroth
 */
@Controller
public class FooController
{
	@RolesAllowed("DEVELOPERS")
	@RequestMapping("secured-method")
	public String securedMethod(@AuthenticationPrincipal UserDetails user, Model model)
	{
		model.addAttribute("name", user.getUsername());
		return "securedMethod";
	}
}
