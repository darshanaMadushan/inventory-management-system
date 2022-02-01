import {getCategoriesProductCountAJAX,
		getStatusProductCount,
		getLineChart} 
		from './charts.js'
	
$(document).ready(function(){
	$("#searchSerial").focus();
	getCategoriesProductCountAJAX()
	getStatusProductCount()
	getLineChart();
	$("#chartCount").text(0);
});

let value = 0; 
setInterval(function(){
	value += 1;
	$("#chartCount").text(value);
}, 60000);