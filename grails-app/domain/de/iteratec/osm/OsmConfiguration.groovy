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

package de.iteratec.osm

import de.iteratec.osm.csi.CsiTransformation

/**
 * Configuration of OpenSpeedMonitor to be changed at runtime.
 */
class OsmConfiguration {

    static final Integer DEFAULT_DETAIL_DATA_STORAGE_TIME_IN_WEEKS = 2
    static final Integer DEFAULT_MAX_DOWNLOAD_TIME_IN_MINUTES = 60
    static final Integer DEFAULT_MIN_VALID_LOADTIME = 10
    static final Integer DEFAULT_MAX_VALID_LOADTIME = 180000
    static final Integer DEFAULT_INITIAL_CHART_HEIGHT_IN_PIXELS = 400
    static final String DEFAULT_MAIN_URL_UNDER_TEST = ''
    static final Integer DEFAULT_MAX_DATA_STORAGE_TIME_IN_MONTHS = 13
    static final Integer DEFAULT_MAX_BATCH_ACTIVITY_STORAGE_TIME_IN_DAYS = 30
    static final CsiTransformation CSI_TRANSFORMATION_TO_USE = CsiTransformation.BY_MAPPING
    static final Integer INTERNAL_MONITORING_STORAGETIME_IN_DAYS = 30


    /* Default (injected) attributes of GORM */
    Long	id
    //String	version

    /* Automatic timestamping of GORM */
    Date	dateCreated
    Date	lastUpdated

    /** Number of weeks very detailed data (waterfalls) is stored. Nightly cleanup deletes older data. */
    Integer detailDataStorageTimeInWeeks = DEFAULT_DETAIL_DATA_STORAGE_TIME_IN_WEEKS
    /** Maximum time in minutes osm polls result of a job run. If no result with status >= 200 returned within this time run is marked as failed */
    Integer defaultMaxDownloadTimeInMinutes = DEFAULT_MAX_DOWNLOAD_TIME_IN_MINUTES
    /**
     * {@link EventResult}s with a loadTimeInMillisecs lower than this won't be factored in csi-{@link CsiAggregation}.
     * Also they are ignore in several Dashboards because they represent meausrement faults.
     */
    Integer minValidLoadtime = DEFAULT_MIN_VALID_LOADTIME
    /**
     * {@link EventResult}s with a loadTimeInMillisecs greater than this won't be factored in csi-{@link CsiAggregation}
     * Also they are ignore in several Dashboards because they represent meausrement faults.
     */
    Integer maxValidLoadtime = DEFAULT_MAX_VALID_LOADTIME
    /** Initial height of charts when opening dashboards. */
    Integer initialChartHeightInPixels = DEFAULT_INITIAL_CHART_HEIGHT_IN_PIXELS
    /** Main url under test within this osm instance. Got shown in chart title of csi dashboard. */
    String mainUrlUnderTest = DEFAULT_MAIN_URL_UNDER_TEST
    /** Maximum Number of months osm keeps results in database   */
    Integer maxDataStorageTimeInMonths = DEFAULT_MAX_DATA_STORAGE_TIME_IN_MONTHS
    /** Maximum Number of days osm keeps BatchActivities in database   */
    Integer maxBatchActivityStorageTimeInDays = DEFAULT_MAX_BATCH_ACTIVITY_STORAGE_TIME_IN_DAYS
    /** Method of transformation from measured load time to percent of users which are satisfied by that load time used in the application. */
    CsiTransformation csiTransformation = CSI_TRANSFORMATION_TO_USE
    /** Time in days internal monitoring data like location queues is stored. */
    Integer internalMonitoringStorageTimeInDays = INTERNAL_MONITORING_STORAGETIME_IN_DAYS
    /** Did the infrastructure setup run already? */
    InfrastructureSetupStatus infrastructureSetupRan = InfrastructureSetupStatus.NOT_STARTED

    enum InfrastructureSetupStatus {
        NOT_STARTED, ABORTED, FINISHED
    }

    static mapping = {
        detailDataStorageTimeInWeeks(defaultValue: DEFAULT_DETAIL_DATA_STORAGE_TIME_IN_WEEKS)
        defaultMaxDownloadTimeInMinutes(defaultValue: DEFAULT_MAX_DOWNLOAD_TIME_IN_MINUTES)
        minValidLoadtime(defaultValue: DEFAULT_MIN_VALID_LOADTIME)
        maxValidLoadtime(defaultValue: DEFAULT_MAX_VALID_LOADTIME)
        initialChartHeightInPixels(defaultValue: DEFAULT_INITIAL_CHART_HEIGHT_IN_PIXELS)
        mainUrlUnderTest(defaultValue: DEFAULT_MAIN_URL_UNDER_TEST)
        maxDataStorageTimeInMonths defaultValue: DEFAULT_MAX_DATA_STORAGE_TIME_IN_MONTHS
        maxBatchActivityStorageTimeInDays defaultValue: DEFAULT_MAX_BATCH_ACTIVITY_STORAGE_TIME_IN_DAYS
        csiTransformation defaultValue: CSI_TRANSFORMATION_TO_USE
        internalMonitoringStorageTimeInDays defaultValue: INTERNAL_MONITORING_STORAGETIME_IN_DAYS
        infrastructureSetupRan defaultValue: InfrastructureSetupStatus.NOT_STARTED
    }

    static constraints = {
        detailDataStorageTimeInWeeks(defaultValue: DEFAULT_DETAIL_DATA_STORAGE_TIME_IN_WEEKS, min: -2147483648, max: 2147483647)
        defaultMaxDownloadTimeInMinutes(defaultValue: DEFAULT_MAX_DOWNLOAD_TIME_IN_MINUTES, min: -2147483648, max: 2147483647)
        minValidLoadtime(defaultValue: DEFAULT_MIN_VALID_LOADTIME, min: -2147483648, max: 2147483647)
        maxValidLoadtime(defaultValue: DEFAULT_MAX_VALID_LOADTIME, min: -2147483648, max: 2147483647)
        initialChartHeightInPixels(defaultValue: DEFAULT_INITIAL_CHART_HEIGHT_IN_PIXELS, min: -2147483648, max: 2147483647)
        mainUrlUnderTest(defaultValue: DEFAULT_MAIN_URL_UNDER_TEST, maxSize: 255)
        maxDataStorageTimeInMonths(defaultValue: DEFAULT_MAX_DATA_STORAGE_TIME_IN_MONTHS, min: 0, max: 2147483647)
        maxBatchActivityStorageTimeInDays defaultValue: DEFAULT_MAX_BATCH_ACTIVITY_STORAGE_TIME_IN_DAYS
        csiTransformation(defaultValue: CSI_TRANSFORMATION_TO_USE)
        internalMonitoringStorageTimeInDays defaultValue: INTERNAL_MONITORING_STORAGETIME_IN_DAYS
        infrastructureSetupRan defaultValue: InfrastructureSetupStatus.NOT_STARTED
    }
}
