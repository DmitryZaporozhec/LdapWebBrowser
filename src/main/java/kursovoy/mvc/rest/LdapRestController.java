package kursovoy.mvc.rest;

import kursovoy.model.LdapConnectionConfig;
import kursovoy.model.LdapNode;
import kursovoy.model.WebResponse;
import kursovoy.service.LdapService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping(value = "/rest/api/ldap")
@RestController
public class LdapRestController {
    Logger logger = Logger.getLogger(LdapRestController.class);
    @Autowired
    private LdapService ldapService;

    @RequestMapping(value = "/test", method = RequestMethod.POST)
    public
    @ResponseBody
    WebResponse testConnection(HttpServletRequest httpServletRequest, @RequestBody LdapConnectionConfig config) {
        WebResponse response = new WebResponse();
        response.setMessage(ldapService.testConnection(config));
        return response;
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public
    @ResponseBody
    WebResponse<List<LdapNode>> getRoot(HttpServletRequest httpServletRequest, @RequestBody LdapConnectionConfig config) {
        WebResponse<List<LdapNode>> result = null;
        try {
            result = ldapService.search(config);
        } catch (Exception e) {
            logger.error(e);
        }
        return result;
    }
}
