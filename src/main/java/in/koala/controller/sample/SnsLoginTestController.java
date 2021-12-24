package in.koala.controller.sample;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SnsLoginTestController {

    private final String AUD = "com.koala.services";
    private final String APPLE_WEBSITE_URL = "https://koala.im";

    @GetMapping(value = "/applelogin")
    public String appleLoginPage(ModelMap model) {

        model.addAttribute("client_id", AUD);
        model.addAttribute("redirect_uri", APPLE_WEBSITE_URL);
        model.addAttribute("nonce", "asd");

        return "test/appleLogin";
    }

    @GetMapping(value="/naverlogin")
    public String appleLoginPage(){
        return "test/naverlogin";
    }

    @GetMapping(value = "/callback")
    public String naverCallBack(){
        return "test/callback";
    }
}
