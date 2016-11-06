package org.softwarewolf.gameserver.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.softwarewolf.gameserver.domain.User;
import org.softwarewolf.gameserver.domain.dto.UserDto;
import org.softwarewolf.gameserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/public")
public class AccountController {
	private static final String EMAIL_PATTERN = 
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	@Autowired
	protected UserRepository userRepository;

	@RequestMapping(value = "/createaccount", method = RequestMethod.GET)
	public String preCreateAccount(UserDto userDto) {
		
		return "/public/createaccount";
	}
	
	@RequestMapping(value = "postaccount", method = RequestMethod.POST)
	public ModelAndView createAccount(@ModelAttribute UserDto userDto, BindingResult bindingResult) {
		Pattern pattern = Pattern.compile(EMAIL_PATTERN);
		Matcher matcher = pattern.matcher(userDto.getEmail());
		
		String errMsg = "";
		// Does this usename already exist?
		String userName = userDto.getUsername();
		if (userName == null || userName.isEmpty()) {
			errMsg += "A user name is required.<br>";
		}
		User user = userRepository.findOneByUsername(userDto.getUsername());
		if (user != null) {
			errMsg = "You can not use that user name. Please pick another.\n";
		}
		if (!userDto.getPassword().equals(userDto.getVerifyPassword())) {
			errMsg += "Password does not match verification password.";
		}
		if (!matcher.matches()) {
			errMsg += "Not a vaild email address. A valid email address is required.\n";
		}
		
		
		if (!errMsg.isEmpty()) {
			ModelAndView mav = new ModelAndView("/public/createaccount", "userDto", userDto);
			mav.addObject("error", errMsg);
			return mav;
		}
		
		user = new User();
		user.setAccountNonExpired(true);
		user.setAccountNonLocked(true);
		user.setCredentialsNonExpired(true);
		user.setEmail(userDto.getEmail());
		user.setEnabled(false);
		String password = userDto.getPassword();
		if (password != null && !password.isEmpty()) {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
			password = encoder.encode(userDto.getPassword());
		}
		user.setPassword(password);		
		user.setUsername(userDto.getUsername());
		user.setFirstName(userDto.getFirstName());
		user.setLastName(userDto.getLastName());
		userRepository.save(user);
		
		return new ModelAndView("/public/postedaccount");
	}
}