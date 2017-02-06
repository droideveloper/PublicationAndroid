(function() {
  // forEach polyfill
  if (!Array.prototype.forEach) {
    Array.prototype.forEach = function(iterator) {
      var self = this || [];
      for(var index = 0, z = self.length; index < z; index++) {
        iterator(self[index], index, self);
      }
    };
  }
  // startsWith polyfill
  if (!String.prototype.startsWith) {
    String.prototype.startsWith = function(searchString, position) {
      position = position || 0;
      return this.substr(position, searchString.length) === searchString;
    };
  }
  // dimensions
  document.addEventListener("DOMContentLoaded", function() {
    var rect = document.body.getBoundingClientRect();
    if (bridge) {
      bridge.boundsOfPage(rect.width, rect.height);
    }
  });
  // content positions
  var collection = document.body.getElementsByTagName("a") || [];
  collection.forEach(function(entry, index) {
    var uri = entry.href || "";
    if (uri.startsWith("file://")) {
      var rect = entry.getBoundingClientRect();
      if (bridge) {
        bridge.indexOfUri(rect.left, uri);
      }
    }
  });
}());