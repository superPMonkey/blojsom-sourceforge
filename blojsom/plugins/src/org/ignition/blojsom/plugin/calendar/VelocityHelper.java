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
package org.ignition.blojsom.plugin.calendar;

import org.ignition.blojsom.util.BlojsomUtils;

import java.util.Calendar;


/**
 * VelocityHelper
 *
 * @author Mark Lussier
 * @version $Id: VelocityHelper.java,v 1.3 2003-03-28 01:07:03 czarneckid Exp $
 */
public class VelocityHelper {

    private BlogCalendar _calendar;

    // [Row][Col]
    private String[][] visualcalendar = new String[6][7];

    /**
     *
     */
    public VelocityHelper() {
    }

    /**
     *
     * @param calendar
     */
    public VelocityHelper(BlogCalendar calendar) {
        _calendar = calendar;
    }

    /**
     *
     * @param calendar
     */
    public void setCalendar(BlogCalendar calendar) {
        _calendar = calendar;
    }

    /**
     *
     */
    public void buildCalendar() {
        int fdow = _calendar.getFirstDayOfMonth() - 1;
        int ldom = _calendar.getDaysInMonth();
        int dowoffset = 0;
        for (int x = 0; x < 6; x++) {
            for (int y = 0; y < 7; y++) {
                if ((x == 0 && y < fdow) || (dowoffset >= ldom)) {
                    visualcalendar[x][y] = "&nbsp;";
                } else {
                    dowoffset += 1;
                    if (!_calendar.dayHasEntry(dowoffset)) {
                        visualcalendar[x][y] = new Integer(dowoffset).toString();
                    } else {
                        StringBuffer _url = new StringBuffer("<a href=\"");
                        String _calurl = BlojsomUtils.getCalendarNavigationUrl(_calendar.getCalendarUrl(), _calendar.getCurrentMonth(), dowoffset, _calendar.getCurrentYear());
                        _url.append(_calurl);
                        _url.append("\">").append(dowoffset).append("</a>");
                        visualcalendar[x][y] = _url.toString();
                    }
                }
            }
        }
    }

    /**
     *
     * @param row
     * @param clazz
     * @return
     */
    public String getCalendarRow(int row, String clazz) {
        StringBuffer result = new StringBuffer();
        for (int x = 0; x < 7; x++) {
            result.append("<td class=\"").append(clazz).append("\">").append(visualcalendar[row - 1][x]).append("</td>");
        }
        return result.toString();
    }

    /**
     *
     * @return
     */
    public String getToday() {
        StringBuffer result = new StringBuffer();
        result.append("<a href=\"").append(_calendar.getCalendarUrl()).append("\">Today</a>");
        return result.toString();
    }

    /**
     *
     * @return
     */
    public String getPreviousMonth() {
        StringBuffer result = new StringBuffer();
        _calendar.getCalendar().add(Calendar.MONTH, -1);
        result.append("<a href=\"");
        String prevurl = BlojsomUtils.getCalendarNavigationUrl(_calendar.getCalendarUrl(),
                (_calendar.getCalendar().get(Calendar.MONTH) + 1),
                -1, _calendar.getCalendar().get(Calendar.YEAR));
        result.append(prevurl);
        result.append("\"> &lt;&nbsp;&nbsp;");
        result.append(_calendar.getShortMonthName(_calendar.getCalendar().get(Calendar.MONTH)));
        result.append("</a>");
        _calendar.getCalendar().add(Calendar.MONTH, 1);
        return result.toString();
    }

    /**
     *
     * @return
     */
    public String getNextMonth() {
        StringBuffer result = new StringBuffer();
        _calendar.getCalendar().add(Calendar.MONTH, 1);

        if ((_calendar.getCalendar().get(Calendar.MONTH) < (_calendar.getToday().get(Calendar.MONTH) + 1)) &&
                (_calendar.getCalendar().get(Calendar.YEAR) <= _calendar.getToday().get(Calendar.YEAR))) {
            result.append("<a href=\"");
            String nexturl = BlojsomUtils.getCalendarNavigationUrl(_calendar.getCalendarUrl(),
                    (_calendar.getCalendar().get(Calendar.MONTH) + 1),
                    -1, _calendar.getCalendar().get(Calendar.YEAR));

            result.append(nexturl);
            result.append("\">");
            result.append(_calendar.getShortMonthName(_calendar.getCalendar().get(Calendar.MONTH)));
            result.append("&nbsp;&nbsp;&gt; </a>");
            _calendar.getCalendar().add(Calendar.MONTH, -1);
        } else {
            result.append(_calendar.getShortMonthName(_calendar.getCalendar().get(Calendar.MONTH))).append("&nbsp;&nbsp;>");
        }
        return result.toString();
    }
}
