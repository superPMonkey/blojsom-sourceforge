<?xml version="1.0"?>
<%@ page import="org.ignition.blojsom.blog.Blog,
                 org.ignition.blojsom.util.BlojsomConstants,
                 org.ignition.blojsom.blog.BlogEntry"
                 session="false"%>
<%
    Blog blogInformation = (Blog) request.getAttribute(BlojsomConstants.BLOJSOM_BLOG);
    BlogEntry[] blogEntries = (BlogEntry[]) request.getAttribute(BlojsomConstants.BLOJSOM_ENTRIES);
%>
<rss version="2.0" xmlns="http://backend.userland.com/rss2"
                   xmlns:dc="http://purl.org/dc/elements/1.1/">
  <channel>
    <title><%= blogInformation.getBlogName() %></title>
    <link><%= blogInformation.getBlogURL() %></link>
    <description><%= blogInformation.getBlogDescription() %></description>
    <language><%= blogInformation.getBlogLanguage() %></language>
    <docs>http://backend.userland.com/rss</docs>
    <generator>blojsom</generator>
	<dc:publisher><%= blogInformation.getBlogOwner()%></dc:publisher>
	<dc:creator><%= blogInformation.getBlogOwnerEmail()%></dc:creator>
    <%
        if (blogEntries != null) {
            for (int i = 0; i < blogEntries.length; i++) {
                BlogEntry blogEntry = blogEntries[i];
    %>
    	<item>
    		<title><%= blogEntry.getEscapedTitle() %></title>
    		<link><%= blogEntry.getEscapedLink() %></link>
    		<description><%= blogEntry.getEscapedDescription() %></description>
            <guid isPermaLink="true"><%= blogEntry.getEscapedLink() %></guid>
			<pubDate><%= blogEntry.getRFC822Date() %></pubDate>
			<wfw:comment xmlns:wfw="http://wellformedweb.org/CommentAPI/">
                 <%= blogInformation.getBlogBaseURL()%>/commentapi/<%= blogEntry.getId()%>
            </wfw:comment>
    	</item>
    <%
            }
        }
    %>
   </channel>
</rss>