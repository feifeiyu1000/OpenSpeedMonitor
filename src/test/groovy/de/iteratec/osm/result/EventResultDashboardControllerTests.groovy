/* 
* OpenSpeedMonitor (OSM)
* Copyright 2014 iteratec GmbH
* 
* Licensed under the Apache License, Version 2.0 (the "License"); 
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
* 	http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software 
* distributed under the License is distributed on an "AS IS" BASIS, 
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
* See the License for the specific language governing permissions and 
* limitations under the License.
*/

package de.iteratec.osm.result

import de.iteratec.osm.csi.Page
import de.iteratec.osm.measurement.environment.Browser
import de.iteratec.osm.measurement.environment.Location
import de.iteratec.osm.measurement.schedule.ConnectivityProfile
import de.iteratec.osm.measurement.schedule.JobGroup
import de.iteratec.osm.measurement.schedule.dao.JobGroupDaoService
import de.iteratec.osm.report.chart.AggregatorType
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.joda.time.DateTime
import org.joda.time.Interval
import spock.lang.Specification

import static org.junit.Assert.*

/**
 * <p>
 * Test-suite of {@link EventResultDashboardController} and 
 * {@link EventResultDashboardShowAllCommand}.
 * </p> 
 *
 * @author rhe
 * @since IT-98
 */
@TestFor(EventResultDashboardController)
@Mock([ConnectivityProfile])
class EventResultDashboardControllerTests extends Specification {

    EventResultDashboardController controllerUnderTest
    static EventResultDashboardShowAllCommand command

    void setup() {
        controllerUnderTest = controller;

        // Mock relevant services:
        command = new EventResultDashboardShowAllCommand()
        controllerUnderTest.jobGroupDaoService = Stub(JobGroupDaoService);
        controllerUnderTest.eventResultDashboardService = Stub(EventResultDashboardService);
    }

    /**
     * Test for inner class {@link CsiDashboardController.ShowAllCommand}.
     */
    void testShowAllCommand_EmptyCreationIsInvalid() {
        expect:
        assertFalse(command.validate())
        assertNotNull("Collections are never null", command.selectedFolder)
        assertNotNull("Collections are never null", command.selectedPages)
        assertNotNull("Collections are never null", command.selectedMeasuredEventIds)
        assertNotNull("Collections are never null", command.selectedBrowsers)
        assertNotNull("Collections are never null", command.selectedLocations)
    }

    /**
     * Test for inner class {@link EventResultDashboardShowAllCommand}.
     */
    void testShowAllCommand_BindFromEmptyRequestArgsIsInvalid() {
        when:
        controllerUnderTest.bindData(command, params)

        then:
        assertFalse(command.validate())
        assertNotNull("Collections are never null", command.selectedFolder)
        assertNotNull("Collections are never null", command.selectedPages)
        assertNotNull("Collections are never null", command.selectedMeasuredEventIds)
        assertNotNull("Collections are never null", command.selectedBrowsers)
        assertNotNull("Collections are never null", command.selectedLocations)
    }

    /**
     * Test for inner class {@link EventResultDashboardShowAllCommand}.
     */
    void testShowAllCommand_BindFromValidRequestArgsIsValid_ValuesNearlyDefaults() {
        given:
        // Fill-in request args:
        params.from = '18.08.2013'
        Date expectedDateForFrom = new Date(1376776800000L)

        params.fromHour = '12:00'

        params.to = '19.08.2013'
        Date expectedDateForTo = new Date(1376863200000L)

        params.toHour = '13:00'
        params.aggrGroup = AggregatorType.RESULT_CACHED_DOC_COMPLETE_INCOMING_BYTES.toString()
        params.selectedFolder = '1'
        params.selectedPages = ['1', '5']
        params.selectedMeasuredEventIds = ['7', '8', '9']
        params.selectedBrowsers = '2'
        params.selectedLocations = '17'
        params._action_showAll = 'Anzeigen'
        params.selectedAllBrowsers = false
        params.selectedAllLocations = false
        params.selectedAllMeasuredEvents = false
        params.selectedAggrGroupValuesCached = [AggregatorType.RESULT_CACHED_LOAD_TIME]
        params.selectedTimeFrameInterval = 0
        params.includeNativeConnectivity = false
        params.includeCustomConnectivity = true
        params.showDataMarkers = false

        params.showDataLabels = false
        params.selectedInterval = 0
        params.selectChartType = 0
        params.trimBelowLoadTimes = 0
        params.trimAboveLoadTimes = 0
        params.trimBelowRequestCounts = 0
        params.trimAboveRequestCounts = 0
        params.trimBelowRequestSizes = 0
        params.trimAboveRequestSizes = 0
        params.debug = false
        params.setFromHour = false
        params.setToHour = false
        params.chartWidth = 0
        params.chartHeight = 0
        params.loadTimeMinimum = 0

        when:
        // Create and fill the command:
        controllerUnderTest.bindData(command, params)

        then:
        // Verification:
        assertTrue(command.validate())
        assertNotNull("Collections are never null", command.selectedFolder)
        assertNotNull("Collections are never null", command.selectedPages)
        assertNotNull("Collections are never null", command.selectedMeasuredEventIds)
        assertNotNull("Collections are never null", command.selectedBrowsers)
        assertNotNull("Collections are never null", command.selectedLocations)

        assertEquals(expectedDateForFrom, command.from);
        assertEquals("12:00", command.fromHour);
        assertEquals("13:00", command.toHour);
        assertEquals(AggregatorType.RESULT_CACHED_DOC_COMPLETE_INCOMING_BYTES, command.aggrGroup);

        assertEquals(1, command.selectedFolder.size())
        assertTrue(command.selectedFolder.contains(1L))

        assertEquals(2, command.selectedPages.size())
        assertTrue(command.selectedPages.contains(1L))
        assertTrue(command.selectedPages.contains(5L))

        assertFalse(command.selectedAllMeasuredEvents as boolean)
        assertEquals(3, command.selectedMeasuredEventIds.size())
        assertTrue(command.selectedMeasuredEventIds.contains(7L))
        assertTrue(command.selectedMeasuredEventIds.contains(8L))
        assertTrue(command.selectedMeasuredEventIds.contains(9L))

        assertFalse(command.selectedAllBrowsers as boolean)
        assertEquals(1, command.selectedBrowsers.size())
        assertTrue(command.selectedBrowsers.contains(2L))

        assertFalse(command.selectedAllLocations as boolean)
        assertEquals(1, command.selectedLocations.size())
        assertTrue(command.selectedLocations.contains(17L))

        // Could we assume the time frame at once?
        Interval timeFrame = command.selectedTimeFrame;

        DateTime start = timeFrame.getStart();
        DateTime end = timeFrame.getEnd();

        assertEquals(2013, start.getYear())
        assertEquals(8, start.getMonthOfYear())
        assertEquals(18, start.getDayOfMonth())
        assertEquals(12, start.getHourOfDay())
        assertEquals(0, start.getMinuteOfHour())
        assertEquals(0, start.getSecondOfMinute())
        assertEquals(0, start.getMillisOfSecond())

        assertEquals(2013, end.getYear())
        assertEquals(8, end.getMonthOfYear())
        assertEquals(19, end.getDayOfMonth())
        assertEquals(13, end.getHourOfDay())
        assertEquals(0, end.getMinuteOfHour())
        assertEquals(59, end.getSecondOfMinute())
        assertEquals(999, end.getMillisOfSecond())
    }

    /**
     * Test for inner class {@link EventResultDashboardShowAllCommand}.
     */
    void testShowAllCommand_BindFromValidRequestArgsIsValid_ToDateBeforeFromDate() {
        given:
        // Fill-in request args:
        params.from = '18.08.2013'
        params.fromHour = '12:00'

        params.to = '17.08.2013'
        params.toHour = '13:00'
        params.aggrGroup = AggregatorType.RESULT_CACHED_DOC_COMPLETE_INCOMING_BYTES.toString()
        params.selectedFolder = '1'
        params.selectedPages = ['1', '5']
        params.selectedMeasuredEventIds = ['7', '8', '9']
        params.selectedBrowsers = '2'
        params.selectedLocations = '17'
        params._action_showAll = 'Anzeigen'
        params.showDataMarkers = false

        params.showDataLabels = false
        params.selectedInterval = 0
        params.selectChartType = 0
        params.trimBelowLoadTimes = 0
        params.trimAboveLoadTimes = 0
        params.trimBelowRequestCounts = 0
        params.trimAboveRequestCounts = 0
        params.trimBelowRequestSizes = 0
        params.trimAboveRequestSizes = 0
        params.debug = false
        params.setFromHour = false
        params.setToHour = false
        params.chartWidth = 0
        params.chartHeight = 0
        params.loadTimeMinimum = 0
        when:
        // Create and fill the command:
        controllerUnderTest.bindData(command, params)

        then:
        // Verification:
        assertFalse(command.validate())
    }

    /**
     * Test for inner class {@link EventResultDashboardShowAllCommand}.
     */
    void testShowAllCommand_BindFromValidRequestArgsIsValid_EqualDateToHourBeforeFromHour() {
        given:
        // Fill-in request args:
        params.from = '18.08.2013'
        params.fromHour = '12:00'

        params.to = '18.08.2013'
        params.toHour = '11:00'
        params.aggrGroup = AggregatorType.RESULT_CACHED_DOC_COMPLETE_INCOMING_BYTES.toString()
        params.selectedFolder = '1'
        params.selectedPages = ['1', '5']
        params.selectedMeasuredEventIds = ['7', '8', '9']
        params.selectedBrowsers = '2'
        params.selectedLocations = '17'
        params._action_showAll = 'Anzeigen'

        params.showDataMarkers = false
        params.showDataLabels = false
        params.selectedInterval = 0
        params.selectChartType = 0
        params.trimBelowLoadTimes = 0
        params.trimAboveLoadTimes = 0
        params.trimBelowRequestCounts = 0
        params.trimAboveRequestCounts = 0
        params.trimBelowRequestSizes = 0
        params.trimAboveRequestSizes = 0
        params.debug = false
        params.setFromHour = false
        params.setToHour = false
        params.chartWidth = 0
        params.chartHeight = 0
        params.loadTimeMinimum = 0
        when:
        // Create and fill the command:
        controllerUnderTest.bindData(command, params)
        then:
        // Verification:
        assertFalse(command.validate())
    }

    /**
     * Test for inner class {@link EventResultDashboardShowAllCommand}.
     */
    void testShowAllCommand_BindFromValidRequestArgsIsValid_EqualDateEqualHourToMinuteBeforeFromMinute() {
        given:
        // Fill-in request args:
        params.from = '18.08.2013'
        params.fromHour = '12:00'

        params.to = '18.08.2013'
        params.toHour = '12:00'
        params.aggrGroup = AggregatorType.RESULT_CACHED_DOC_COMPLETE_INCOMING_BYTES.toString()
        params.selectedFolder = '1'
        params.selectedPages = ['1', '5']
        params.selectedMeasuredEventIds = ['7', '8', '9']
        params.selectedBrowsers = '2'
        params.selectedLocations = '17'
        params._action_showAll = 'Anzeigen'
        params.showDataMarkers = false

        params.showDataLabels = false
        params.selectedInterval = 0
        params.selectChartType = 0
        params.trimBelowLoadTimes = 0
        params.trimAboveLoadTimes = 0
        params.trimBelowRequestCounts = 0
        params.trimAboveRequestCounts = 0
        params.trimBelowRequestSizes = 0
        params.trimAboveRequestSizes = 0
        params.debug = false
        params.setFromHour = false
        params.setToHour = false
        params.chartWidth = 0
        params.chartHeight = 0
        params.loadTimeMinimum = 0
        when:
        // Create and fill the command:
        controllerUnderTest.bindData(command, params)
        then:
        // Verification:
        assertFalse(command.validate())
    }

    /**
     * Test for inner class {@link EventResultDashboardShowAllCommand}.
     */
    void testShowAllCommand_BindFromValidRequestArgsIsValid_ValuesDifferingFromDefaults() {
        given:
        // Fill-in request args:
        params.from = '18.08.2013'
        Date expectedDateForFrom = new Date(1376776800000L)

        params.fromHour = '16:00'
        params.to = '18.08.2013'
        Date expectedDateForTo = new Date(1376776800000L)

        params.toHour = '18:00'
        params.aggrGroup = AggregatorType.PAGE.toString()
        params.selectedFolder = '1'
        params.selectedPages = ['1', '5']
        params.selectedPages = ['1', '5']
        params.selectedMeasuredEventIds = ['7', '8', '9']
        params.selectedBrowsers = '2'
        params.selectedLocations = '17'
        params._action_showAll = 'Anzeigen'
        params.selectedAllBrowsers = false
        params.selectedAllLocations = false
        params.selectedAllMeasuredEvents = false
        params.selectedAggrGroupValuesCached = [AggregatorType.RESULT_CACHED_LOAD_TIME]
        params.selectedTimeFrameInterval = 0
        params.includeNativeConnectivity = false
        params.includeCustomConnectivity = true
        params.showDataMarkers = false
        params.showDataLabels = false
        params.selectedInterval = 0
        params.selectChartType = 0
        params.trimBelowLoadTimes = 0
        params.trimAboveLoadTimes = 0
        params.trimBelowRequestCounts = 0
        params.trimAboveRequestCounts = 0
        params.trimBelowRequestSizes = 0
        params.trimAboveRequestSizes = 0
        params.debug = false
        params.setFromHour = false
        params.setToHour = false
        params.chartWidth = 0
        params.chartHeight = 0
        params.loadTimeMinimum = 0
        // Create and fill the command:
        when:
        controllerUnderTest.bindData(command, params)
        then:
        // Verification:
        assertTrue(command.validate())
        assertNotNull("Collections are never null", command.selectedFolder)
        assertNotNull("Collections are never null", command.selectedPages)
        assertNotNull("Collections are never null", command.selectedMeasuredEventIds)
        assertNotNull("Collections are never null", command.selectedBrowsers)
        assertNotNull("Collections are never null", command.selectedLocations)

        assertEquals(expectedDateForFrom, command.from);
        assertEquals("16:00", command.fromHour);
        assertEquals("18:00", command.toHour);
        assertEquals(AggregatorType.PAGE, command.aggrGroup);

        assertEquals(1, command.selectedFolder.size())
        assertTrue(command.selectedFolder.contains(1L))

        assertEquals(2, command.selectedPages.size())
        assertTrue(command.selectedPages.contains(1L))
        assertTrue(command.selectedPages.contains(5L))

        assertFalse(command.selectedAllMeasuredEvents as boolean)
        assertEquals(3, command.selectedMeasuredEventIds.size())
        assertTrue(command.selectedMeasuredEventIds.contains(7L))
        assertTrue(command.selectedMeasuredEventIds.contains(8L))
        assertTrue(command.selectedMeasuredEventIds.contains(9L))

        assertFalse(command.selectedAllBrowsers as boolean)
        assertEquals(1, command.selectedBrowsers.size())
        assertTrue(command.selectedBrowsers.contains(2L))

        assertFalse(command.selectedAllLocations as boolean)
        assertEquals(1, command.selectedLocations.size())
        assertTrue(command.selectedLocations.contains(17L))

        // Could we assume the time frame at once?
        Interval timeFrame = command.selectedTimeFrame;

        DateTime start = timeFrame.getStart();
        DateTime end = timeFrame.getEnd();

        assertEquals(2013, start.getYear())
        assertEquals(8, start.getMonthOfYear())
        assertEquals(18, start.getDayOfMonth())
        assertEquals(16, start.getHourOfDay())
        assertEquals(0, start.getMinuteOfHour())
        assertEquals(0, start.getSecondOfMinute())
        assertEquals(0, start.getMillisOfSecond())

        assertEquals(2013, end.getYear())
        assertEquals(8, end.getMonthOfYear())
        assertEquals(18, end.getDayOfMonth())
        assertEquals(18, end.getHourOfDay())
        assertEquals(0, end.getMinuteOfHour())
        assertEquals(59, end.getSecondOfMinute())
        assertEquals(999, end.getMillisOfSecond())
    }

    void testShowAllCommand_BindFromInvalidRequestArgsIsInvalid() {
        given:
        // Fill-in request args:
        params.from = '18.08.2013'
        params.fromHour = '12:00'
        params.to = '19.08.2013'
        params.toHour = '13:00'
        params.aggrGroup = AggregatorType.RESULT_CACHED_DOC_COMPLETE_INCOMING_BYTES.toString()
        params.selectedFolder = '1'
        params.selectedPages = ['NOT-A-NUMBER']
        params.selectedMeasuredEventIds = ['7', '8', '9']
        params.selectedBrowsers = '2'
        params.selectedLocations = 'UGLY'
        params._action_showAll = 'Anzeigen'
        params.showDataMarkers = false

        params.showDataLabels = false
        params.selectedInterval = 0
        params.selectChartType = 0
        params.trimBelowLoadTimes = 0
        params.trimAboveLoadTimes = 0
        params.trimBelowRequestCounts = 0
        params.trimAboveRequestCounts = 0
        params.trimBelowRequestSizes = 0
        params.trimAboveRequestSizes = 0
        params.debug = false
        params.setFromHour = false
        params.setToHour = false
        params.chartWidth = 0
        params.chartHeight = 0
        params.loadTimeMinimum = 0

        when:
        // Create and fill the command:
        controllerUnderTest.bindData(command, params)

        then:
        // Verification:
        assertFalse(command.validate())
        assertNotNull("Collections are never null", command.selectedFolder)
        assertNotNull("Collections are never null", command.selectedPages)
        assertNotNull("Collections are never null", command.selectedMeasuredEventIds)
        assertNotNull("Collections are never null", command.selectedBrowsers)
        assertNotNull("Collections are never null", command.selectedLocations)

        assertTrue("Invalid data -> no elements in Collection", command.selectedPages.isEmpty())
        assertTrue("Invalid data -> no elements in Collection", command.selectedLocations.isEmpty())
    }

    /**
     * Test for inner class {@link EventResultDashboardShowAllCommand}.
     */
    void testShowAllCommand_BindFromInvalidRequestArgsIsInvalid_selectedPage_isEmpty_for_RESULT_CACHED_DOC_COMPLETE_INCOMING_BYTES() {
        given:
        // Fill-in request args:
        params.from = '18.08.2013'
        params.fromHour = '16:00'
        params.to = '18.08.2013'
        params.toHour = '18:00'
        params.aggrGroup = AggregatorType.RESULT_CACHED_DOC_COMPLETE_INCOMING_BYTES.toString()
        params.selectedFolder = '1'
        params.selectedPages = []
        params.selectedMeasuredEventIds = ['7', '8', '9']
        params.selectedBrowsers = '2'
        params.selectedLocations = '17'
        params._action_showAll = 'Anzeigen'
        params.showDataMarkers = false

        params.showDataLabels = false
        params.selectedInterval = 0
        params.selectChartType = 0
        params.trimBelowLoadTimes = 0
        params.trimAboveLoadTimes = 0
        params.trimBelowRequestCounts = 0
        params.trimAboveRequestCounts = 0
        params.trimBelowRequestSizes = 0
        params.trimAboveRequestSizes = 0
        params.debug = false
        params.setFromHour = false
        params.setToHour = false
        params.chartWidth = 0
        params.chartHeight = 0
        params.loadTimeMinimum = 0

        when:
        // Create and fill the command:
        controllerUnderTest.bindData(command, params)

        then:
        // Verification:
        assertFalse(command.validate())
    }

    /**
     * Test for inner class {@link EventResultDashboardShowAllCommand}.
     */
    void testShowAllCommand_BindFromInvalidRequestArgsIsInvalid_selectedPage_isEmpty_for_WEEKLY_PAGE() {
        given:
        // Fill-in request args:
        params.from = '18.08.2013'
        params.fromHour = '16:00'
        params.to = '18.08.2013'
        params.toHour = '18:00'
        params.aggrGroup = AggregatorType.PAGE.toString()
        params.selectedFolder = '1'
        params.selectedPages = []
        params.selectedMeasuredEventIds = ['7', '8', '9']
        params.selectedBrowsers = '2'
        params.selectedLocations = '17'
        params._action_showAll = 'Anzeigen'
        params.showDataMarkers = false

        params.showDataLabels = false
        params.selectedInterval = 0
        params.selectChartType = 0
        params.trimBelowLoadTimes = 0
        params.trimAboveLoadTimes = 0
        params.trimBelowRequestCounts = 0
        params.trimAboveRequestCounts = 0
        params.trimBelowRequestSizes = 0
        params.trimAboveRequestSizes = 0
        params.debug = false
        params.setFromHour = false
        params.setToHour = false
        params.chartWidth = 0
        params.chartHeight = 0
        params.loadTimeMinimum = 0

        when:
        // Create and fill the command:
        controllerUnderTest.bindData(command, params)

        then:
        // Verification:
        assertFalse(command.validate())
    }

    /**
     * Test for inner class {@link EventResultDashboardShowAllCommand}.
     */
    void testShowAllCommand_BindFromInvalidRequestArgsIsInvalid_selectedMeasuredEvents_isEmpty_for_RESULT_CACHED_DOC_COMPLETE_INCOMING_BYTES() {
        given:
        // Fill-in request args:
        params.from = '18.08.2013'
        params.fromHour = '16:00'
        params.to = '18.08.2013'
        params.toHour = '18:00'
        params.aggrGroup = AggregatorType.RESULT_CACHED_DOC_COMPLETE_INCOMING_BYTES.toString()
        params.selectedFolder = '1'
        params.selectedPages = ['1', '5']
        params.selectedMeasuredEventIds = []
        params.selectedBrowsers = '2'
        params.selectedLocations = '17'
        params._action_showAll = 'Anzeigen'
        params.showDataMarkers = false

        params.showDataLabels = false
        params.selectedInterval = 0
        params.selectChartType = 0
        params.trimBelowLoadTimes = 0
        params.trimAboveLoadTimes = 0
        params.trimBelowRequestCounts = 0
        params.trimAboveRequestCounts = 0
        params.trimBelowRequestSizes = 0
        params.trimAboveRequestSizes = 0
        params.debug = false
        params.setFromHour = false
        params.setToHour = false
        params.chartWidth = 0
        params.chartHeight = 0
        params.loadTimeMinimum = 0

        when:
        // Create and fill the command:
        controllerUnderTest.bindData(command, params)

        then:
        // Verification:
        assertFalse(command.validate())
    }

    /**
     * Test for inner class {@link EventResultDashboardShowAllCommand}.
     */
    void testShowAllCommand_BindFromInvalidRequestArgsIsInvalid_selectedBrowsers_isEmpty_for_RESULT_CACHED_DOC_COMPLETE_INCOMING_BYTES() {
        given:
        // Fill-in request args:
        params.from = '18.08.2013'
        params.fromHour = '16:00'
        params.to = '18.08.2013'
        params.toHour = '18:00'
        params.aggrGroup = AggregatorType.RESULT_CACHED_DOC_COMPLETE_INCOMING_BYTES.toString()
        params.selectedFolder = '1'
        params.selectedPages = ['1', '5']
        params.selectedMeasuredEventIds = ['7', '8', '9']
        params.selectedBrowsers = []
        params.selectedLocations = '17'
        params._action_showAll = 'Anzeigen'
        params.showDataMarkers = false

        params.showDataLabels = false
        params.selectedInterval = 0
        params.selectChartType = 0
        params.trimBelowLoadTimes = 0
        params.trimAboveLoadTimes = 0
        params.trimBelowRequestCounts = 0
        params.trimAboveRequestCounts = 0
        params.trimBelowRequestSizes = 0
        params.trimAboveRequestSizes = 0
        params.debug = false
        params.setFromHour = false
        params.setToHour = false
        params.chartWidth = 0
        params.chartHeight = 0
        params.loadTimeMinimum = 0
        when:
        // Create and fill the command:
        controllerUnderTest.bindData(command, params)
        then:
        // Verification:
        assertFalse(command.validate())
    }

    /**
     * Test for inner class {@link EventResultDashboardShowAllCommand}.
     */
    void testShowAllCommand_BindFromInvalidRequestArgsIsInvalid_selectedLocations_isEmpty_for_RESULT_CACHED_DOC_COMPLETE_INCOMING_BYTES() {
        given:
        // Fill-in request args:
        params.from = '18.08.2013'
        params.fromHour = '16:00'
        params.to = '18.08.2013'
        params.toHour = '18:00'
        params.aggrGroup = AggregatorType.RESULT_CACHED_DOC_COMPLETE_INCOMING_BYTES.toString()
        params.selectedFolder = '1'
        params.selectedPages = ['1', '5']
        params.selectedMeasuredEventIds = ['7', '8', '9']
        params.selectedBrowsers = '1'
        params.selectedLocations = []
        params._action_showAll = 'Anzeigen'
        params.showDataMarkers = false

        params.showDataLabels = false
        params.selectedInterval = 0
        params.selectChartType = 0
        params.trimBelowLoadTimes = 0
        params.trimAboveLoadTimes = 0
        params.trimBelowRequestCounts = 0
        params.trimAboveRequestCounts = 0
        params.trimBelowRequestSizes = 0
        params.trimAboveRequestSizes = 0
        params.debug = false
        params.setFromHour = false
        params.setToHour = false
        params.chartWidth = 0
        params.chartHeight = 0
        params.loadTimeMinimum = 0
        when:
        // Create and fill the command:
        controllerUnderTest.bindData(command, params)
        then:
        // Verification:
        assertFalse(command.validate())
    }

    void testConstructViewDataMap() {
        given:
        Page page1 = new Page(name: 'Page1', weight: 0) {
            public Long getId() { return 1L; }
        };
        Page page2 = new Page(name: 'Page2', weight: 0.25d) {
            public Long getId() { return 2L; }
        };
        Page page3 = new Page(name: 'Page3', weight: 0.5d) {
            public Long getId() { return 3L; }
        };

        MeasuredEvent measuredEvent1 = new MeasuredEvent(name: 'MeasuredEvent1', testedPage: page3) {
            public Long getId() { return 1001L; }
        };
        MeasuredEvent measuredEvent2 = new MeasuredEvent(name: 'MeasuredEvent2', testedPage: page2) {
            public Long getId() { return 1002L; }
        };
        MeasuredEvent measuredEvent3 = new MeasuredEvent(name: 'MeasuredEvent3', testedPage: page1) {
            public Long getId() { return 1003L; }
        };
        MeasuredEvent measuredEvent4 = new MeasuredEvent(name: 'MeasuredEvent4', testedPage: page2) {
            public Long getId() { return 1004L; }
        };

        Browser browser1 = new Browser(name: 'Browser1') {
            public Long getId() { return 11L; }
        };

        Location location1 = new Location(label: 'Location1', browser: browser1) {
            public Long getId() { return 101L; }
        };
        Location location2 = new Location(label: 'Location2', browser: browser1) {
            public Long getId() { return 102L; }
        };
        Location location3 = new Location(label: 'Location3', browser: browser1) {
            public Long getId() { return 103L; }
        };

        controllerUnderTest.eventResultDashboardService.getAllJobGroups() >> {
            return [new JobGroup(name: 'Group2'),
                    new JobGroup(name: 'Group1')]
        };
        controllerUnderTest.eventResultDashboardService.getAllPages() >> {
            return [page1, page2, page3]
        };
        controllerUnderTest.eventResultDashboardService.getAllMeasuredEvents() >> {
            return [measuredEvent3, measuredEvent1, measuredEvent2, measuredEvent4]
        };
        controllerUnderTest.eventResultDashboardService.getAllBrowser() >> {
            return [browser1]
        };
        controllerUnderTest.eventResultDashboardService.getAllLocations() >> {
            return [location1, location2, location3]
        };

        when:
        // Run the test:
        Map<String, Object> result = controllerUnderTest.constructStaticViewDataOfShowAll();

        then:
        // Verify result (lists should be sorted by UI visible name or label):
        assertNotNull(result);
        assertEquals(18, result.size());

        // AggregatorType
        assertTrue(result.containsKey('aggrGroupLabels'))
        List<String> aggrGroupLabels = result.get('aggrGroupLabels');
        assertEquals(EventResultDashboardController.AGGREGATOR_GROUP_LABELS, aggrGroupLabels)
        //		assertEquals(2, aggrGroupLabels.size())
        //		assertEquals('AT-1', aggrGroupLabels.get(0))
        //		assertEquals('AT-2', aggrGroupLabels.get(1))

        assertTrue(result.containsKey('aggrGroupValuesCached'))
        assertTrue(result.containsKey('aggrGroupValuesUnCached'))

        // CSI-groups
        assertTrue(result.containsKey('folders'))
        List<JobGroup> csiGroups = result.get('folders');
        assertEquals(2, csiGroups.size())
        assertEquals('Group2', csiGroups.get(0).getName())
        assertEquals('Group1', csiGroups.get(1).getName())

        // Pages
        assertTrue(result.containsKey('pages'))
        List<Page> pages = result.get('pages');
        assertEquals(3, pages.size())
        assertEquals('Page1', pages.get(0).getName())
        assertEquals('Page2', pages.get(1).getName())
        assertEquals('Page3', pages.get(2).getName())

        // MeasuredEvents
        assertTrue(result.containsKey('measuredEvents'))
        List<MeasuredEvent> measuredEvents = result.get('measuredEvents');
        assertEquals(4, measuredEvents.size())
        assertEquals('MeasuredEvent3', measuredEvents.get(0).getName())
        assertEquals('MeasuredEvent1', measuredEvents.get(1).getName())
        assertEquals('MeasuredEvent2', measuredEvents.get(2).getName())
        assertEquals('MeasuredEvent4', measuredEvents.get(3).getName())

        // Browsers
        assertTrue(result.containsKey('browsers'))
        List<Browser> browsers = result.get('browsers');
        assertEquals(1, browsers.size())
        assertEquals('Browser1', browsers.get(0).getName())

        // Locations
        assertTrue(result.containsKey('locations'))
        List<Location> locations = result.get('locations');
        assertEquals(3, locations.size())
        assertEquals('Location1', locations.get(0).getLabel())
        assertEquals('Location2', locations.get(1).getLabel())
        assertEquals('Location3', locations.get(2).getLabel())

        // Data for java-script utilities:
        assertTrue(result.containsKey('dateFormat'))
        assertEquals(EventResultDashboardController.DATE_FORMAT_STRING_FOR_HIGH_CHART, result.get('dateFormat'))
        assertTrue(result.containsKey('weekStart'))
        assertEquals(EventResultDashboardController.MONDAY_WEEKSTART, result.get('weekStart'))

        // --- Map<PageID, Set<MeasuredEventID>>
        Map<Long, Set<Long>> eventsOfPages = result.get('eventsOfPages')
        assertNotNull(eventsOfPages)

        Set<Long> eventsOfPage1 = eventsOfPages.get(1L)
        assertNotNull(eventsOfPage1)
        assertEquals(1, eventsOfPage1.size());
        assertTrue(eventsOfPage1.contains(1003L));

        Set<Long> eventsOfPage2 = eventsOfPages.get(2L)
        assertNotNull(eventsOfPage2)
        assertEquals(2, eventsOfPage2.size());
        assertTrue(eventsOfPage2.contains(1002L));
        assertTrue(eventsOfPage2.contains(1004L));

        Set<Long> eventsOfPage3 = eventsOfPages.get(3L)
        assertNotNull(eventsOfPage3)
        assertEquals(1, eventsOfPage3.size());
        assertTrue(eventsOfPage3.contains(1001L));

        // --- Map<BrowserID, Set<LocationID>>
        Map<Long, Set<Long>> locationsOfBrowsers = result.get('locationsOfBrowsers')
        assertNotNull(locationsOfBrowsers)

        Set<Long> locationsOfBrowser1 = locationsOfBrowsers.get(11L)
        assertNotNull(locationsOfBrowser1)
        assertEquals(3, locationsOfBrowser1.size());
        assertTrue(locationsOfBrowser1.contains(101L));
        assertTrue(locationsOfBrowser1.contains(102L));
        assertTrue(locationsOfBrowser1.contains(103L));
    }

    /**
     * Test for inner class {@link EventResultDashboardShowAllCommand}.
     */
    void testShowAllCommand_testCopyRequestDataToViewModelMap() {
        given:
        // Create and fill a command:
        // form = '18.08.2013'
        Date expectedFromDate = new Date(1376776800000L)
        command.from = expectedFromDate
        command.fromHour = "12:00"
        Date expectedToDate = new Date(1376863200000L)
        command.to = expectedToDate
        command.toHour = "13:00"
        command.aggrGroup = AggregatorType.RESULT_CACHED_DOC_COMPLETE_INCOMING_BYTES.toString()
        command.selectedFolder = [1L]
        command.selectedPages = [1L, 5L]
        command.selectedMeasuredEventIds = [7L, 8L, 9L]
        command.selectedBrowsers = [2L]
        command.selectedLocations = [17L]
        command.selectedAllBrowsers = false
        command.selectedAllLocations = false
        command.selectedAllMeasuredEvents = false
        command.selectedAggrGroupValuesCached = [AggregatorType.RESULT_CACHED_LOAD_TIME]
        command.debug = false
        command.setFromHour = false
        command.setToHour = false
        command.includeNativeConnectivity = false
        command.selectedConnectivityProfiles = []
        command.selectedAllConnectivityProfiles = true
        command.includeNativeConnectivity = false
        command.includeCustomConnectivity = true
        command.customConnectivityName = 'Custom (6000.*'
        command.showDataMarkers = false

        command.showDataLabels = false
        command.selectedInterval = 0
        command.selectChartType = 0
        command.trimBelowLoadTimes = 0
        command.trimAboveLoadTimes = 0
        command.trimBelowRequestCounts = 0
        command.trimAboveRequestCounts = 0
        command.trimBelowRequestSizes = 0
        command.trimAboveRequestSizes = 0

        command.setFromHour = false
        command.setToHour = false
        command.chartWidth = 0
        command.chartHeight = 0
        command.loadTimeMinimum = 0

        // Do we fill all fields?
        assertTrue(command.validate())

        // Run the test:
        when:
        Map<String, Object> dataUnderTest = new HashMap<String, Object>();
        command.copyRequestDataToViewModelMap(dataUnderTest);

        then:
        // Verification:
        assertEquals(43, dataUnderTest.size());

        assertContainedAndNotNullAndEquals(dataUnderTest, 'selectedFolder', [1L]);
        assertContainedAndNotNullAndEquals(dataUnderTest, 'selectedPages', [1L, 5L]);
        assertContainedAndNotNullAndEquals(dataUnderTest, 'selectedAllMeasuredEvents', false);
        assertContainedAndNotNullAndEquals(dataUnderTest, 'selectedMeasuredEventIds', [7L, 8L, 9L]);
        assertContainedAndNotNullAndEquals(dataUnderTest, 'selectedAllBrowsers', false);
        assertContainedAndNotNullAndEquals(dataUnderTest, 'selectedBrowsers', [2L]);
        assertContainedAndNotNullAndEquals(dataUnderTest, 'selectedAllLocations', false);
        assertContainedAndNotNullAndEquals(dataUnderTest, 'selectedLocations', [17L]);

        assertContainedAndNotNullAndEquals(dataUnderTest, 'from', '18.08.2013');
        assertContainedAndNotNullAndEquals(dataUnderTest, 'fromHour', '12:00');

        assertContainedAndNotNullAndEquals(dataUnderTest, 'to', '19.08.2013');
        assertContainedAndNotNullAndEquals(dataUnderTest, 'toHour', '13:00');
        assertContainedAndNotNullAndEquals(dataUnderTest, 'debug', false);
        assertContainedAndNotNullAndEquals(dataUnderTest, 'selectedConnectivityProfiles', []);
        assertContainedAndNotNullAndEquals(dataUnderTest, 'selectedAllConnectivityProfiles', true);
        assertContainedAndNotNullAndEquals(dataUnderTest, 'selectedAllConnectivityProfiles', true);
        assertContainedAndNotNullAndEquals(dataUnderTest, 'customConnectivityName', 'Custom (6000.*');
    }

    /**
     * Test for inner class {@link EventResultDashboardShowAllCommand}.
     */
    void testShowAllCommand_testCopyRequestDataToViewModelMap_defaultsForMissingValues() {
        given:
        // Create and fill a command:
        // form = '18.08.2013'
        Date expectedFromDate = new Date(1376776800000L)
        command.from = expectedFromDate
        command.fromHour = null // Missing!
        Date expectedToDate = new Date(1376863200000L)
        command.to = expectedToDate
        command.toHour = null // Missing!
        command.aggrGroup = null // Missing!
        command.selectedFolder = [1L]
        command.selectedPages = [1L, 5L]
        command.selectedMeasuredEventIds = [7L, 8L, 9L]
        command.selectedBrowsers = [2L]
        command.selectedLocations = [17L]
        command.selectedAllBrowsers = false
        command.selectedAllLocations = false
        command.selectedAllMeasuredEvents = false
        command.selectedAggrGroupValuesCached = [AggregatorType.RESULT_CACHED_LOAD_TIME]
        command.debug = false
        command.setFromHour = false
        command.setToHour = false
        command.includeNativeConnectivity = false
        command.includeCustomConnectivity = true
        command.customConnectivityName = 'Custom (6000.*'
        command.showDataMarkers = false

        command.showDataLabels = false
        command.selectedInterval = 0
        command.selectChartType = 0
        command.trimBelowLoadTimes = 0
        command.trimAboveLoadTimes = 0
        command.trimBelowRequestCounts = 0
        command.trimAboveRequestCounts = 0
        command.trimBelowRequestSizes = 0
        command.trimAboveRequestSizes = 0
        command.setFromHour = false
        command.setToHour = false
        command.chartWidth = 0
        command.chartHeight = 0
        command.loadTimeMinimum = 0

        // Run the test:
        when:
        Map<String, Object> dataUnderTest = new HashMap<String, Object>();
        command.copyRequestDataToViewModelMap(dataUnderTest);

        then:
        // Verification:
        assertEquals(41, dataUnderTest.size());

        assertContainedAndNotNullAndEquals(dataUnderTest, 'selectedFolder', [1L]);
        assertContainedAndNotNullAndEquals(dataUnderTest, 'selectedPages', [1L, 5L]);
        assertContainedAndNotNullAndEquals(dataUnderTest, 'selectedAllMeasuredEvents', false);
        assertContainedAndNotNullAndEquals(dataUnderTest, 'selectedMeasuredEventIds', [7L, 8L, 9L]);
        assertContainedAndNotNullAndEquals(dataUnderTest, 'selectedAllBrowsers', false);
        assertContainedAndNotNullAndEquals(dataUnderTest, 'selectedBrowsers', [2L]);
        assertContainedAndNotNullAndEquals(dataUnderTest, 'selectedAllLocations', false);
        assertContainedAndNotNullAndEquals(dataUnderTest, 'selectedLocations', [17L]);

        assertContainedAndNotNullAndEquals(dataUnderTest, 'from', '18.08.2013');

        assertContainedAndNotNullAndEquals(dataUnderTest, 'to', '19.08.2013');
        assertContainedAndNotNullAndEquals(dataUnderTest, 'debug', false);
    }

    /**
     * Test for inner class {@link EventResultDashboardShowAllCommand}.
     */
    void testShowAllCommand_testCopyRequestDataToViewModelMap_selectAllSelection() {
        given:
        // Create and fill a command:
        // form = '18.08.2013'
        Date expectedFromDate = new Date(1376776800000L)
        command.from = expectedFromDate
        command.fromHour = "12:00"
        // to = '19.08.2013'
        Date expectedToDate = new Date(1376863200000L)
        command.to = expectedToDate
        command.toHour = "13:00"
        command.aggrGroup = AggregatorType.RESULT_CACHED_DOC_COMPLETE_INCOMING_BYTES.toString()
        command.selectedFolder = [1L]
        command.selectedPages = [1L, 5L]
        command.selectedAllMeasuredEvents = 'on'
        command.selectedMeasuredEventIds = []
        command.selectedBrowsers = [2L]
        command.selectedLocations = [17L]
        command.selectedAllBrowsers = false
        command.selectedAllLocations = false
        command.selectedAggrGroupValuesCached = [AggregatorType.RESULT_CACHED_LOAD_TIME]
        command.debug = true
        command.setFromHour = false
        command.setToHour = false
        command.includeNativeConnectivity = false
        command.customConnectivityName = 'Custom (6000/.*'
        command.selectedConnectivityProfiles = []
        command.selectedAllConnectivityProfiles = true
        command.includeNativeConnectivity = false
        command.includeCustomConnectivity = true
        command.customConnectivityName = 'Custom (6000.*'
        command.showDataMarkers = false

        command.showDataLabels = false
        command.selectedInterval = 0
        command.selectChartType = 0
        command.trimBelowLoadTimes = 0
        command.trimAboveLoadTimes = 0
        command.trimBelowRequestCounts = 0
        command.trimAboveRequestCounts = 0
        command.trimBelowRequestSizes = 0
        command.trimAboveRequestSizes = 0
        command.setFromHour = false
        command.setToHour = false
        command.chartWidth = 0
        command.chartHeight = 0
        command.loadTimeMinimum = 0

        // Do we fill all fields?
        assertTrue(command.validate())

        when:
        // Run the test:
        Map<String, Object> dataUnderTest = new HashMap<String, Object>();
        command.copyRequestDataToViewModelMap(dataUnderTest);

        then:
        // Verification:
        assertEquals(43, dataUnderTest.size());

        assertContainedAndNotNullAndEquals(dataUnderTest, 'selectedFolder', [1L]);
        assertContainedAndNotNullAndEquals(dataUnderTest, 'selectedPages', [1L, 5L]);
        assertContainedAndNotNullAndEquals(dataUnderTest, 'selectedAllMeasuredEvents', true);
        assertContainedAndNotNullAndEquals(dataUnderTest, 'selectedMeasuredEventIds', []);
        assertContainedAndNotNullAndEquals(dataUnderTest, 'selectedAllBrowsers', false);
        assertContainedAndNotNullAndEquals(dataUnderTest, 'selectedBrowsers', [2L]);
        assertContainedAndNotNullAndEquals(dataUnderTest, 'selectedAllLocations', false);
        assertContainedAndNotNullAndEquals(dataUnderTest, 'selectedLocations', [17L]);

        assertContainedAndNotNullAndEquals(dataUnderTest, 'from', '18.08.2013');
        assertContainedAndNotNullAndEquals(dataUnderTest, 'fromHour', "12:00");

        assertContainedAndNotNullAndEquals(dataUnderTest, 'to', '19.08.2013');
        assertContainedAndNotNullAndEquals(dataUnderTest, 'toHour', "13:00");
        assertContainedAndNotNullAndEquals(dataUnderTest, 'debug', true);
        assertContainedAndNotNullAndEquals(dataUnderTest, 'selectedConnectivityProfiles', []);
        assertContainedAndNotNullAndEquals(dataUnderTest, 'selectedAllConnectivityProfiles', true);
        assertContainedAndNotNullAndEquals(dataUnderTest, 'customConnectivityName', 'Custom (6000.*');
    }

    /**
     * Test for inner class {@link EventResultDashboardShowAllCommand}.
     */
    void testShowAllCommand_testCreateMvQueryParams() {
        given:
        // form = '18.08.2013'
        command.from = new Date(1376776800000L)
        command.fromHour = "12:00"
        // to = '19.08.2013'
        command.to = new Date(1376863200000L)
        command.toHour = "13:00"
        command.aggrGroup = AggregatorType.RESULT_CACHED_DOC_COMPLETE_INCOMING_BYTES.toString()
        command.selectedFolder = [1L]
        command.selectedPages = [1L, 5L]
        command.selectedMeasuredEventIds = [7L, 8L, 9L]
        command.selectedBrowsers = [2L]
        command.selectedLocations = [17L]
        command.selectedAggrGroupValuesCached = [AggregatorType.RESULT_CACHED_DOC_COMPLETE_INCOMING_BYTES]
        command.selectedAllMeasuredEvents = false
        command.selectedAllBrowsers = false
        command.selectedAllLocations = false
        command.selectedTimeFrameInterval = 0
        command.includeNativeConnectivity = false
        command.includeCustomConnectivity = false
        command.showDataMarkers = false
        command.showDataLabels = false
        command.selectedInterval = 0
        command.selectChartType = 0
        command.trimBelowLoadTimes = 0
        command.trimAboveLoadTimes = 0
        command.trimBelowRequestCounts = 0
        command.trimAboveRequestCounts = 0
        command.trimBelowRequestSizes = 0
        command.trimAboveRequestSizes = 0
        command.setFromHour = false
        command.setToHour = false
        command.chartWidth = 0
        command.chartHeight = 0
        command.loadTimeMinimum = 0
        command.debug = false

        when:
        // Do we fill all fields?
        assertTrue(command.validate())

        then:
        // Run the test:
        MvQueryParams mvQueryParams = command.createMvQueryParams();

        // Verification:
        assertNotNull(mvQueryParams);
        assertEquals([1L] as SortedSet, mvQueryParams.jobGroupIds);
        assertEquals([1L, 5L] as SortedSet, mvQueryParams.pageIds);
        assertEquals([7L, 8L, 9L] as SortedSet, mvQueryParams.measuredEventIds);
        assertEquals([2L] as SortedSet, mvQueryParams.browserIds);
        assertEquals([17L] as SortedSet, mvQueryParams.locationIds);
    }

    /**
     * Test for inner class {@link EventResultDashboardShowAllCommand}.
     */
    void testShowAllCommand_testCreateMvQueryParams_SelectAllIgnoresRealSelection_MeasuredEvents() {
        given:
        // form = '18.08.2013'
        command.from = new Date(1376776800000L)
        command.fromHour = "12:00"
        // to = '19.08.2013'
        command.to = new Date(1376863200000L)
        command.toHour = "13:00"
        command.aggrGroup = AggregatorType.RESULT_CACHED_DOC_COMPLETE_INCOMING_BYTES.toString()
        command.selectedFolder = [1L]
        command.selectedPages = [1L, 5L]
        command.selectedAllMeasuredEvents = 'on';
        command.selectedMeasuredEventIds = [7L, 8L, 9L]
        command.selectedBrowsers = [2L]
        command.selectedLocations = [17L]
        command.selectedAllBrowsers = false
        command.selectedAllLocations = false
        command.selectedAggrGroupValuesCached = [AggregatorType.RESULT_CACHED_LOAD_TIME]
        command.includeNativeConnectivity = false
        command.includeCustomConnectivity = true
        command.showDataMarkers = false

        command.showDataLabels = false
        command.selectedInterval = 0
        command.selectChartType = 0
        command.trimBelowLoadTimes = 0
        command.trimAboveLoadTimes = 0
        command.trimBelowRequestCounts = 0
        command.trimAboveRequestCounts = 0
        command.trimBelowRequestSizes = 0
        command.trimAboveRequestSizes = 0
        command.setFromHour = false
        command.setToHour = false
        command.chartWidth = 0
        command.chartHeight = 0
        command.loadTimeMinimum = 0
        command.debug = false

        // Do we fill all fields?
        assertTrue(command.validate())
        when:
        // Run the test:
        MvQueryParams mvQueryParams = command.createMvQueryParams();
        then:
        // Verification:
        assertNotNull(mvQueryParams);
        assertEquals([1L] as SortedSet, mvQueryParams.jobGroupIds);
        assertEquals([1L, 5L] as SortedSet, mvQueryParams.pageIds);
        assertEquals("This set is empty which means to fit all",
                [] as SortedSet, mvQueryParams.measuredEventIds);
        assertEquals([2L] as SortedSet, mvQueryParams.browserIds);
        assertEquals([17L] as SortedSet, mvQueryParams.locationIds);
    }

    /**
     * Test for inner class {@link EventResultDashboardShowAllCommand}.
     */
    void testShowAllCommand_testCreateMvQueryParams_SelectAllIgnoresRealSelection_Browsers() {
        given:
        // form = '18.08.2013'
        command.from = new Date(1376776800000L)
        command.fromHour = "12:00"
        // to = '19.08.2013'
        command.to = new Date(1376863200000L)
        command.toHour = "13:00"
        command.aggrGroup = AggregatorType.RESULT_CACHED_DOC_COMPLETE_INCOMING_BYTES.toString()
        command.selectedFolder = [1L]
        command.selectedPages = [1L, 5L]
        command.selectedMeasuredEventIds = [7L, 8L, 9L]
        command.selectedAllBrowsers = true;
        command.selectedBrowsers = [2L]
        command.selectedLocations = [17L]
        command.selectedAllLocations = false
        command.selectedAllMeasuredEvents = false
        command.selectedAggrGroupValuesCached = [AggregatorType.RESULT_CACHED_LOAD_TIME]
        command.includeNativeConnectivity = false
        command.includeCustomConnectivity = true
        command.showDataMarkers = false

        command.showDataLabels = false
        command.selectedInterval = 0
        command.selectChartType = 0
        command.trimBelowLoadTimes = 0
        command.trimAboveLoadTimes = 0
        command.trimBelowRequestCounts = 0
        command.trimAboveRequestCounts = 0
        command.trimBelowRequestSizes = 0
        command.trimAboveRequestSizes = 0
        command.setFromHour = false
        command.setToHour = false
        command.chartWidth = 0
        command.chartHeight = 0
        command.loadTimeMinimum = 0
        command.debug = false

        // Do we fill all fields?
        assertTrue(command.validate())

        when:
        // Run the test:
        MvQueryParams mvQueryParams = command.createMvQueryParams();
        then:
        // Verification:
        assertNotNull(mvQueryParams);
        assertEquals([1L] as SortedSet, mvQueryParams.jobGroupIds);
        assertEquals([1L, 5L] as SortedSet, mvQueryParams.pageIds);
        assertEquals([7L, 8L, 9L] as SortedSet, mvQueryParams.measuredEventIds);
        assertEquals("This set is empty which means to fit all",
                [] as SortedSet, mvQueryParams.browserIds);
        assertEquals([17L] as SortedSet, mvQueryParams.locationIds);
    }

    /**
     * Test for inner class {@link EventResultDashboardShowAllCommand}.
     */
    void testShowAllCommand_testCreateMvQueryParams_SelectAllIgnoresRealSelection_Locations() {
        given:
        // form = '18.08.2013'
        command.from = new Date(1376776800000L)
        command.fromHour = "12:00"
        // to = '19.08.2013'
        command.to = new Date(1376863200000L)
        command.toHour = "13:00"
        command.aggrGroup = AggregatorType.RESULT_CACHED_DOC_COMPLETE_INCOMING_BYTES.toString()
        command.selectedFolder = [1L]
        command.selectedPages = [1L, 5L]
        command.selectedMeasuredEventIds = [7L, 8L, 9L]
        command.selectedBrowsers = [2L]
        command.selectedAllLocations = true;
        command.selectedLocations = [17L]
        command.selectedAllBrowsers = false
        command.selectedAllMeasuredEvents = false
        command.selectedAggrGroupValuesCached = [AggregatorType.RESULT_CACHED_LOAD_TIME]
        command.includeNativeConnectivity = false
        command.includeCustomConnectivity = true
        command.showDataMarkers = false

        command.showDataLabels = false
        command.selectedInterval = 0
        command.selectChartType = 0
        command.trimBelowLoadTimes = 0
        command.trimAboveLoadTimes = 0
        command.trimBelowRequestCounts = 0
        command.trimAboveRequestCounts = 0
        command.trimBelowRequestSizes = 0
        command.trimAboveRequestSizes = 0
        command.setFromHour = false
        command.setToHour = false
        command.chartWidth = 0
        command.chartHeight = 0
        command.loadTimeMinimum = 0
        command.debug = false

        // Do we fill all fields?
        assertTrue(command.validate())
        when:
        // Run the test:
        MvQueryParams mvQueryParams = command.createMvQueryParams();
        then:
        // Verification:
        assertNotNull(mvQueryParams);
        assertEquals([1L] as SortedSet, mvQueryParams.jobGroupIds);
        assertEquals([1L, 5L] as SortedSet, mvQueryParams.pageIds);
        assertEquals([7L, 8L, 9L] as SortedSet, mvQueryParams.measuredEventIds);
        assertEquals([2L] as SortedSet, mvQueryParams.browserIds);
        assertEquals("This set is empty which means to fit all",
                [] as SortedSet, mvQueryParams.locationIds);
    }

    /**
     * Test for inner class {@link EventResultDashboardShowAllCommand}.
     */
    void testShowAllCommand_testCreateMvQueryParams_() {
        given:
        // form = '18.08.2013'
        command.from = new Date(1376776800000L)
        command.fromHour = "12:00"
        // to = '19.08.2013'
        command.to = new Date(1376863200000L)
        command.toHour = "13:00"
        command.aggrGroup = AggregatorType.RESULT_CACHED_DOC_COMPLETE_INCOMING_BYTES.toString()
        command.selectedFolder = [1L]
        command.selectedPages = [1L, 5L]
        command.selectedMeasuredEventIds = [7L, 8L, 9L]
        command.selectedBrowsers = [2L]
        command.selectedLocations = [17L]
        command.selectedAggrGroupValuesCached = [AggregatorType.RESULT_CACHED_DOC_COMPLETE_INCOMING_BYTES]
        command.selectedAllMeasuredEvents = false
        command.selectedAllBrowsers = false
        command.selectedAllLocations = false
        command.selectedTimeFrameInterval = 0
        command.selectedAllConnectivityProfiles = []
        command.includeNativeConnectivity = true
        command.includeCustomConnectivity = false
        command.showDataMarkers = false

        command.showDataLabels = false
        command.selectedInterval = 0
        command.selectChartType = 0
        command.trimBelowLoadTimes = 0
        command.trimAboveLoadTimes = 0
        command.trimBelowRequestCounts = 0
        command.trimAboveRequestCounts = 0
        command.trimBelowRequestSizes = 0
        command.trimAboveRequestSizes = 0
        command.setFromHour = false
        command.setToHour = false
        command.chartWidth = 0
        command.chartHeight = 0
        command.loadTimeMinimum = 0
        command.debug = false

        // Do we fill all fields?
        assertTrue(command.validate())
        when:
        // Run the test:
        MvQueryParams mvQueryParams = command.createMvQueryParams();
        then:
        // Verification:
        assertNotNull(mvQueryParams);
        assertEquals([1L] as SortedSet, mvQueryParams.jobGroupIds);
    }

    /**
     * Test for inner class {@link EventResultDashboardShowAllCommand}.
     */
    void testShowAllCommand_testCreateMvQueryParams_invalidCommand() {
        given: "an invalid command"
        assertFalse(command.validate())

        when:
        command.createMvQueryParams();

        then: "an exception is thrown"
        thrown IllegalStateException
    }

    /**
     * <p>
     * Asserts that a value is contained in a {@link Map}, that the value is
     * not <code>null</code> and is equals to specified expected value.
     * </p>
     *
     * @param dataUnderTest
     *         The map which contents is to be checked, not <code>null</code>.
     * @param key
     *         The key to which the value to check is bound,
     *         not <code>null</code>.
     * @param expectedValue
     *         The expected value to be equals to according to
     * {@link Object#equals(Object)}; not <code>null</code>.
     * @throws AssertionError
     *         if at least one of the conditions are not satisfied.
     */
    private
    static void assertContainedAndNotNullAndEquals(Map<String, Object> dataUnderTest, String key, Object expectedValue) throws AssertionError {
        assertNotNull('dataUnderTest', dataUnderTest)
        assertNotNull('key', key)
        assertNotNull('expectedValue', expectedValue)

        assertTrue('Map must contain key \"' + key + '\"', dataUnderTest.containsKey(key))
        assertNotNull('Map must contain a not-null value for key \"' + key + '\"', dataUnderTest.get(key))
        assertEquals(expectedValue, dataUnderTest.get(key))
    }
}
