function showList(data) {
  var content = $('#index').children(':jqmData(role=content)');
  var markup = '<ul data-role="listview">';
  for (i = 0; i < data[itemName()].length; i++) {
    var item = data[itemName()][i];
    markup += listItem(item);
  }
  content.html(markup);
  content.find(':jqmData(role=listview)').listview();
}

$('#index').live('pageshow', function() {
  $.mobile.showPageLoadingMsg();
  $.getJSON('/rest/' + itemName(), function(data) {
    showList(data);
    $.mobile.hidePageLoadingMsg();
  });
});

function showItem(url, options) {
  $.mobile.showPageLoadingMsg();
  var id = url.hash.replace(/.*id=/, '');
  var pageSelector = url.hash.replace(/\?.*$/, "");

  $.getJSON('/rest/' + itemName() + '/' + id, function(data) {
    var $page = $(pageSelector);
    var $header = $page.children(':jqmData(role=header)');
    var $content = $page.children(':jqmData(role=content)');
    $content.html(itemDetails(data));
    options.dataUrl = url.href;
    $.mobile.changePage($page, options);
    $.mobile.hidePageLoadingMsg();
  });
}

$(document).bind('pagebeforechange', function(event, data) {
  // We only want to handle changepage calls where the caller is asking us to
  // load a page by URL.
  if (typeof data.toPage === "string") {
    // We are being asked to load a page by URL, but we only want to handle URLs
    // that request the data for a specific item.
    var url = $.mobile.path.parseUrl(data.toPage);
    var re = /^#item/;
    if (url.hash.search(re) !== -1) {
      // We're being asked to display the items for a specific item. Call our
      // internal method that builds the content for the item on the fly.
      showItem(url, data.options);

      // Make sure to tell changepage we've handled this call so it doesn't have
      // to do anything.
      event.preventDefault();
    }
  }
});