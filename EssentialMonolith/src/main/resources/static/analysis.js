$(document).ready(function() {
	$.ajax('analysis', {success: function(data) {
		var d = new Date(data.lastRunTime);
		var content = '<p> lastRunTime: ' + d.toLocaleDateString() + ' ' + d.toLocaleTimeString() + ' populating: ' + data.populating + '</p>';
		$('#analysis').empty().append(content);
	}})
	$.ajax('analysis/count', {success: function(data) {
		$('#count').empty().append('(Current Fact Count: ' + data + ')');
	}})
	$.ajax('analysis/billingdimensions', {success: function(data) {
		displayDimensions(data, 'billingtable', '#billingdimensions');
	}})
	$('#populate').click(function(){
		$.ajax('analysis/populate', {success: function() {
			location.reload();
		}})
	});
	$('#billingresult').click(function(){
		queryDisplayResult('analysis/billingresult','#billingtable','#billingresults');
	});
})
function NumericCommas(yourNumber) {
    //Seperates the components of the number
    var n= yourNumber.toFixed(0).toString().split(".");
    //Comma-fies the first part
    n[0] = n[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    //Combines the two sections
    return n.join(".");
}
function displayDimensions(data, table, display) {
	var content = '<table id="' + table + '"><tr>';
	for ( i = 0; i < data.length; ++i ) {
		content += '<th>' + data[i].name + '</th>';
	}
	content += '</tr><tr>';
	for ( i = 0; i < data.length; ++i ) {
		content += '<td><select id="' + data[i].name + '"><option value=""></option>';
		var dvalue  = data[i].dimensions;
		for ( j = 0; j < dvalue.length; ++j ) {
			content += '<option value="' + dvalue[j].id + '">' + dvalue[j].name + '</option>'; 
		}
		content += '</select></td>';
	}
	content += '</tr></table>';
	$(display).empty().append(content);
}
function queryDisplayResult(url, table, display) {
	var queryString = '';
	var queryMark = '?';
	selectionTable = $(document).find(table);
	var th = selectionTable[0].rows[0];
	var tr = selectionTable[0].rows[1];
	for ( i = 0; i < tr.cells.length; ++i ) {
		var select = tr.cells[i].firstChild;
		var selectedIndex = select.selectedIndex;
		if ( selectedIndex > 0 ) {
			queryString += queryMark + th.cells[i].firstChild.textContent + '=' + select[selectedIndex].value;
			queryMark = '&';
		}
	}
	$.ajax(url+queryString, {success: function(data) {
		if ( data.n ) {
			var content = '<table style="text-align:center; width: 350px;"><tr><th>Total</th><th>Mean</th><th>Min</th><th>Max</th><th>Std Dev</th></tr>';
			content += '<tr><td>' 
				+ NumericCommas(data.mean * data.n) + '</td><td>' 
				+ NumericCommas(data.mean) +  '</td><td>'
				+ NumericCommas(data.min) +  '</td><td>'
				+ NumericCommas(data.max) +  '</td><td>';
				if ( data.standardDeviation ) {
					content += NumericCommas(data.standardDeviation);
				}
			content += '</td></tr></table>';
			$(display).empty().append(content);
		} else {
			$(display).empty().append("None found.");
		}
	}})
}