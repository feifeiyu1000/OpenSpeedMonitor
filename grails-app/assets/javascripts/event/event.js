//= require /bower_components/bootstrap-timepicker/js/bootstrap-timepicker.js
//= require_self

$(document).ready(function() {
    var timeInput = $('#hourTimepicker');
    timeInput.timepicker({showMeridian: false});
});