/**
 * Copyright (c) 2003-2006, David A. Czarnecki
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
package org.blojsom.plugin.pingback;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.apache.xmlrpc.AsyncCallback;
import org.apache.xmlrpc.XmlRpcClient;
import org.blojsom.blog.BlogEntry;
import org.blojsom.blog.BlogUser;
import org.blojsom.blog.BlojsomConfiguration;
import org.blojsom.blog.Blog;
import org.blojsom.event.BlojsomEvent;
import org.blojsom.event.BlojsomListener;
import org.blojsom.plugin.BlojsomPlugin;
import org.blojsom.plugin.BlojsomPluginException;
import org.blojsom.plugin.common.VelocityPlugin;
import org.blojsom.plugin.pingback.event.PingbackAddedEvent;
import org.blojsom.plugin.email.EmailConstants;
import org.blojsom.plugin.admin.event.AddBlogEntryEvent;
import org.blojsom.plugin.admin.event.BlogEntryEvent;
import org.blojsom.plugin.admin.event.UpdatedBlogEntryEvent;
import org.blojsom.util.BlojsomConstants;
import org.blojsom.util.BlojsomUtils;
import org.blojsom.util.BlojsomMetaDataConstants;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.mail.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Vector;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Pingback plugin implements a pingback client to send pingbacks to any URLs in a blog entry according to the
 * <a href="http://www.hixie.ch/specs/pingback/pingback">Pingback 1.0</a> specification.
 *
 * @author David Czarnecki
 * @version $Id: PingbackPlugin.java,v 1.8 2006-02-24 23:36:48 czarneckid Exp $
 * @since blojsom 2.24
 */
public class PingbackPlugin extends VelocityPlugin implements BlojsomPlugin, BlojsomListener, BlojsomConstants, EmailConstants {

    private static Log _logger = LogFactory.getLog(PingbackPlugin.class);

    private static final String PINGBACK_PLUGIN_EMAIL_TEMPLATE_HTML = "org/blojsom/plugin/pingback/pingback-plugin-email-template-html.vm";
    private static final String PINGBACK_PLUGIN_EMAIL_TEMPLATE_TEXT = "org/blojsom/plugin/pingback/pingback-plugin-email-template-text.vm";

    private static final String DEFAULT_PINGBACK_PREFIX = "[blojsom] Pingback on: ";
    private static final String PINGBACK_PREFIX_IP = "plugin-pingback-email-prefix";
    private static final String BLOJSOM_PINGBACK_PLUGIN_BLOG_ENTRY = "BLOJSOM_PINGBACK_PLUGIN_BLOG_ENTRY";
    private static final String BLOJSOM_PINGBACK_PLUGIN_PINGBACK = "BLOJSOM_PINGBACK_PLUGIN_PINGBACK";

    private static final String PINGBACK_METHOD = "pingback.ping";
    private static final String X_PINGBACK_HEADER = "X-Pingback";
    private static final String PINGBACK_LINK_REGEX = "<link rel=\"pingback\" href=\"([^\"]+)\" ?/?>";
    private static final String HREF_REGEX = "href\\s*=\\s*\"(.*?)\"";

    /**
     * IP address meta-data
     */
    public static final String BLOJSOM_PINGBACK_PLUGIN_METADATA_IP = "BLOJSOM_PINGBACK_PLUGIN_METADATA_IP";

    public static final String BLOJSOM_PLUGIN_PINGBACK_METADATA_DESTROY = "BLOJSOM_PLUGIN_PINGBACK_METADATA_DESTROY";
    public static final String PINGBACK_PLUGIN_METADATA_SEND_PINGBACKS = "send-pingbacks";

    private String _mailServer;
    private String _mailServerUsername;
    private String _mailServerPassword;
    private Session _session;

    private PingbackPluginAsyncCallback _callbackHandler;

    /**
     * Create a new instance of the Pingback plugin
     */
    public PingbackPlugin() {
    }

    /**
     * Initialize this plugin. This method only called when the plugin is instantiated.
     *
     * @param servletConfig        Servlet config object for the plugin to retrieve any initialization parameters
     * @param blojsomConfiguration {@link org.blojsom.blog.BlojsomConfiguration} information
     * @throws org.blojsom.plugin.BlojsomPluginException
     *          If there is an error initializing the plugin
     */
    public void init(ServletConfig servletConfig, BlojsomConfiguration blojsomConfiguration) throws BlojsomPluginException {
        _callbackHandler = new PingbackPluginAsyncCallback();

        _mailServer = servletConfig.getInitParameter(SMTPSERVER_IP);

        if (_mailServer != null) {
            if (_mailServer.startsWith("java:comp/env")) {
                try {
                    Context context = new InitialContext();
                    _session = (Session) context.lookup(_mailServer);
                } catch (NamingException e) {
                    _logger.error(e);
                    throw new BlojsomPluginException(e);
                }
            } else {
                _mailServerUsername = servletConfig.getInitParameter(SMTPSERVER_USERNAME_IP);
                _mailServerPassword = servletConfig.getInitParameter(SMTPSERVER_PASSWORD_IP);
            }
        } else {
            throw new BlojsomPluginException("Missing SMTP servername servlet initialization parameter: " + SMTPSERVER_IP);
        }

        blojsomConfiguration.getEventBroadcaster().addListener(this);

        _logger.debug("Initialized pingback plugin");
    }

    /**
     * Process the blog entries
     *
     * @param httpServletRequest  Request
     * @param httpServletResponse Response
     * @param user                {@link org.blojsom.blog.BlogUser} instance
     * @param context             Context
     * @param entries             Blog entries retrieved for the particular request
     * @return Modified set of blog entries
     * @throws org.blojsom.plugin.BlojsomPluginException
     *          If there is an error processing the blog entries
     */
    public BlogEntry[] process(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlogUser user, Map context, BlogEntry[] entries) throws BlojsomPluginException {
        return entries;
    }

    /**
     * Perform any cleanup for the plugin. Called after {@link #process}.
     *
     * @throws org.blojsom.plugin.BlojsomPluginException
     *          If there is an error performing cleanup for this plugin
     */
    public void cleanup() throws BlojsomPluginException {
    }

    /**
     * Called when BlojsomServlet is taken out of service
     *
     * @throws org.blojsom.plugin.BlojsomPluginException
     *          If there is an error in finalizing this plugin
     */
    public void destroy() throws BlojsomPluginException {
    }

    /**
     * Setup the pingback e-mail
     *
     * @param blog  {@link org.blojsom.blog.Blog} information
     * @param email Email message
     * @throws org.apache.commons.mail.EmailException If there is an error preparing the e-mail message
     */
    protected void setupEmail(Blog blog, BlogEntry entry, Email email) throws EmailException {
        email.setCharset(UTF8);

        // If we have a mail session for the environment, use that
        if (_session != null) {
            email.setMailSession(_session);
        } else {
            // Otherwise, if there is a username and password for the mail server, use that
            if (!BlojsomUtils.checkNullOrBlank(_mailServerUsername) && !BlojsomUtils.checkNullOrBlank(_mailServerPassword)) {
                email.setHostName(_mailServer);
                email.setAuthentication(_mailServerUsername, _mailServerPassword);
            } else {
                email.setHostName(_mailServer);
            }
        }

        email.setFrom(blog.getBlogOwnerEmail(), "Blojsom Pingback");

        String author;
        if (entry.getMetaData().get(BlojsomMetaDataConstants.BLOG_ENTRY_METADATA_AUTHOR) != null) {
            author = (String) entry.getMetaData().get(BlojsomMetaDataConstants.BLOG_ENTRY_METADATA_AUTHOR);
        } else {
            author = blog.getBlogOwner();
        }

        String authorEmail = blog.getBlogOwnerEmail();

        if (author != null) {
            authorEmail = blog.getAuthorizedUserEmail(author);
            if (BlojsomUtils.checkNullOrBlank(authorEmail)) {
                authorEmail = blog.getBlogOwnerEmail();
            }
        }

        email.addTo(authorEmail, author);
        email.setSentDate(new Date());
    }

    /**
     * Handle an event broadcast from another component
     *
     * @param event {@link org.blojsom.event.BlojsomEvent} to be handled
     */
    public void handleEvent(BlojsomEvent event) {
        if (event instanceof AddBlogEntryEvent || event instanceof UpdatedBlogEntryEvent) {
            BlogEntryEvent blogEntryEvent = (BlogEntryEvent) event;

            String text = blogEntryEvent.getBlogEntry().getDescription();
            if (!BlojsomUtils.checkNullOrBlank(text) && BlojsomUtils.checkMapForKey(blogEntryEvent.getBlogEntry().getMetaData(), PINGBACK_PLUGIN_METADATA_SEND_PINGBACKS)) {
                String pingbackURL;
                String sourceURI = blogEntryEvent.getBlogEntry().getLink();
                String targetURI;

                Pattern hrefPattern = Pattern.compile(HREF_REGEX, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.UNICODE_CASE | Pattern.DOTALL);
                Matcher hrefMatcher = hrefPattern.matcher(text);
                _logger.debug("Checking for href's in entry: " + blogEntryEvent.getBlogEntry().getPermalink());
                while (hrefMatcher.find()) {
                    targetURI = hrefMatcher.group(1);
                    _logger.debug("Found potential targetURI: " + targetURI);

                    // Perform an HTTP request and first see if the X-Pingback header is available
                    try {
                        HttpURLConnection urlConnection = (HttpURLConnection) new URL(targetURI).openConnection();
                        urlConnection.setRequestMethod("GET");
                        urlConnection.connect();
                        pingbackURL = urlConnection.getHeaderField(X_PINGBACK_HEADER);

                        // If the header is not available, look for the link in the URL content
                        if (pingbackURL == null) {
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), UTF8));
                            StringBuffer content = new StringBuffer();
                            String input;
                            while ((input = bufferedReader.readLine()) != null) {
                                content.append(input).append(LINE_SEPARATOR);
                            }
                            bufferedReader.close();

                            Pattern pingbackLinkPattern = Pattern.compile(PINGBACK_LINK_REGEX, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.UNICODE_CASE | Pattern.DOTALL);
                            Matcher pingbackLinkMatcher = pingbackLinkPattern.matcher(content.toString());
                            if (pingbackLinkMatcher.find()) {
                                pingbackURL = pingbackLinkMatcher.group(1);
                            }
                        }

                        // Finally, send the pingback
                        if (pingbackURL != null && targetURI != null) {
                            Vector parameters = new Vector();
                            parameters.add(sourceURI);
                            parameters.add(targetURI);
                            try {
                                _logger.debug("Sending pingback to: " + pingbackURL + " sourceURI: " + sourceURI + " targetURI: " + targetURI);
                                XmlRpcClient xmlRpcClient = new XmlRpcClient(pingbackURL);
                                xmlRpcClient.executeAsync(PINGBACK_METHOD, parameters, _callbackHandler);
                            } catch (MalformedURLException e) {
                                _logger.error(e);
                            }
                        }
                    } catch (IOException e) {
                        _logger.error(e);
                    }
                }
            } else {
                _logger.debug("No text in blog entry or " + PINGBACK_PLUGIN_METADATA_SEND_PINGBACKS + " not enabled.");
            }
        } else if (event instanceof PingbackAddedEvent) {
            HtmlEmail email = new HtmlEmail();
            PingbackAddedEvent pingbackAddedEvent = (PingbackAddedEvent) event;

            if (pingbackAddedEvent.getBlogUser().getBlog().getBlogEmailEnabled().booleanValue()) {
                try {
                    setupEmail(pingbackAddedEvent.getBlogUser().getBlog(), pingbackAddedEvent.getBlogEntry(), email);

                    Map emailTemplateContext = new HashMap();
                    emailTemplateContext.put(BLOJSOM_BLOG, pingbackAddedEvent.getBlogUser().getBlog());
                    emailTemplateContext.put(BLOJSOM_USER, pingbackAddedEvent.getBlogUser());
                    emailTemplateContext.put(BLOJSOM_PINGBACK_PLUGIN_PINGBACK, pingbackAddedEvent.getPingback());
                    emailTemplateContext.put(BLOJSOM_PINGBACK_PLUGIN_BLOG_ENTRY, pingbackAddedEvent.getBlogEntry());

                    String htmlText = mergeTemplate(PINGBACK_PLUGIN_EMAIL_TEMPLATE_HTML, pingbackAddedEvent.getBlogUser(), emailTemplateContext);
                    String plainText = mergeTemplate(PINGBACK_PLUGIN_EMAIL_TEMPLATE_TEXT, pingbackAddedEvent.getBlogUser(), emailTemplateContext);

                    email.setHtmlMsg(htmlText);
                    email.setTextMsg(plainText);

                    String emailPrefix = pingbackAddedEvent.getBlogUser().getBlog().getBlogProperty(PINGBACK_PREFIX_IP);
                    if (BlojsomUtils.checkNullOrBlank(emailPrefix)) {
                        emailPrefix = DEFAULT_PINGBACK_PREFIX;
                    }

                    email = (HtmlEmail) email.setSubject(emailPrefix + pingbackAddedEvent.getBlogEntry().getTitle());

                    email.send();
                } catch (EmailException e) {
                    _logger.error(e);
                }
            }
        }
    }

    /**
     * Process an event from another component
     *
     * @param event {@link BlojsomEvent} to be handled
     * @since blojsom 2.24
     */
    public void processEvent(BlojsomEvent event) {
    }

    /**
     * Asynchronous callback handler for the pingback ping
     */
    private class PingbackPluginAsyncCallback implements AsyncCallback {

        /**
         * Default constructor
         */
        public PingbackPluginAsyncCallback() {
        }

        /**
         * Call went ok, handle result.
         *
         * @param o   Return object
         * @param url URL
         * @param s   String
         */
        public void handleResult(Object o, URL url, String s) {
            _logger.debug(o.toString());
        }

        /**
         * Something went wrong, handle error.
         *
         * @param e   Exception containing error from XML-RPC call
         * @param url URL
         * @param s   String
         */
        public void handleError(Exception e, URL url, String s) {
            _logger.error(e);
        }
    }
}