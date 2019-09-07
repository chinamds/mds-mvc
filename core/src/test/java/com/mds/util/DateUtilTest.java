package com.mds.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.*;

public class DateUtilTest {
    //~ Instance fields ========================================================

    private final Logger log = LoggerFactory.getLogger(DateUtilTest.class);

    @Test
    public void testGetInternationalDatePattern() {
        LocaleContextHolder.setLocale(new Locale("nl")); //nl
        assertEquals("dd-MMM-yyyy", DateUtils.getDatePattern());

        LocaleContextHolder.setLocale(Locale.FRANCE); 
        assertEquals("dd/MM/yyyy", DateUtils.getDatePattern());

        LocaleContextHolder.setLocale(Locale.GERMANY);
        assertEquals("dd.MM.yyyy", DateUtils.getDatePattern());
        
        // non-existant bundle should default to default locale
        LocaleContextHolder.setLocale(new Locale("fi"));
        String fiPattern = DateUtils.getDatePattern();
        LocaleContextHolder.setLocale(Locale.getDefault());
        String defaultPattern = DateUtils.getDatePattern();

        assertEquals(defaultPattern, fiPattern);
    }

    @Test
    public void testGetDate() throws Exception {
        if (log.isDebugEnabled()) {
            log.debug("db date to convert: " + new Date());
        }

        String date = DateUtils.convertFrom(new Date());

        if (log.isDebugEnabled()) {
            log.debug("converted ui date: " + date);
        }

        assertTrue(date != null);
    }

    @Test
    public void testGetDateTime() {
        if (log.isDebugEnabled()) {
            log.debug("entered 'testGetDateTime' method");
        }
        String now = DateUtils.getTimeNow(new Date());
        assertTrue(now != null);
        log.debug(now);
    }

    @Test
    public void testGetDateWithNull() {
        final String date = DateUtils.convertFrom(null);
        assertEquals("", date);
    }

    @Test
    public void testGetDateTimeWithNull() {
        final String date = DateUtils.getDateTime(null, null);
        assertEquals("", date);
    }

    @Test
    public void testGetToday() throws ParseException {
        assertNotNull(DateUtils.getToday());
    }
}
