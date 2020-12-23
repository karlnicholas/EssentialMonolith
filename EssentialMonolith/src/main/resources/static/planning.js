$(document).ready(function(){
	$.ajax('/planning', { success: function(data){
		var content = '<table style="text:center;"><tr><th>Entry Date</th><th>Hours</th><th>Rate</th><th>Project</th><th>Employee</th></tr>';
		for(const worklog of data) {
			content += '<tr><td>' + worklog.entryDate + '</td><td>' + worklog.hours + '</td><td>' + worklog.rate + '</td><td>' + worklog.project.name + '</td><td>' + worklog.employee.name + '</td></tr>';
		}
		content += '</table>';
		$('#worklog').empty().append(content);
	}})
})