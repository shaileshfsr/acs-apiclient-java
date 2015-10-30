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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foresee.interfaces.logging.LoggerAbstraction;

import java.io.ByteArrayOutputStream;

/**
 * Created by Bradley.Bax on 10/14/2015.
 */
public class AcsApiDateRange
{
    private static LoggerAbstraction _logger;

    // bbax: this is used by the json engine during deserialization to string... seems like a strange requirement
    // to map from object -> string... beware the dangers of no logging
    public AcsApiDateRange(){

    }

    public AcsApiDateRange(LoggerAbstraction logger){
        _logger = logger;
    }

    public enum AcsApiCalendarType
    {
        G, GREGORIAN, FISCAL
    }

    public enum AcsApiRange
    {
        DAY, WEEK, MONTH, QUARTER, YEAR
    }

    /// <summary>
    /// Type of date range sequence.
    /// </summary>
    public enum AcsApiRangeSequence
    {
        DAYS, WEEKS, MONTHS, QUARTERS, YEARS, CUSTOM
    }

    /// <summary>
    /// Modifier for the date range period.
    /// </summary>
    public enum AcsApiPeriodModifier
    {
        DEFINED, PRIOR, CURRENT, NEXT, PRIOR_YEAR
    }

    @JsonProperty(value = "a")
    public String AsOfDate;

    @JsonProperty(value = "c")
    public String CalendarType;

    @JsonProperty(value = "r")
    public String Range;

    @JsonProperty(value = "k")
    public String CustomerKey;

    @JsonProperty(value = "n")
    public int RangeNumber;

    @JsonProperty(value = "p")
    public String PeriodModifier;

    @JsonProperty(value = "f")
    public String FromDate;

    @JsonProperty(value = "l")
    public String ToDate;

    public String ToAcsApiJson()
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try{
            mapper.writeValue(baos, this);
        }catch(Exception exc){
            _logger.error("Failed to serialize object to json", exc);
            return null;
        }
        return baos.toString();
    }
}
