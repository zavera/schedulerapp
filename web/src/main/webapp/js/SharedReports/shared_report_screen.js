/*
 * Copyright (c) 2015-2016, President and Fellows of Harvard College
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


var renderSharedReportPage = (function () {
    var initFn = function () {

            commonInit();

    };


    function commonInit() {
        loadMetaHeaders();

        initFooter();
        eraseLicense();
        renderSharedReportData();
    }

    return {
        init: initFn
    };
}());


function sharedReportData() {
    commonData();
    renderBreadcrumbs('shared_report_screen');
    report_renderSharedReportGrid();
}




function createSharedReportTemplateDatePickers() {
    WidgetUtil.createDatepicker("#report_start_date", {
        onSelect: function (selectedDate) {
            $("#report_end_date").datepicker("option", "minDate", selectedDate);
        },
        onClose: function (dateText, inst) {
            try {
                var selectedDate = $.datepicker.parseDate('mm/dd/yy', dateText);
                $("#report_end_date").datepicker("option", "minDate", selectedDate);
            } catch (e) {
                alert("Incorrect Date format. It should be MM/DD/YYYY.");
                $("#report_start_date").val('');
                return;
            }
        }
    });

    WidgetUtil.createDatepicker("#report_end_date", {
        onClose: function (dateText, inst) {
            try {
                $.datepicker.parseDate('mm/dd/yy', dateText);
            } catch (e) {
                alert("Incorrect Date format. It should be MM/DD/YYYY.");
                $("#report_end_date").val('');
            }
        }
    });
    var startDate = new Date();
    startDate.setDate(1);
    startDate.setMonth(startDate.getMonth() - 1);
    var endDate = new Date(); // current date
    endDate.setDate(1); // going to 1st of the month
    endDate.setHours(-1);
    $("#report_start_date").datepicker('setDate', startDate);
    $("#report_end_date").datepicker('setDate', endDate);
    $("#report_end_date").datepicker("option", "minDate", startDate);
    $('#ui-datepicker-div').hide();
}


function renderSharedReportData(){
    commonData();
    var reportJson = sessionStorage.getItem("sharedReportTemplate");
    sharedReport_selectedReport = JSON.parse(reportJson);
    renderSharedReportBreadcrumbs(sharedReport_selectedReport.name);
    $('#sharedReport_reportTitle').html(sharedReport_selectedReport.name);
    if( sharedReport_selectedReport.dateBounded == true){

        $('#reportDateRangeContainer').css({display: "block"});
        createSharedReportTemplateDatePickers();
        }
    else{
        $('#dateBoundMsgCell').css({display: "block"});
    }






}








var form = document.createElement("form");
var hiddenField = document.createElement("input");

function exportTemplateToExcel() {
    setTimeout(function () {

        selectedStartDate = '';
        selectedEndDate = '';


        if ( sharedReport_selectedReport.dateBounded == true) {
            selectedStartDate = $('#report_start_date').datepicker('getDate');
            selectedEndDate = $('#report_end_date').datepicker('getDate');

            if ($('#reportStartDate').datepicker('getDate') == null || $('#reportEndDate').datepicker('getDate') == null) {
                alert("Please select a valid date range!");
                return;
            }
            else {
                if (selectedStartDate > selectedEndDate) {
                    $("#reportEndDate").datepicker("option", "minDate", selectedStartDate);
                    alert("Please select a valid date range!");
                    return;
                }
            }
            selectedEndDate.setHours(23, 59, 59, 59);
        }



            let selectedReportId = sharedReport_selectedReport.reportId;
            startDate = selectedStartDate != '' ? selectedStartDate.valueOf() : null;
            endDate =  selectedEndDate != '' ? selectedEndDate.valueOf() : null;
            selectedUserReportId  = sessionStorage.getItem("sharedReportTemplateId");

            var actionUrl = '';
            if(startDate == null && endDate == null){
                 actionUrl = 'rest/reports/sharedTemplates?id=' + selectedReportId + '&type=Shared&templateUserId=' + selectedUserReportId;
            }
            else {
                 actionUrl = 'rest/reports/sharedTemplates?startDate=' + startDate + '&endDate=' + endDate + '&id='+ selectedReportId + '&type=Shared&templateUserId=' + selectedUserReportId;

            }
        document.body.appendChild(form);
        form.appendChild(hiddenField);

        form.setAttribute("method", "post");
        form.setAttribute("action", actionUrl);

        hiddenField.style.display = "none";
        hiddenField.setAttribute("name", "data");
        hiddenField.setAttribute("value", '');

        form.submit();

       // $.post(actionUrl, {data: jsonData}, function (data) {console.log(data)})


    }, 100);
}





function getSharedReportSelectedRowId(id) {
    $.getJSON("rest/reports/sharedTemplates/" + id, function (data) {
        sessionStorage.setItem("sharedReportTemplate", JSON.stringify(data));
        sessionStorage.setItem("sharedReportTemplateId" , id);
        window.location.href = "shared_report_screen.html";



    });
}





function report_renderSharedReportGrid() {
    commonData();
    $.getJSON("rest/reports/sharedTemplates", function (data) {
        var iteration = 0;
        var out =
            " <table id='sharedReports' class='hoverable'>" +
            "  <tr>" +
            "   <td><strong>Name</strong></td> " +
            "   <td><strong>User</strong></td> " +

            "   <td><strong>Last Update</strong></td>" +
            "  </tr> ";

        if(data.length == 0){
            out += "<tr><td><label> No Shared Reports </label> </td></tr>"
        }

        $.each(data, function () {
            out += "<tr onclick='getSharedReportSelectedRowId(" + this.id + ")'>"
            out += "   <td>" + this.reportTemplateName + "</td>" +

                "       <td>" + this.ecommons + "</td>" +

                "      <td>" + this.latestUpdate + "</td>" +
                " </tr>";
            iteration++;
        });

        out += "  </table>";

        $('#sharedReportDataTable').html(out);
    });
}