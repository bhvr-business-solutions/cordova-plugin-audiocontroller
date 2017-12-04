var exec = require('cordova/exec');

var PLUGIN_NAME = 'AudioController';

var cbs = [];

var AudioController = {
  subscribe: function(cb) {
    cbs.push(cb);

    // exec native code 1 time only
    if (cbs.length !== 1) {
      return;
    }

    this.watch(this.watch);
  },

  watch: function(callback) {
    exec(function(message) {

      var time;

      if (message.indexOf('|') !== -1) {
        time = parseFloat(message.split('|')[1]);
        message = message.split('|')[0];
      }

      for(var i in cbs) {
        cbs[i](message, time);
      }

      callback();
    }, null, PLUGIN_NAME, 'subscribe', []);
  },


  setControls: function(data) {
    data.artist = !isUndefined(data.artist) ? data.artist : "";
    data.track = !isUndefined(data.track) ? data.track : "";
    data.album = !isUndefined(data.album) ? data.album : "";
    data.cover = !isUndefined(data.cover) ? data.cover : "";
    data.ticker = !isUndefined(data.ticker) ? data.ticker : "";
    data.duration = !isUndefined(data.duration) ? data.duration : 0;
    data.elapsed = !isUndefined(data.elapsed) ? data.elapsed : 0;
    data.isPlaying = !isUndefined(data.isPlaying) ? data.isPlaying : true;
    data.hasPrev = !isUndefined(data.hasPrev) ? data.hasPrev : true;
    data.hasNext = !isUndefined(data.hasNext) ? data.hasNext : true;
    data.hasSkipForward = !isUndefined(data.hasSkipForward) ? data.hasSkipForward : false;
    data.hasSkipBackward = !isUndefined(data.hasSkipBackward) ? data.hasSkipBackward : false;
    data.hasScrubbing = !isUndefined(data.hasScrubbing) ? data.hasScrubbing : false;
    data.skipForwardInterval = !isUndefined(data.skipForwardInterval) ? data.skipForwardInterval : 0;
    data.skipBackwardInterval = !isUndefined(data.skipBackwardInterval) ? data.skipBackwardInterval : 0;
    data.hasClose = !isUndefined(data.hasClose) ? data.hasClose : false;
    data.dismissable = !isUndefined(data.dismissable) ? data.dismissable : false;
    data.playIcon = !isUndefined(data.playIcon) ? data.playIcon : "";
    data.pauseIcon = !isUndefined(data.pauseIcon) ? data.pauseIcon : "";
    data.prevIcon = !isUndefined(data.prevIcon) ? data.prevIcon : "";
    data.nextIcon = !isUndefined(data.nextIcon) ? data.nextIcon : "";
    data.closeIcon = !isUndefined(data.closeIcon) ? data.closeIcon : "";
    data.notificationIcon = !isUndefined(data.notificationIcon) ? data.notificationIcon : "";

    exec(function() {}, null, PLUGIN_NAME, 'setControls', [data]);
  },

  unsetControls: function() {
    exec(function() {}, null, PLUGIN_NAME, 'unsetControls', []);
  },

  updateIsPlaying: function(isPlaying) {
    exec(function() {}, null, PLUGIN_NAME, 'updateIsPlaying', [isPlaying]);
  }
};

module.exports = AudioController;


function isUndefined(val) {
  return val === undefined;
}