import {getStatusProductCount,
 		projectsRadarChart, 
 		getLineChart} 
 		from '../js/charts.js'

$(document).ready(function(){
	getStatusProductCount();
	projectsRadarChart();
	getLineChart();
	$("#chartCount").text(0);	
})

let value = 0; 
setInterval(function(){
	value += 1;
	$("#chartCount").text(value);
}, 60000);