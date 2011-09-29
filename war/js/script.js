// jQuery mobile method for dynamically injecting pages.
// JQM doc page located at ~/docs/pages/page-dynamic.html.
$(document).bind('pagebeforechange', function(event, data) {
  // Only handle changepage calls whens loading a page URL.
  if (typeof data.toPage === "string") {
    // Only hanlde requests for the item page.
    var url = $.mobile.path.parseUrl(data.toPage);
    var re = /^#item/;
    if (url.hash.search(re) !== -1) {
      showItem(url, data.options);
      event.preventDefault();
    }
  }
});// Listen for when the index/list page is shown to display a new list.

$('#index').live('pageshow', function() {
  $.mobile.showPageLoadingMsg();
  $.getJSON('/rest/' + itemName(), function(data) {
    showList(data);
    $.mobile.hidePageLoadingMsg();
  });
});

// Generic function to show the list of items.
// Calls listItem which needs to be defined per type of item.
function showList(data) {
  var page = $('#index');
  var header = page.children(':jqmData(role=header)');
  var content = page.children(':jqmData(role=content)');
  var markup = '<ul data-role="listview">';
  for (i = 0; i < data[itemName()].length; i++) {
    var item = data[itemName()][i];
    markup += listItem(item);
  }
  header.find('h1').html(listTitle());
  content.html(markup);
  content.find(':jqmData(role=listview)').listview();
}

// Generic function to show the item page.
// Calls itemDetails which needs to be defined per type of item.
function showItem(url, options) {
  $.mobile.showPageLoadingMsg();
  var id = url.hash.replace(/.*id=/, '');
  var pageSelector = url.hash.replace(/\?.*$/, "");

  $.getJSON('/rest/' + itemName() + '/' + id, function(data) {
    var page = $(pageSelector);
    var header = page.children(':jqmData(role=header)');
    var content = page.children(':jqmData(role=content)');
    header.find('h1').html(itemTitle());
    content.html(itemDetails(data));
    options.dataUrl = url.href;
    $.mobile.changePage(page, options);
    $.mobile.hidePageLoadingMsg();
  });
}