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
package org.ignition.blojsom.fetcher;

import org.ignition.blojsom.blog.Blog;
import org.ignition.blojsom.blog.BlogCategory;
import org.ignition.blojsom.blog.BlogEntry;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * BlojsomFetcher
 *
 * @author David Czarnecki
 * @since blojsom 1.8
 * @version $Id: BlojsomFetcher.java,v 1.6 2003-06-11 02:47:34 czarneckid Exp $
 */
public interface BlojsomFetcher {

    /**
     * Initialize this fetcher. This method only called when the fetcher is instantiated.
     *
     * @param servletConfig Servlet config object for the plugin to retrieve any initialization parameters
     * @param blog {@link Blog} instance
     * @throws BlojsomFetcherException If there is an error initializing the fetcher
     */
    public void init(ServletConfig servletConfig, Blog blog) throws BlojsomFetcherException;

    /**
     * Return a new blog entry instance
     *
     * @since blojsom 1.9
     * @return Blog entry instance
     */
    public BlogEntry newBlogEntry();

    /**
     * Return a new blog category instance
     *
     * @since blojsom 1.9.1
     * @return Blog category instance
     */
    public BlogCategory newBlogCategory();

    /**
     * Fetch a set of {@link BlogEntry} objects.
     *
     * @param httpServletRequest Request
     * @param httpServletResponse Response
     * @param flavor Flavor
     * @param context Context
     * @return Blog entries retrieved for the particular request
     * @throws BlojsomFetcherException If there is an error retrieving the blog entries for the request
     */
    public BlogEntry[] fetchEntries(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    String flavor,
                                    Map context) throws BlojsomFetcherException;

    /**
     * Fetch a set of {@link BlogEntry} objects. This method is intended to be used for other
     * components such as the XML-RPC handlers that cannot generate servlet request and
     * response objects, but still need to be able to fetch entries. Implementations of this
     * method <b>must</b> be explicit about the exact parameter names and types that are
     * expected to return an appropriate set of {@link BlogEntry} objects.
     *
     * @param fetchParameters Parameters which will be used to retrieve blog entries
     * @return Blog entries retrieved for the particular request
     * @throws BlojsomFetcherException If there is an error retrieving the blog entries for the request
     */
    public BlogEntry[] fetchEntries(Map fetchParameters) throws BlojsomFetcherException;

    /**
     * Fetch a set of {@link BlogCategory} objects
     *
     * @param httpServletRequest Request
     * @param httpServletResponse Response
     * @param flavor Flavor
     * @param context Context
     * @return Blog categories retrieved for the particular request
     * @throws BlojsomFetcherException If there is an error retrieving the blog categories for the request
     */
    public BlogCategory[] fetchCategories(HttpServletRequest httpServletRequest,
                                          HttpServletResponse httpServletResponse,
                                          String flavor,
                                          Map context) throws BlojsomFetcherException;

    /**
     * Fetch a set of {@link BlogCategory} objects. This method is intended to be used for other
     * components such as the XML-RPC handlers that cannot generate servlet request and
     * response objects, but still need to be able to fetch categories. Implementations of this
     * method <b>must</b> be explicit about the exact parameter names and types that are
     * expected to return an appropriate set of {@link BlogCategory} objects.
     *
     * @param fetchParameters Parameters which will be used to retrieve blog entries
     * @return Blog categories retrieved for the particular request
     * @throws BlojsomFetcherException If there is an error retrieving the blog categories for the request
     */
    public BlogCategory[] fetchCategories(Map fetchParameters) throws BlojsomFetcherException;

    /**
     * Called when {@link org.ignition.blojsom.servlet.BlojsomServlet} is taken out of service
     *
     * @throws BlojsomFetcherException If there is an error in finalizing this fetcher
     */
    public void destroy() throws BlojsomFetcherException;
}
