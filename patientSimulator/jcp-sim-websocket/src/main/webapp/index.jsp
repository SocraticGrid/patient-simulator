<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
    "http://www.w3.org/TR/html4/loose.dtd">

<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JCpSim Web Console</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <!-- Bootstrap -->
        <link href="css/bootstrap.min.css" rel="stylesheet" media="screen">
        <link href="css/bootstrap-switch.min.css" rel="stylesheet" media="screen">
        <link href="css/bootstrap-multiselect.css" rel="stylesheet" media="screen">

        <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
        <!--[if lt IE 9]>
          <script src="../../assets/js/html5shiv.js"></script>
          <script src="../../assets/js/respond.min.js"></script>
        <![endif]-->
    </head>
    <body>
        <div class="container">
            <h1 id="title">JCpSim Web Console</h1>
            <div class="panel panel-default">
                <div class="panel-heading"><h3>Physiologic Monitor</h3></div>
                <div class="panel-body">
                    <form class="form-horizontal" role="form">
                        <div class="form-group">
                            <label for="dataGathererStatus" class="col-sm-2 control-label">Status</label>
                            <div class="col-sm-10">
                                <input type="checkbox" name="dataGathererStatus" id="dataGathererStatus" checked>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="dataGathererSampleRate" class="col-sm-2 control-label">Sample Rate</label>
                            <div class="col-xs-2">
                                <div class="input-group">
                                    <input type="number" class="form-control" id="dataGathererSampleRate">
                                    <span class="input-group-btn">
                                        <button id="dataGathererSampleRateButton" class="btn btn-info" type="button">Change!</button>
                                    </span>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="dataGathererFields" class="col-sm-2 control-label">Fields</label>
                            <div class="col-sm-10">
                                <div class="btn-group">
                                    <select id="dataGathererFields" class="select2" multiple="multiple" style="width:400px;">

                                    </select>
                                    <button id="dataGathererFieldsButton" class="btn btn-info" type="button">Change!</button>
                                </div>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="dataGathererFormat" class="col-sm-2 control-label">Format</label>
                            <div class="col-sm-10">
                                <div class="btn-group">
                                    <select id="dataGathererFormat" class="select2" style="width:400px;">
                                        <option value="JSON">JSON</option>
                                        <option value="JSONLD">JSONLD</option>
                                    </select>
                                    <button id="dataGathererFormatButton" class="btn btn-info" type="button">Change!</button>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3>CDS System</h3>
                </div>
                <div class="panel-body">
                    <form class="form-horizontal" role="form">
                        <div class="form-group">
                            <label for="cdsStatus" class="col-sm-2 control-label">Alerts</label>
                            <div class="col-sm-10">
                                <input type="checkbox" name="cdsStatus" id="cdsStatus" checked>
                            </div>
                        </div>
                        <div class="form-group">
                            <label for="cdsGenomicStatus" class="col-sm-2 control-label">Genomic Data</label>
                            <div class="col-sm-10">
                                <input type="checkbox" name="cdsGenomicStatus" id="cdsGenomicStatus" checked>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
            <div class="panel panel-default">
                <div class="panel-heading">
                    <h3>Patient Data</h3>
                </div>
                <div class="panel-body">
                    <canvas id="dataChart" width="1000" height="300"></canvas>
                </div>
                <div class="panel-footer">
                    <select id="chartFields" class="select2" style="width:400px;">
                        
                    </select>
                </div>
            </div>
        </div>

        <!--  Waiting dialog -->
        <div class="modal fade" id="waitDialog" tabindex="-1" data-backdrop="static" data-keyboard="false">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div id="waitDialogHeader" class="modal-header">
                        <h1>Processing...</h1>
                    </div>
                    <div class="modal-body">
                        <div class="progress progress-striped active">
                            <div class="progress-bar" role="progressbar" aria-valuenow="100" aria-valuemin="0" aria-valuemax="100" style="width: 100%"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- jQuery (necessary for Bootstrap's JavaScript plugins) -->
        <script src="//code.jquery.com/jquery.js"></script>
        <!-- Include all compiled plugins (below), or include individual files as needed -->
        <script src="js/bootstrap.min.js"></script>
        <script src="js/bootstrap-switch.min.js"></script>
        <script src="js/bootstrap-multiselect.js"></script>
        <script type="text/javascript" charset="utf-8" src="js/Chart.min.js"></script>

        <script>//<![CDATA[

            var dataChart;
            var optionsNoAnimation = {
                animation: false,
                datasetFill: false
            };



            (function() {

                showWaitDialog("Loading...");

                $("[name='dataGathererStatus']").bootstrapSwitch({
                    onColor: 'success',
                    offColor: 'danger'
                });

                $("#dataGathererStatus").on('switchChange.bootstrapSwitch', function(event, state) {
                    changeDataGathererStatus('running', state);
                });

                $("#dataGathererSampleRateButton").click(function() {
                    changeDataGathererStatus('sampleRate', $("#dataGathererSampleRate").val());
                });

                $("#dataGathererFieldsButton").click(function() {
                    if (!$("#dataGathererFields").val()) {
                        alert("Please select at least one field!");
                        return;
                    }
                    changeDataGathererStatus('fields', $("#dataGathererFields").val().toString());
                });

                $("#dataGathererFormatButton").click(function() {
                    changeDataGathererStatus('format', $("#dataGathererFormat").val());
                });


                $('#dataGathererFields').multiselect({
                    includeSelectAllOption: true,
                    includeSelectAllDivider: true
                });

                $('#dataGathererFormat').multiselect({
                });
                
                $("[name='cdsStatus']").bootstrapSwitch({
                    onColor: 'success',
                    offColor: 'danger'
                });
                
                $("#cdsStatus").on('switchChange.bootstrapSwitch', function(event, state) {
                    changeCDSStatus("ALERTS", state?"ON":"OFF");
                });
                
                $("[name='cdsGenomicStatus']").bootstrapSwitch({
                    onColor: 'success',
                    offColor: 'danger'
                });
                
                $("#cdsGenomicStatus").on('switchChange.bootstrapSwitch', function(event, state) {
                    changeCDSStatus("GENOME", state?"ON":"OFF");
                });

                $('#chartFields').multiselect({
                    onChange: function(element, checked) {
                        dataLabels = [];
                        dataValues = [];
                    }
                });

                var ctx = document.getElementById("dataChart").getContext("2d");
                dataChart = new Chart(ctx);

                //get agent configuration data
                $.ajax({
                    type: "GET",
                    url: "JCpSimWebConsoleServlet?action=getInitialConfiguration"
                }).done(function(data) {

                    try {

                        var fields = data.availableFields;
                        $.each(fields, function() {
                            $("#dataGathererFields").append("<option value='" + this + "'>" + this + "</option>");
                        })
                        $('#dataGathererFields').multiselect('rebuild');

                        getStatusFromServer();
                        setInterval(getStatusFromServer, 10000);
                        
                        getCDSStatusFromServer();
                        setInterval(getCDSStatusFromServer, 10000);

                    } finally {
                        hideWaitDialog();
                    }

                });
                
                

                openWS();

            })();

            function showWaitDialog(message) {
                $('#waitDialogHeader h1').html(message || "Please wait...");
                $('#waitDialog').modal();
            }

            function hideWaitDialog() {
                $('#waitDialog').modal('hide');
            }

            function getStatusFromServer() {
                $.ajax({
                    type: "GET",
                    url: "JCpSimWebConsoleServlet?action=getDataGathererStatus"
                }).done(function(data) {
                    updateUI(data);
                });
            }
            
            function getCDSStatusFromServer() {
                $.ajax({
                    type: "GET",
                    url: "RecommendationConsoleServlet?action=getStatus"
                }).done(function(data) {
                    updateCDSUI(data);
                });
            }
            
            function updateCDSUI(data) {
                $("[name='cdsStatus']").bootstrapSwitch('state', data.alertService.enabled, true);
                $("[name='cdsGenomicStatus']").bootstrapSwitch('state', data.genomicService.enabled, true);
            }

            function updateUI(data) {
                $('#dataGathererSampleRate').val(data.jcpsim.sampleRate);
                $("[name='dataGathererStatus']").bootstrapSwitch('state', data.jcpsim.running, true);
                $('#dataGathererFormat').multiselect('select', data.jcpsim.format);

                var chartFieldCurrentValue = $("#chartFields").val();
                $('#chartFields').empty();
                $.each(data.jcpsim.selectedFields, function() {
                    $('#dataGathererFields').multiselect('select', this);
                    if (this != "TIME"){
                        $('#chartFields').append("<option value='" + this + "'>" + this + "</option>");
                    }
                });

                $('#chartFields').multiselect('rebuild');
                $('#chartFields').multiselect('select', chartFieldCurrentValue);
                

            }

            function changeDataGathererStatus(attribute, value) {
                showWaitDialog("Changing '" + attribute + "' value of Data Gatherer to '" + value + "'");
                $.ajax({
                    type: "GET",
                    url: "JCpSimWebConsoleServlet?action=changeDataGathererStatus&attribute=" + attribute + "&value=" + value
                }).done(function(data) {
                    try {
                        if (data.status && data.status === "error") {
                            alert("ERROR: " + data.message);
                            return;
                        }
                        updateUI(data);
                    } finally {
                        hideWaitDialog();
                    }

                });
            }
            
            function changeCDSStatus(service, value /*'ON' or 'OFF' */) {
                showWaitDialog("Changing CDS "+service+" Status to '" + value + "'");
                $.ajax({
                    type: "GET",
                    url: "RecommendationConsoleServlet?action=toggle&service="+service+"&value="+value
                }).done(function(data) {
                    try {
                        if (data.status && data.status === "error") {
                            alert("ERROR: " + data.message);
                            return;
                        }
                        updateCDSUI(data);
                    } finally {
                        hideWaitDialog();
                    }

                });
            }
            
            function openWS() {
                if ('WebSocket' in window) {

                    this.socket = new WebSocket("ws://" + location.host + "/jcp-sim-websocket/jcpsimEndpoint");
                    this.socket.onmessage = function(message) {

                        try {
                            var data = $.parseJSON(message.data);
                            updateChart(data);
                        } catch (e) {
                            alert("Error parsing notificatoin: " + e);
                        }
                    };
                }
                else if ('MozWebSocket' in window) {
                    //Chat.socket = new MozWebSocket(host);
                    alert('Error: WebSocket is not supported by this browser. (Or maybe is but it is not yet implemented!)');
                } else {
                    alert('Error: WebSocket is not supported by this browser.');
                }
            }

            var dataLabels = [];
            var dataValues = [];
            function updateChart(newData) {

                dataLabels.push(newData.TIME % 100);
                dataValues.push(newData[$("#chartFields").val()]);

                if (dataValues.length > 100) {
                    dataLabels.shift();
                    dataValues.shift();
                }

                var data = {
                    labels: dataLabels,
                    datasets: [
                        {
                            fillColor: "rgba(220,220,220,0.5)",
                            strokeColor: "rgba(220,220,220,1)",
                            pointColor: "rgba(220,220,220,1)",
                            pointStrokeColor: "#fff",
                            data: dataValues
                        }
                    ]
                };

                dataChart.Line(data, optionsNoAnimation);
            }

            //]]>
        </script>
    </body>
</html>
