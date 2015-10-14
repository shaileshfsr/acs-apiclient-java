package com.foresee.date;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;

/**
 * Created by Bradley.Bax on 10/14/2015.
 */
public class AcsApiDateRange
{
    final static Logger _logger = LogManager.getLogger(AcsApiDateRange.class);
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
