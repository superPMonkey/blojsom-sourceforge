/**
 * Copyright (c) 2003-2007, David A. Czarnecki
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions and the
 *     following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 *     following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of "David A. Czarnecki" and "blojsom" nor the names of its contributors may be used to
 *     endorse or promote products derived from this software without specific prior written permission.
 * Products derived from this software may not be called "blojsom", nor may "blojsom" appear in their name,
 *     without prior written permission of David A. Czarnecki.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
 * AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.blojsom.plugin.moderation.admin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.blojsom.blog.Blog;
import org.blojsom.blog.Entry;
import org.blojsom.fetcher.Fetcher;
import org.blojsom.fetcher.FetcherException;
import org.blojsom.plugin.PluginException;
import org.blojsom.plugin.admin.WebAdminPlugin;
import org.blojsom.util.BlojsomConstants;
import org.blojsom.util.BlojsomUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * IP address moderation administration plugin
 *
 * @author David Czarnecki
 * @version $Id: IPAddressModerationAdminPlugin.java,v 1.2 2007-01-17 02:35:12 czarneckid Exp $
 * @since blojsom 3.0
 */
public class IPAddressModerationAdminPlugin extends WebAdminPlugin {

    private Log _logger = LogFactory.getLog(IPAddressModerationAdminPlugin.class);

    private static final String BLACKLIST = "blacklist";
    private static final String IP_BLACKLIST_IP = "ip-blacklist";
    private static final String IP_WHITELIST_IP = "ip-whitelist";

    // Localization constants
    private static final String FAILED_IP_ADDRESS_MODERATION_PERMISSION_KEY = "failed.ip.address.moderation.permission.text";
    private static final String ADDED_IP_TO_BLACKLIST_KEY = "added.ip.to.blacklist.text";
    private static final String IP_ALREADY_ADDED_TO_BLACKLIST_KEY = "ip.already.added.to.blacklist.text";
    private static final String ADDED_IP_TO_WHITELIST_KEY = "added.ip.to.whitelist.text";
    private static final String IP_ALREADY_ADDED_TO_WHITELIST_KEY = "ip.already.added.to.whitelist.text";
    private static final String DELETED_IP_ADDRESSES_BLACKLIST_KEY = "deleted.ip.addresses.blacklist.text";
    private static final String DELETED_IP_ADDRESSES_WHITELIST_KEY = "deleted.ip.addresses.whitelist.text";
    private static final String NO_IP_ADDRESSES_SELECTED_KEY = "no.ip.addresses.selected.text";

    // Context
    private static final String BLOJSOM_PLUGIN_IP_WHITELIST = "BLOJSOM_PLUGIN_IP_WHITELIST";
    private static final String BLOJSOM_PLUGIN_IP_BLACKLIST = "BLOJSOM_PLUGIN_IP_BLACKLIST";

    // Pages
    private static final String EDIT_IP_MODERATION_SETTINGS_PAGE = "/org/blojsom/plugin/moderation/admin/templates/admin-edit-ip-moderation-settings";

    // Form itmes
    private static final String IP_ADDRESS = "ip-address";
    private static final String LIST_TYPE = "list-type";

    // Actions
    private static final String ADD_IP_ADDRESS_ACTION = "add-ip-address";
    private static final String DELETE_IP_ADDRESS_ACTION = "delete-ip-address";

    // Permissions
    private static final String IP_MODERATION_PERMISSION = "ip_moderation";

    private Fetcher _fetcher;

    /**
     * Create a new instance of the IP address moderation administration plugin
     */
    public IPAddressModerationAdminPlugin() {
    }

    /**
     * Return the display name for the plugin
     *
     * @return Display name for the plugin
     */
    public String getDisplayName() {
        return "IP Address Moderation plugin";
    }

    /**
     * Return the name of the initial editing page for the plugin
     *
     * @return Name of the initial editing page for the plugin
     */
    public String getInitialPage() {
        return EDIT_IP_MODERATION_SETTINGS_PAGE;
    }

    /**
     * Set the {@link Fetcher}
     *
     * @param fetcher {@link Fetcher}
     */
    public void setFetcher(Fetcher fetcher) {
        _fetcher = fetcher;
    }

    /**
     * Initialize this plugin. This method only called when the plugin is instantiated.
     *
     * @throws org.blojsom.plugin.PluginException
     *          If there is an error initializing the plugin
     */
    public void init() throws PluginException {
        super.init();
    }

    /**
     * Process the blog entries
     *
     * @param httpServletRequest  Request
     * @param httpServletResponse Response
     * @param blog                {@link Blog} instance
     * @param context             Context
     * @param entries             Blog entries retrieved for the particular request
     * @return Modified set of blog entries
     * @throws PluginException If there is an error processing the blog entries
     */
    public Entry[] process(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Blog blog, Map context, Entry[] entries) throws PluginException {
        entries = super.process(httpServletRequest, httpServletResponse, blog, context, entries);

        String page = BlojsomUtils.getRequestValue(BlojsomConstants.PAGE_PARAM, httpServletRequest);

        String username = getUsernameFromSession(httpServletRequest, blog);
        if (!checkPermission(blog, null, username, IP_MODERATION_PERMISSION)) {
            httpServletRequest.setAttribute(BlojsomConstants.PAGE_PARAM, ADMIN_ADMINISTRATION_PAGE);
            addOperationResultMessage(context, getAdminResource(FAILED_IP_ADDRESS_MODERATION_PERMISSION_KEY, FAILED_IP_ADDRESS_MODERATION_PERMISSION_KEY, blog.getBlogAdministrationLocale()));

            return entries;
        }

        if (ADMIN_LOGIN_PAGE.equals(page)) {
            return entries;
        } else {
            String action = BlojsomUtils.getRequestValue(ACTION_PARAM, httpServletRequest);
            List ipAddressesFromBlacklist = loadIPList(blog, IP_BLACKLIST_IP);
            List ipAddressesFromWhitelist = loadIPList(blog, IP_WHITELIST_IP);
            String listType = BlojsomUtils.getRequestValue(LIST_TYPE, httpServletRequest);

            if (ADD_IP_ADDRESS_ACTION.equals(action)) {
                String ipAddress = BlojsomUtils.getRequestValue(IP_ADDRESS, httpServletRequest);

                if (BLACKLIST.equals(listType)) {
                    if (!ipAddressesFromBlacklist.contains(ipAddress)) {
                        ipAddressesFromBlacklist.add(ipAddress);
                        blog.setProperty(IP_BLACKLIST_IP, BlojsomUtils.listToString(ipAddressesFromBlacklist, "\n"));
                        try {
                            _fetcher.saveBlog(blog);
                        } catch (FetcherException e) {
                            if (_logger.isErrorEnabled()) {
                                _logger.error(e);
                            }
                        }
                        addOperationResultMessage(context, formatAdminResource(ADDED_IP_TO_BLACKLIST_KEY, ADDED_IP_TO_BLACKLIST_KEY, blog.getBlogAdministrationLocale(), new Object[] {ipAddress}));
                    } else {
                        addOperationResultMessage(context, formatAdminResource(IP_ALREADY_ADDED_TO_BLACKLIST_KEY, IP_ALREADY_ADDED_TO_BLACKLIST_KEY, blog.getBlogAdministrationLocale(), new Object[] {ipAddress}));
                    }
                } else {
                    if (!ipAddressesFromWhitelist.contains(ipAddress)) {
                        ipAddressesFromWhitelist.add(ipAddress);
                        blog.setProperty(IP_WHITELIST_IP, BlojsomUtils.listToString(ipAddressesFromWhitelist, "\n"));
                        try {
                            _fetcher.saveBlog(blog);
                        } catch (FetcherException e) {
                            if (_logger.isErrorEnabled()) {
                                _logger.error(e);
                            }
                        }
                        addOperationResultMessage(context, formatAdminResource(ADDED_IP_TO_WHITELIST_KEY, ADDED_IP_TO_WHITELIST_KEY, blog.getBlogAdministrationLocale(), new Object[] {ipAddress}));
                    } else {
                        addOperationResultMessage(context, formatAdminResource(IP_ALREADY_ADDED_TO_WHITELIST_KEY, IP_ALREADY_ADDED_TO_WHITELIST_KEY, blog.getBlogAdministrationLocale(), new Object[] {ipAddress}));
                    }
                }
            } else if (DELETE_IP_ADDRESS_ACTION.equals(action)) {
                String[] ipAddressesToDelete = httpServletRequest.getParameterValues(IP_ADDRESS);

                if (ipAddressesToDelete != null && ipAddressesToDelete.length > 0) {
                    if (BLACKLIST.equals(listType)) {
                        for (int i = 0; i < ipAddressesToDelete.length; i++) {
                            ipAddressesFromBlacklist.set(Integer.parseInt(ipAddressesToDelete[i]), null);
                        }

                        ipAddressesFromBlacklist = BlojsomUtils.removeNullValues(ipAddressesFromBlacklist);
                        blog.setProperty(IP_BLACKLIST_IP, BlojsomUtils.listToString(ipAddressesFromBlacklist, "\n"));
                        try {
                            _fetcher.saveBlog(blog);
                        } catch (FetcherException e) {
                            if (_logger.isErrorEnabled()) {
                                _logger.error(e);
                            }
                        }
                        addOperationResultMessage(context, formatAdminResource(DELETED_IP_ADDRESSES_BLACKLIST_KEY, DELETED_IP_ADDRESSES_BLACKLIST_KEY, blog.getBlogAdministrationLocale(), new Object[] {new Integer(ipAddressesToDelete.length)}));
                    } else {
                        for (int i = 0; i < ipAddressesToDelete.length; i++) {
                            ipAddressesFromWhitelist.set(Integer.parseInt(ipAddressesToDelete[i]), null);
                        }

                        ipAddressesFromWhitelist = BlojsomUtils.removeNullValues(ipAddressesFromWhitelist);
                        blog.setProperty(IP_WHITELIST_IP, BlojsomUtils.listToString(ipAddressesFromWhitelist, "\n"));
                        try {
                            _fetcher.saveBlog(blog);
                        } catch (FetcherException e) {
                            if (_logger.isErrorEnabled()) {
                                _logger.error(e);
                            }
                        }
                        addOperationResultMessage(context, formatAdminResource(DELETED_IP_ADDRESSES_WHITELIST_KEY, DELETED_IP_ADDRESSES_WHITELIST_KEY, blog.getBlogAdministrationLocale(), new Object[] {new Integer(ipAddressesToDelete.length)}));
                    }
                } else {
                    addOperationResultMessage(context, getAdminResource(NO_IP_ADDRESSES_SELECTED_KEY, NO_IP_ADDRESSES_SELECTED_KEY, blog.getBlogAdministrationLocale()));
                }
            }

            context.put(BLOJSOM_PLUGIN_IP_BLACKLIST, ipAddressesFromBlacklist);
            context.put(BLOJSOM_PLUGIN_IP_WHITELIST, ipAddressesFromWhitelist);
        }

        return entries;
    }

    /**
     * Load the list of IP addresses from whitelist or blacklist from the blog properties
     *
     * @param blog {@link Blog}
     * @param property Whitelist or Blacklist property
     * @return List of IP addresses
     */
    protected List loadIPList(Blog blog, String property) {
        ArrayList ipAddresses = new ArrayList();
        String ipAddressValues = blog.getProperty(property);

        if (!BlojsomUtils.checkNullOrBlank(ipAddressValues)) {
            try {
                BufferedReader br = new BufferedReader(new StringReader(ipAddressValues));
                String ipAddress;

                while ((ipAddress = br.readLine()) != null) {
                    ipAddresses.add(ipAddress);
                }

                br.close();
            } catch (IOException e) {
                if (_logger.isErrorEnabled()) {
                    _logger.error(e);
                }
            }
        }

        return ipAddresses;
    }
}