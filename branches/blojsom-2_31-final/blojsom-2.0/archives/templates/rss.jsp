<?xml version="1.0" encoding="UTF-8"?>
<%@ page contentType="text/xml; charset=UTF-8"
         import="org.blojsom.blog.Blog,
                 org.blojsom.util.BlojsomConstants,
                 org.blojsom.blog.BlogEntry,
                 org.blojsom.util.BlojsomUtils"
         session="false"%>
<!-- name="generator" content="<%= request.getAttribute(BlojsomConstants.BLOJSOM_VERSION) %>" -->
<%
    Blog blogInformation = (Blog) request.getAttribute(BlojsomConstants.BLOJSOM_BLOG);
    BlogEntry[] blogEntries = (BlogEntry[]) request.getAttribute(BlojsomConstants.BLOJSOM_ENTRIES);
    boolean blogCommentsEnabled = true;
    if (request.getAttribute(BlojsomConstants.BLOJSOM_COMMENTS_ENABLED) != null) {
        blogCommentsEnabled = ((Boolean) request.getAttribute(BlojsomConstants.BLOJSOM_COMMENTS_ENABLED)).booleanValue();
    }    
%>
<rss version="0.92">
  <channel>
    <title><%= blogInformation.getBlogName() %></title>
    <link><%= blogInformation.getBlogURL() %></link>
    <description><%= blogInformation.getBlogDescription() %></description>
    <language><%= blogInformation.getBlogLanguage() %></language>
    <%
        if (blogEntries != null) {
            for (int i = 0; i < blogEntries.length; i++) {
                BlogEntry blogEntry = blogEntries[i];
    %>
    	<item>
    		<title><%= blogEntry.getEscapedTitle() %></title>
    		<link><%= blogEntry.getEscapedLink() %></link>
    		<description><%= blogEntry.getEscapedDescription() %></description>
    	</item>
    <%
            }
        }
    %>
   </channel>
</rss>