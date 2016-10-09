package org.ldapbrowser.model;

import java.io.Serializable;

/**
 * Created by zaporozhec on 10/8/16.
 */
public class LdapConnectionConfig implements Serializable {
    private String url;
    private String userName;
    private String userPassword;
    private String baseDN;
    private String searchFilter;

    public String getBaseDN() {
        return baseDN;
    }

    public void setBaseDN(String baseDN) {
        this.baseDN = baseDN;
    }

    public String getSearchFilter() {
        return searchFilter;
    }

    public void setSearchFilter(String searchFilter) {
        this.searchFilter = searchFilter;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LdapConnectionConfig)) return false;

        LdapConnectionConfig that = (LdapConnectionConfig) o;

        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        if (userName != null ? !userName.equals(that.userName) : that.userName != null) return false;
        return userPassword != null ? userPassword.equals(that.userPassword) : that.userPassword == null;

    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (userPassword != null ? userPassword.hashCode() : 0);
        return result;
    }
}
