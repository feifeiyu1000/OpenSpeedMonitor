//= require bower_components/vue/dist/vue.js
"use strict";

    Vue.component('threshold-measured-event', {
        props: ['thresholds'],
        template: '#threshold-tab-measured-event-vue',
        methods: {
            deleteThreshold: function (threshold) {
                this.$emit('delete-threshold', threshold);
            },
            updateThreshold: function (threshold) {
                this.$emit('update-threshold', threshold);
            }
        }
    });