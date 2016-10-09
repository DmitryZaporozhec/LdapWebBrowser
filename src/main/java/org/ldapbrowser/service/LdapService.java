package org.ldapbrowser.service;

import org.ldapbrowser.model.LdapConnectionConfig;
import org.ldapbrowser.model.LdapNode;
import org.ldapbrowser.model.WebResponse;

import java.util.List;

/**
 * Created by zaporozhec on 10/8/16.
 */
public interface LdapService {
    public String testConnection(LdapConnectionConfig config);

    public WebResponse<List<LdapNode>> search(LdapConnectionConfig config) throws Exception;
}
