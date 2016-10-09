package kursovoy.service;

import kursovoy.model.LdapConnectionConfig;
import kursovoy.model.LdapNode;
import kursovoy.model.WebResponse;

import java.util.List;

/**
 * Created by zaporozhec on 10/8/16.
 */
public interface LdapService {
    public String testConnection(LdapConnectionConfig config);

    public WebResponse<List<LdapNode>> search(LdapConnectionConfig config) throws Exception;
}
