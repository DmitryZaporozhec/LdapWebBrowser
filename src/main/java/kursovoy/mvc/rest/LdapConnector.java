package kursovoy.mvc.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by zaporozhec on 10/8/16.
 */

@Controller
public class LdapConnector {
    @RequestMapping(value = "/")
    public String getPage() {
        return "editor";
    }
}
