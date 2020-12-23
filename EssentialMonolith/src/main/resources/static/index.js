$(document).ready(function(){
	$.ajax('/hr', { success: function(data){
		var content = '<table style="text:center;"><tr><th>Name</th><th>Department</th></tr>';
		for(const employee of data) {
			content += '<tr><td>' + employee.name + '</td><td>' + employee.department.name + '</td></tr>';
		}
		content += '</table>';
		$('#employees').empty().append(content);
	}})
})