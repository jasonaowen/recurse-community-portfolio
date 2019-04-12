package com.recurse.portfolio;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {
    @RequestMapping("/")
    public ModelAndView index(@AuthenticationPrincipal DefaultOAuth2User user) {
        if (user == null) {
            return new ModelAndView("home");
        } else {
            return secure(user);
        }
    }

    @RequestMapping("/secure")
    public ModelAndView secure(@AuthenticationPrincipal DefaultOAuth2User user) {
        ModelAndView mv = new ModelAndView("secure");
        mv.addObject("username", user.getName());
        return mv;
    }
}
