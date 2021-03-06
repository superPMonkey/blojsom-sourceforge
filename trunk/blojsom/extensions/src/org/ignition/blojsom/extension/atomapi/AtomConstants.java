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
package org.ignition.blojsom.extension.atomapi;

/**
 * AtomConstants
 *
 * @author Mark Lussier
 * @since blojsom 1.9.5
 * @version $Id: AtomConstants.java,v 1.3 2003-08-09 16:55:24 intabulas Exp $
 */
public interface AtomConstants {

    /**
     * Author Element Tag Name
     */
    static final String ELEMENT_AUTHOR = "author";

    /**
     * Name Element Tag Name
     */
    static final String ELEMENT_NAME = "name";

    /**
     * Homepage  Element Tag Name
     */
    static final String ELEMENT_HOMEPAGE = "homepage";

    /**
     * WebLog Element Tag Name
     */
    static final String ELEMENT_WEBLOG = "weblog";

    /**
     * Title Element Tag Name
     */
    static final String ELEMENT_TITLE = "title";

    /**
     * SubTitle Element Tag Name
     */
    static final String ELEMENT_SUBTITLE = "subtitle";

    /**
     * Summary Element Tag Name
     */
    static final String ELEMENT_SUMMARY = "summary";

    /**
     * Issued Element Tag Name
     */
     static final String ELEMENT_ISSUED = "issued";

    /**
     * Created Element Tag Name
     */
    static final String ELEMENT_CREATED = "created";

    /**
     * Modified Element Tag Name
     */
    static final String ELEMENT_MODIFIED = "modified";

    /**
     * Link Element Tag Name
     */
    static final String ELEMENT_LINK = "link";

    /**
     * Id Element Tag Name
     */
    static final String ELEMENT_ID = "id";

    /**
     * Content Element Tag Name
     */
    static final String ELEMENT_CONTENT = "content";

    /**
     * Atom Date Format
     */
    static final String ATOM_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    /**
     * Header Value prefix for Basic Relm Auth.. NOTE: THE TRAILING SLASH IS INTENDED!
     */
    static final String BASE64_AUTH_PREFIX = "Basic ";

    /**
     * Authentication Relm
     */
    static final String AUTHENTICATION_RELM = "basic realm=\"blojsom\"";


    /**
     * Resonse Header for Authentication Challenge
     */
    static final String HEADER_AUTHCHALLENGE = "WWW-Authenticate";

    /**
     * Resonse Header for Path
     */
    static final String HEADER_LOCATION = "Location";

    /**
     * Inbound Request Header with Authentication Credentials
     */
    static final String HEADER_AUTHORIZATION = "Authorization";


}
