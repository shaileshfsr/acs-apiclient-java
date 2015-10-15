/*
The MIT License (MIT)

Copyright (c) 2015 Answers Cloud Services

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */

package com.foresee.date;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Bradley.Bax on 10/14/2015.
 */
public class AcsApiDateRangeBuilder
{
    public final AcsApiDateRange DateRange;
    private static final String DateFormat = "yyyy-MM-dd";

    public AcsApiDateRangeBuilder(){
        this(null, AcsApiDateRange.AcsApiCalendarType.G);
    }

    public AcsApiDateRangeBuilder(Date relativeDate){
        this(relativeDate, AcsApiDateRange.AcsApiCalendarType.G);
    }

    public AcsApiDateRangeBuilder(Date relativeDate, AcsApiDateRange.AcsApiCalendarType calendar)
    {
        this.DateRange = new AcsApiDateRange();

        if (relativeDate != null) {
            this.DateRange.AsOfDate = FormattedDate(relativeDate);
        }

        this.DateRange.CalendarType = calendar.toString();
    }

    private String FormattedDate(Date target){
        SimpleDateFormat df = new SimpleDateFormat(DateFormat);
        return df.format(target);
    }

    public void SetCustomDateRange(Date start, Date end)
    {
        this.DateRange.Range = AcsApiDateRange.AcsApiRangeSequence.CUSTOM.toString();
        this.DateRange.FromDate = FormattedDate(start);
        this.DateRange.ToDate = FormattedDate(end);
    }

    public void SetAbsoluteDateRange(AcsApiDateRange.AcsApiRange rangeType){
        SetAbsoluteDateRange(rangeType, 0, AcsApiDateRange.AcsApiPeriodModifier.DEFINED);
    }

    public void SetAbsoluteDateRange(AcsApiDateRange.AcsApiRange rangeType, int value){
        SetAbsoluteDateRange(rangeType, value, AcsApiDateRange.AcsApiPeriodModifier.DEFINED);
    }

    public void SetAbsoluteDateRange(AcsApiDateRange.AcsApiRange rangeType, int value, AcsApiDateRange.AcsApiPeriodModifier modifier)
    {
        this.DateRange.Range = rangeType.toString();
        this.DateRange.RangeNumber = value;
        this.DateRange.PeriodModifier = modifier.toString();
    }

    public void SetAbsoluteDateRangeSequence(int value, AcsApiDateRange.AcsApiRangeSequence rangeType){
        SetAbsoluteDateRangeSequence(value, rangeType, AcsApiDateRange.AcsApiPeriodModifier.DEFINED);
    }

    public void SetAbsoluteDateRangeSequence(int value,  AcsApiDateRange.AcsApiRangeSequence rangeType, AcsApiDateRange.AcsApiPeriodModifier modifier)
    {
        this.DateRange.Range = rangeType.toString();
        this.DateRange.RangeNumber = value;
        this.DateRange.PeriodModifier = modifier.toString();
    }

    public void UseCustomCalendar(String calendarKey)
    {
        this.DateRange.CustomerKey = calendarKey;
    }
}