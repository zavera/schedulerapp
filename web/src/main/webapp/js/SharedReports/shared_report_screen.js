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


var reportPage = (function () {
    var initFn = function () {
        $.get("rest/app/getStaticDataForStandardReportPage", function (data) {
            var parsedData = $.parseJSON(data);
            var studyStatusFilterValues = parsedData.studyStatusFilterValues;
            // populate the study status menu options based on data received from the server
            // These menu options are used in the Study Data report
            studyStatusFilterOptions = buildSelectOptions(studyStatusFilterValues, 'label');
            commonInit();
        });
    };

    function commonInit() {
        loadMetaHeaders();
        renderReportData();
        initFooter();
        eraseLicense();
        app_runIdleTimer();
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




function report_renderSharedReportGrid() {
    commonData();
    $.getJSON("rest/reports/sharedTemplates", function (data) {
        var iteration = 0;

        var out =
            " <table id='sharedReports' class='hoverable'>" +
            "  <tr>" +
            "   <td><strong>Name</strong></td> " +

            "   <td><strong>Last Update</strong></td>" +
            "  </tr> ";

        $.each(data, function () {
            out += "<tr onclick='getSharedReportSelectedRowId(" + this.id + ")'>"
            out += "   <td>" + this.reportTemplateName + "</td>" +

                "      <td>" + this.latestUpdate + "</td>" +
                " </tr>";
            iteration++;
        });

        out += "  </table>";

        $('#sharedReportDataTable').html(out);
    });
}