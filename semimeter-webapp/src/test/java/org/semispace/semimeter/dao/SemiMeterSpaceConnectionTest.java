package org.semispace.semimeter.dao;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.semispace.SemiSpaceInterface;
import org.semispace.semimeter.space.CounterHolder;
import org.semispace.semimeter.space.ZeroAbleBlankCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 *
 */
@ContextConfiguration(locations={"/context/semimeter-test-context.xml"})
public class SemiMeterSpaceConnectionTest extends AbstractJUnit4SpringContextTests {
    private static final Logger log = LoggerFactory.getLogger(SemiMeterSpaceConnectionTest.class);
    private static final long NUMBER_OF_TEST_ELEMENTS = 100;
    private SemiSpaceInterface space;

    @Autowired
    private SemiMeterDao semiMeterDao;

    private ZeroAbleBlankCounter counter = null;

    @Before
    public void fetchSpaceFromDao() {
        log.info("Before: Current state of semiMeterDao:\n"+semiMeterDao);
        space = semiMeterDao.retrieveSpace();
        if ( counter == null ) {
            counter = new ZeroAbleBlankCounter(space);
        }
    }
    @After
    public void status() {
        log.info("After: Current state of semiMeterDao:\n"+semiMeterDao);
    }

    @Test
    public void testInsertionBySpace() {
        int oldSize = semiMeterDao.size();
        counter.count("/junit/InsertionBySpace");
        counter.reset();
        awaitNoCounterHoldersInSpace();
        Assert.assertEquals("Expected size of database to increase after addition of a single element.", oldSize+1, semiMeterDao.size() );
    }

    @Test
    public void testMultipleInsertionBySpace() {
        long bench = System.currentTimeMillis();
        for ( int i=0 ; i < NUMBER_OF_TEST_ELEMENTS ; i++ ) {
            counter.count("/junit/InsertionBySpace/"+i);
            counter.reset();
        }
        log.info("After {} ms, {} elements has been put into space", System.currentTimeMillis() - bench, NUMBER_OF_TEST_ELEMENTS);
        bench = System.currentTimeMillis();
        awaitNoCounterHoldersInSpace();
        log.info("After {} _more_ ms all elements are found to be put into database. (This number is misleading if the number of elements are few.)", System.currentTimeMillis() - bench);
    }

    @Test
    public void testMultipleInsertionBySpaceWithEqualElements() {
        long bench = System.currentTimeMillis();
        for ( int i=0 ; i < NUMBER_OF_TEST_ELEMENTS ; i++ ) {
            counter.count("/junit/InsertionBySpace/count/up");
            counter.reset();
        }
        log.info("After {} ms, {} collapsible elements has been put into space", System.currentTimeMillis() - bench, NUMBER_OF_TEST_ELEMENTS);
        bench = System.currentTimeMillis();
        awaitNoCounterHoldersInSpace();
        log.info("After {} _more_ ms all collapsible elements are found to be put into database. (This number is misleading if the number of elements are few.)", System.currentTimeMillis() - bench);
    }

    private void awaitNoCounterHoldersInSpace() {
        int c = 0;
        int numberOfFails=0;
        do {
            c++;
            if ( c > 100 ) {
                c=0;
                numberOfFails++;
                log.info("Waiting for space to be empty when querying for counterHolder...");
            }
            if ( numberOfFails > 3 ) {
                CounterHolder ch = space.takeIfExists( new CounterHolder());
                log.info("After taking "+(ch==null?"no":"one")+" element, there is "+(
                        ( space.readIfExists( new CounterHolder()) == null )?"no":"more")+
                        " elements left. semiMeterDao is now: \n"+semiMeterDao);
                Assert.fail("Serious problem with space. Had counter holder which did not get consumed. Taking it now: "+ ch );
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}
        } while ( space.readIfExists( new CounterHolder()) != null );
    }
}
