/**
 * Copyright (c) 2003, David A. Czarnecki
 * All rights reserved.
 *
 * Portions Copyright (c) 2003 by Mark Lussier
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
 *      this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * Neither the name of the "David A. Czarnecki" and "blojsom" nor the names of
 * its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * Products derived from this software may not be called "blojsom",
 * nor may "blojsom" appear in their name, without prior written permission of
 * David A. Czarnecki.
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
package org.blojsom.plugin.admin;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.blojsom.blog.Blog;
import org.blojsom.blog.BlogEntry;
import org.blojsom.blog.BlogUser;
import org.blojsom.blog.BlojsomConfiguration;
import org.blojsom.plugin.BlojsomPluginException;
import org.blojsom.util.BlojsomConstants;
import org.blojsom.util.BlojsomUtils;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;
import java.util.Properties;

/**
 * EditBlogPropertiesPlugin
 *
 * @author David Czarnecki
 * @since blojsom 2.04
 * @version $Id: EditBlogPropertiesPlugin.java,v 1.1 2003-10-21 03:34:33 czarneckid Exp $
 */
public class EditBlogPropertiesPlugin extends BaseAdminPlugin {

    private static Log _logger = LogFactory.getLog(EditBlogPropertiesPlugin.class);

    private static final String EDIT_BLOG_PROPERTIES_PAGE = "/org/blojsom/plugin/admin/admin-edit-blog-properties";
    private static final String EDIT_BLOG_PROPERTIES_ACTION = "edit-blog-properties";

    /**
     *
     */
    public EditBlogPropertiesPlugin() {
    }

    /**
     * Initialize this plugin. This method only called when the plugin is instantiated.
     *
     * @param servletConfig Servlet config object for the plugin to retrieve any initialization parameters
     * @param blojsomConfiguration {@link org.blojsom.blog.BlojsomConfiguration} information
     * @throws org.blojsom.plugin.BlojsomPluginException If there is an error initializing the plugin
     */
    public void init(ServletConfig servletConfig, BlojsomConfiguration blojsomConfiguration) throws BlojsomPluginException {
        super.init(servletConfig, blojsomConfiguration);
    }

    /**
     * Process the blog entries
     *
     * @param httpServletRequest Request
     * @param httpServletResponse Response
     * @param user {@link org.blojsom.blog.BlogUser} instance
     * @param context Context
     * @param entries Blog entries retrieved for the particular request
     * @return Modified set of blog entries
     * @throws BlojsomPluginException If there is an error processing the blog entries
     */
    public BlogEntry[] process(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlogUser user, Map context, BlogEntry[] entries) throws BlojsomPluginException {
        if (!authenticateUser(httpServletRequest, user.getBlog())) {
            httpServletRequest.setAttribute(PAGE_PARAM, ADMIN_LOGIN_PAGE);
            return entries;
        }

        String action = BlojsomUtils.getRequestValue(ACTION_PARAM, httpServletRequest);
        if (action == null || "".equals(action)) {
            _logger.debug("User did not request edit action");
            httpServletRequest.setAttribute(PAGE_PARAM, ADMIN_ADMINISTRATION_PAGE);
            return entries;
        } else if (PAGE_ACTION.equals(action)) {
            _logger.debug("User requested edit page");
            httpServletRequest.setAttribute(PAGE_PARAM, EDIT_BLOG_PROPERTIES_PAGE);
            return entries;
        } else if (EDIT_BLOG_PROPERTIES_ACTION.equals(action)) {
            _logger.debug("User requested edit action");
            Blog blog = user.getBlog();

            String blogPropertyValue = BlojsomUtils.getRequestValue("blog-name", httpServletRequest);
            blog.setBlogName(blogPropertyValue);
            blogPropertyValue = BlojsomUtils.getRequestValue("blog-description", httpServletRequest);
            blog.setBlogDescription(blogPropertyValue);
            blogPropertyValue = BlojsomUtils.getRequestValue("blog-url", httpServletRequest);
            blog.setBlogURL(blogPropertyValue);
            blogPropertyValue = BlojsomUtils.getRequestValue("blog-base-url", httpServletRequest);
            blog.setBlogBaseURL(blogPropertyValue);
            blogPropertyValue = BlojsomUtils.getRequestValue("blog-country", httpServletRequest);
            blog.setBlogCountry(blogPropertyValue);
            blogPropertyValue = BlojsomUtils.getRequestValue("blog-language", httpServletRequest);
            blog.setBlogLanguage(blogPropertyValue);
            blogPropertyValue = BlojsomUtils.getRequestValue("blog-depth", httpServletRequest);
            try {
                int blogDepth = Integer.parseInt(blogPropertyValue);
                blog.setBlogDepth(blogDepth);
            } catch (NumberFormatException e) {
                _logger.error("Blog depth parameter invalid.", e);
            }
            blogPropertyValue = BlojsomUtils.getRequestValue("blog-display-entries", httpServletRequest);
            try {
                int blogDisplayEntries = Integer.parseInt(blogPropertyValue);
                blog.setBlogDisplayEntries(blogDisplayEntries);
            } catch (NumberFormatException e) {
                _logger.error("Blog display entries parameter invalid.", e);
            }
            blogPropertyValue = BlojsomUtils.getRequestValue("blog-owner", httpServletRequest);
            blog.setBlogOwner(blogPropertyValue);
            blogPropertyValue = BlojsomUtils.getRequestValue("blog-owner-email", httpServletRequest);
            blog.setBlogOwnerEmail(blogPropertyValue);
            blogPropertyValue = BlojsomUtils.getRequestValue("blog-comments-enabled", httpServletRequest);
            blog.setBlogCommentsEnabled(Boolean.valueOf(blogPropertyValue));
            blogPropertyValue = BlojsomUtils.getRequestValue("blog-trackbacks-enabled", httpServletRequest);
            blog.setBlogTrackbacksEnabled(Boolean.valueOf(blogPropertyValue));
            blogPropertyValue = BlojsomUtils.getRequestValue("blog-email-enabled", httpServletRequest);
            blog.setBlogEmailEnabled(Boolean.valueOf(blogPropertyValue));
            blogPropertyValue = BlojsomUtils.getRequestValue("blog-file-encoding", httpServletRequest);
            blog.setBlogFileEncoding(blogPropertyValue);

            // Request that we go back to the edit blog properties page
            httpServletRequest.setAttribute(PAGE_PARAM, EDIT_BLOG_PROPERTIES_PAGE);

            // Write out new blog properties
            Properties blogProperties = BlojsomUtils.mapToProperties(blog.getBlogProperties(), blog.getBlogFileEncoding());
            File propertiesFile = new File(_blojsomConfiguration.getInstallationDirectory()
                    + BlojsomUtils.removeInitialSlash(_blojsomConfiguration.getBaseConfigurationDirectory()) +
                    "/" + user.getId() + "/" + BlojsomConstants.BLOG_DEFAULT_PROPERTIES);

            _logger.debug("Writing blog properties to: " + propertiesFile.toString());

            try {
                FileOutputStream fos = new FileOutputStream(propertiesFile);
                blogProperties.store(fos, null);
                fos.close();
            } catch (IOException e) {
                _logger.error(e);
            }

            return entries;
        }

        return entries;
    }

    /**
     * Perform any cleanup for the plugin. Called after {@link #process}.
     *
     * @throws BlojsomPluginException If there is an error performing cleanup for this plugin
     */
    public void cleanup() throws BlojsomPluginException {

    }

    /**
     * Called when BlojsomServlet is taken out of service
     *
     * @throws BlojsomPluginException If there is an error in finalizing this plugin
     */
    public void destroy() throws BlojsomPluginException {

    }
}