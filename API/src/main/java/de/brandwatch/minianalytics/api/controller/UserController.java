package de.brandwatch.minianalytics.api.controller;

import de.brandwatch.minianalytics.api.security.model.User;
import de.brandwatch.minianalytics.api.security.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(value = "/registration")
    public String registration() {
        return "registration";
    }

    @PostMapping(value = "/registration")
    public String registerNewUser(User user) {
        try {
            userService.checkForUsernameDuplicate(user.getUsername());
            userService.save(user);

            return "redirect:/login";
        } catch (Exception e) {
            logger.error(e.getMessage());
        }

        return "registration";
    }
}
