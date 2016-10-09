package kursovoy.service.impl;

import kursovoy.model.LdapConnectionConfig;
import kursovoy.model.LdapNode;
import kursovoy.model.ResponseCode;
import kursovoy.model.WebResponse;
import kursovoy.service.LdapService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.*;
import java.util.*;

/**
 * Created by zaporozhec on 10/8/16.
 */
@Service
public class LdapServiceImpl implements LdapService {
    Logger log = Logger.getLogger(LdapServiceImpl.class);
    private final static Map<Integer, LdapContext> ldapConnections = new HashMap<Integer, LdapContext>();
    private final static int PAGE_SIZE = 100;
    private final static int SIZE_LIMIT = 100;
    private final static int TIME_LIMIT = 0;    //indefinitely

    @Override
    public String testConnection(LdapConnectionConfig config) {
        String retVal = "";
        LdapContext context = null;
        try {
            context = this.connect(config);
        } catch (Exception e) {
            retVal = e.getMessage();
        }
        if (context != null) {
            retVal = "Connected!";
        }
        return retVal;
    }

    protected LdapContext connect(LdapConnectionConfig config) throws NamingException {
        LdapContext context = null;
        if (ldapConnections.get(config.hashCode()) != null) {
            context = ldapConnections.get(config.hashCode());
            return context;
        } else {
            Hashtable<String, String> envDC = new Hashtable<String, String>(11);
//        System.setProperty("javax.net.ssl.trustStore", keystore);
            String hostUrl = config.getUrl(); //   managedSys.getHostUrl();
            if (log.isDebugEnabled()) {
                log.debug("Directory host url:" + hostUrl);
            }
            envDC.put(Context.PROVIDER_URL, hostUrl);
            envDC.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            envDC.put(Context.SECURITY_AUTHENTICATION, "simple"); // simple
            envDC.put("java.naming.ldap.attributes.binary", "objectGUID");
            envDC.put("java.naming.ldap.attributes.binary", "objectSid");
            envDC.put(Context.SECURITY_PRINCIPAL, config.getUserName());  //"administrator@diamelle.local"
            envDC.put(Context.SECURITY_CREDENTIALS, config.getUserPassword());
            if (hostUrl.toLowerCase().contains("ldaps")) {
                envDC.put(Context.SECURITY_PROTOCOL, "SSL");
            }
            context = new InitialLdapContext(envDC, null);
            ldapConnections.put(config.hashCode(), context);
            return context;
        }
    }

    @Override
    public WebResponse<List<LdapNode>> search(LdapConnectionConfig config) {
        WebResponse<List<LdapNode>> response = new WebResponse<List<LdapNode>>();
        response.setResponseCode(ResponseCode.OK);
        try {
            List<LdapNode> retVal = search(config, SearchControls.SUBTREE_SCOPE);
            response.setReturnObject(retVal);
        } catch (Exception e) {
            response.setResponseCode(ResponseCode.FAILURE);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    private List<LdapNode> search(LdapConnectionConfig config, int controlsA) throws Exception {
        List<LdapNode> nodes = new ArrayList<LdapNode>();
        LdapContext ctx = this.connect(config);
        SearchControls searchCtls = new SearchControls();
        ctx.setRequestControls(new Control[]{new PagedResultsControl(PAGE_SIZE, Control.NONCRITICAL)});
        searchCtls.setTimeLimit(TIME_LIMIT);
        searchCtls.setCountLimit(SIZE_LIMIT);
        searchCtls.setSearchScope(controlsA);
        byte[] cookie = null;
        int pageCounter = 0;
        int pageRowCount = 0;
        int totalRecords = 0;
        int ctr = 0;
        NamingEnumeration results = null;
        do {
            pageCounter++;
            pageRowCount = 0;
            try {
                results = ctx.search(config.getBaseDN(), config.getSearchFilter(), searchCtls);
            } catch (Exception sux) {
                if ("connection closed".equalsIgnoreCase(sux.getMessage())) {
                    ctx = null;
                    connect(config);
                    log.warn("Conection were closed. try to reconnect!");
                    return search(config, controlsA);
                } else throw sux;
            }
            while (results != null && results.hasMoreElements()) {
                pageRowCount++;
                totalRecords++;
                log.debug("LAST LDAP SYNC COUNTERS: TotalRecords=" + totalRecords + "");
                SearchResult sr = (SearchResult) results.nextElement();
                log.debug("SearchResultElement   : " + sr.getName());
                log.debug("Attributes: " + sr.getAttributes());
                log.debug("-New Row to Synchronize --" + ctr++);
                Attributes attrs = sr.getAttributes();
                LdapNode node = new LdapNode();
                Map<String, List<String>> attributes = new TreeMap<String, List<String>>();
                if (attrs != null) {
                    for (NamingEnumeration ae = attrs.getAll(); ae.hasMore(); ) {
                        Attribute attr = (Attribute) ae.next();
                        List<String> valueList = new ArrayList<String>();
                        String key = attr.getID();
                        log.debug("attribute id=: " + key);
                        for (NamingEnumeration e = attr.getAll(); e.hasMore(); ) {
                            Object o = e.next();
                            if ("objectGUID".equals(key)) {
                                valueList.add("BINARY:" + o.toString());
                            } else if (o instanceof byte[]) {
                                valueList.add("BINARY:" + String.valueOf((byte[]) o));
                            } else if (o.toString() != null) {
                                if ("distinguishedName".equals(key)) {
                                    node.setId(o.toString());
                                }
                                valueList.add(o.toString());
                            }
                        }
                        Collections.sort(valueList);
                        attributes.put(key, valueList);
                        node.setAttributes(attributes);
                    }
                    nodes.add(node);
                }
            }
            log.debug("LDAP Search PAGE RESULT: Page=" + pageCounter + ", rows= " + pageRowCount + " have been processed.");
            Control[] controls = ctx.getResponseControls();
            if (controls != null) {
                for (Control c : controls) {
                    if (c instanceof PagedResultsResponseControl) {
                        PagedResultsResponseControl prrc = (PagedResultsResponseControl) c;
                        cookie = prrc.getCookie();
                        break;
                    }
                }
            }
            ctx.setRequestControls(new Control[]{new PagedResultsControl(PAGE_SIZE, cookie, Control.CRITICAL)});
        } while (cookie != null);

        return nodes;
    }
}
