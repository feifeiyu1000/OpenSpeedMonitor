//= require /urlHandling/urlHelper.js

"use strict";

var OpenSpeedMonitor = OpenSpeedMonitor || {};
OpenSpeedMonitor.ChartModules = OpenSpeedMonitor.ChartModules || {};
OpenSpeedMonitor.ChartModules.UrlHandling = OpenSpeedMonitor.ChartModules.UrlHandling || {};
OpenSpeedMonitor.ChartModules.UrlHandling = OpenSpeedMonitor.ChartModules.UrlHandling || {};

OpenSpeedMonitor.ChartModules.UrlHandling.PageComparison = (function () {
    var loadedState = "";

    var initWaitForPostload = function () {
        var dependencies = ["pageComparison", "resultSelection", "selectIntervalTimeframeCard"];
        OpenSpeedMonitor.postLoader.onLoaded(dependencies, function () {
            loadState(OpenSpeedMonitor.ChartModules.UrlHandling.UrlHelper.getUrlParameter());
            addEventHandlers();
        });
    };

    var addEventHandlers = function () {
        $(window).on("historyStateChanged", saveState);
        window.onpopstate = function (event) {
            var state = event.state || OpenSpeedMonitor.ChartModules.UrlHandling.UrlHelper.getUrlParameter();
            loadState(state);
        };
    };

    var urlEncodeState = function (state) {
        return $.param(state, true);
    };

    var saveState = function () {
        var state = {};
        state["from"] = $("#fromDatepicker").val();
        state["to"] = $("#toDatepicker").val();
        state["selectedTimeFrameInterval"] = $("#timeframeSelect").val();
        state['measurand'] = $("#selectedAggrGroupValuesUnCached").val();
        state['jobGroupId1'] = [];
        state['pageId1'] = [];
        state['jobGroupId2'] = [];
        state['pageId2'] = [];
        OpenSpeedMonitor.ChartModules.GuiHandling.PageComparison.Comparisons.getComparisons().forEach(function (comparison) {
            state['jobGroupId1'].push(comparison['jobGroupId1']);
            state['pageId1'].push(comparison['pageId1']);
            state['jobGroupId2'].push(comparison['jobGroupId2']);
            state['pageId2'].push(comparison['pageId2']);
        });
        var encodedState = urlEncodeState(state);
        if (encodedState !== loadedState) {
            loadedState = encodedState;
            window.history.pushState(state, "", "show?" + encodedState);
        }
    };

    var loadState = function (state) {
        if (!state) {
            return;
        }
        var encodedState = urlEncodeState(state);
        if (encodedState === loadedState) {
            return;
        }
        setTimeFrame(state);
        setComparisons(state);
        setMeasurand(state);
        loadedState = encodedState;
        if (state['jobGroupId1'] && state['pageId1'] && state['jobGroupId2'] && state['pageId2']) {
            $(window).trigger("historyStateLoaded");
        }
    };

    var setTimeFrame = function (state) {
        var timeFrame = (state["from"] && state["to"]) ? [new Date(state["from"]), new Date(state["to"])] : null;
        OpenSpeedMonitor.selectIntervalTimeframeCard.setTimeFrame(timeFrame, state["selectedTimeFrameInterval"]);
        if (state["comparativeFrom"] && state["comparativeTo"]) {
            var comparativeTimeFrame = [new Date(state["comparativeFrom"]), new Date(state["comparativeTo"])];
            OpenSpeedMonitor.selectIntervalTimeframeCard.setComparativeTimeFrame(comparativeTimeFrame);
        }
    };

    var setComparisons = function (params) {
        var pageId1s = params['pageId1'];
        var pageId2s = params['pageId2'];
        var jobGroupId1 = params['jobGroupId1'];
        var jobGroupId2 = params['jobGroupId2'];
        var rows = jobGroupId1.length;
        var isArray = jobGroupId1.constructor == Array;
        var comparisons = [];
        if (isArray) {
            for (var i = 0; i < rows; i++) {
                var comparison = {};
                comparison['jobGroupId1'] = jobGroupId1[i];
                comparison['jobGroupId2'] = jobGroupId2[i];
                comparison['pageId1'] = pageId1s[i];
                comparison['pageId2'] = pageId2s[i];
                comparisons.push(comparison);
            }
        } else {
            var comparison = {};
            comparison['jobGroupId1'] = jobGroupId1;
            comparison['jobGroupId2'] = jobGroupId2;
            comparison['pageId1'] = pageId1s;
            comparison['pageId2'] = pageId2s;
            comparisons.push(comparison);
        }
        OpenSpeedMonitor.ChartModules.GuiHandling.PageComparison.Comparisons.setComparisons(comparisons);
    };

    var setMeasurand = function (params) {
        var measurand = params['measurand'];
        if (!measurand) {
            return;
        }

        var selects = $(".measurand-select");
        if (measurand.startsWith("_UTM")) {
            var optGroupUserTimings = $(selects[0]).find('.measurand-opt-group-USER_TIMINGS');
            var alreadyThere = optGroupUserTimings.size() > 1;
            if (!alreadyThere) {
                OpenSpeedMonitor.domUtils.updateSelectOptions(optGroupUserTimings, [{
                    id: measurand,
                    name: measurand
                }])
            }
        }
        $("#selectedAggrGroupValuesUnCached").val(measurand);
    };

    initWaitForPostload();
    return {};
})();
