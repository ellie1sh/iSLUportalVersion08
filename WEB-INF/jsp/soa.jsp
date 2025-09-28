<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <link rel="stylesheet" type="text/css" href="../lib/bootstrap/css/bootstrap.min.css" media="screen">
    <link rel="stylesheet" type="text/css" href="../lib/css/fonts/ptsans/stylesheet.css" media="screen">
    <link rel="stylesheet" type="text/css" href="../lib/css/mws-style.css" media="screen">
    <link rel="stylesheet" type="text/css" href="../lib/css/fonts/icomoon/style.css" media="screen">
    <link rel="stylesheet" type="text/css" href="../lib/css/icons/icol16.css" media="screen">
    <link rel="stylesheet" type="text/css" href="../lib/css/icons/icol32.css" media="screen">
    <link rel="stylesheet" type="text/css" href="../lib/css/themer.css" media="screen">
    <link rel="stylesheet" type="text/css" href="../lib/css/spinner.css" media="screen">
    <link rel="stylesheet" type="text/css" href="../lib/plugins/jAlert/jAlert.css" media="screen">
    <link rel="stylesheet" type="text/css" href="../lib/jui/css/jquery.ui.all.css" media="screen">
    <link rel="stylesheet" type="text/css" href="../lib/jui/jquery-ui.custom.css" media="screen">
    <link rel="shortcut icon" href="../lib/images/favicon.ico">
    <title>iSLU Student Portal</title>
</head>
<body onload="" oncontextmenu="return false;">
    <div id="entirepageloader" style="width: 100%;height: 100%;background-color: #fff;position: absolute;z-index: 100000;opacity: .5; text-align:center; display:none;">
        <img id="loaderimg" src="../lib/images/default.gif" style="margin-top:25%;">
    </div>
    
    <!-- Header -->
    <div id="mws-header" class="clearfix">
        <div id="mws-logo-container">
            <a href=""><div id="mws-logo-wrap">
                <img src="../lib/images/ico1.png">
            </div></a>
        </div>
        <div id="mws-user-tools" class="clearfix">
            <div id="mws-user-info" class="mws-inset">
                <div id="mws-user-photo">
                    <img src="../lib/images/ungrdwoman.png" alt="User Photo">
                </div>
                <div id="mws-user-functions">
                    <div id="mws-username">
                        ${student.fullName}
                    </div>
                    <ul>
                        <li><a href="../logout">Logout</a></li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Main Wrapper -->
    <div id="mws-wrapper">
        <div id="mws-sidebar-stitch"></div>
        <div id="mws-sidebar-bg"></div>
        
        <!-- Sidebar -->
        <div id="mws-sidebar">
            <div id="mws-nav-collapse">
                <span></span>
                <span></span>
                <span></span>
            </div>
            <div id="mws-searchbox" class="mws-inset" style="text-align: center;">
                <l style="color: #fff;"><strong>${semester}</strong></l>
            </div>
            
            <!-- Navigation -->
            <div id="mws-navigation">
                <ul>
                    <li><a href="../students/home" onclick="$('#entirepageloader').css('display', 'block');"><i class="icon-home"></i> Home</a></li>
                    <li><a href="../students/classsched" onclick="$('#entirepageloader').css('display', 'block');"><i class="icon-table"></i> Schedule</a></li>
                    <li><a href="../students/attendance" onclick="$('#entirepageloader').css('display', 'block');"><i class="icon-pushpin"></i> Attendance</a></li>
                    <li class="active"><a href="../students/soa" onclick="$('#entirepageloader').css('display', 'block');"><i class="icon-calculate"></i> Statement of Accounts</a></li>
                    <li><a href="../students/grades" onclick="$('#entirepageloader').css('display', 'block');"><i class="icon-stats-up"></i> Grades</a></li>
                    <li><a href="../students/transcript" onclick="$('#entirepageloader').css('display', 'block');"><i class="icon-th-list"></i> Transcript of Records</a></li>
                    <li><a href="../students/checklist" onclick="$('#entirepageloader').css('display', 'block');"><i class="icon-ok-sign"></i> Curriculum Checklist</a></li>
                    <li><a href="../students/medical" onclick="$('#entirepageloader').css('display', 'block');"><i class="icon-google-plus"></i> Medical Record</a></li>
                    <li><a href="../students/profile" onclick="$('#entirepageloader').css('display', 'block');"><i class="icon-business-card"></i> Personal Details</a></li>
                    <li><a href="../searchjournalperiodical" target="_blank"><i class="icon-book"></i> Journal/Periodical</a></li>
                    <li><a href="../students/about" onclick="$('#entirepageloader').css('display', 'block');"><i class="icon-info-sign"></i> Downloadables /<br>About iSLU</a></li>
                </ul>
            </div>
        </div>
        
        <!-- Main Content -->
        <div id="mws-container" class="clearfix">
            <div class="container">
                <table width="100%">
                    <tbody>
                        <tr>
                            <td width="70%" valign="top">
                                <!-- Statement Panel -->
                                <div class="mws-panel grid_12" style="box-shadow: 0px 0px 0px 0px rgba(0, 0, 0, 0);">
                                    <div class="mws-panel-header">
                                        <span><i class="icon-pie-chart-2"></i> Statement of Accounts (${semester})</span>
                                    </div>
                                    <div class="mws-panel-body" style="background-color: #fff;">
                                        <!-- Student Info -->
                                        <div class="mws-stat-container clearfix">
                                            <a class="mws-stat" href="#">
                                                <span class="mws-stat-icon icol32-user-female"></span>
                                                <span class="mws-stat-content">
                                                    <span class="mws-stat-title">${student.id} | BSIT 2</span>
                                                    <span class="mws-stat-value">${student.fullName}</span>
                                                </span>
                                            </a>
                                        </div>
                                        
                                        <!-- Amount Due Display -->
                                        <span style="font-size: 30px;">Your amount due for <strong>${examInfo.currentPeriod}</strong> is:</span><br><br>
                                        <strong style="font-size: 50px;">P </strong>
                                        <strong style="font-size: 50px; color: ${examInfo.amountDue > 0 ? '#901818' : '#009900'};">
                                            <fmt:formatNumber value="${examInfo.amountDue}" type="number" pattern="#,##0.00"/>
                                        </strong><br><br>
                                        
                                        <!-- Remaining Balance -->
                                        <span style="font-size: 20px;">Your remaining balance as of <strong>${currentDate}</strong> is:</span><br><br>
                                        <strong style="font-size: 50px;">P </strong>
                                        <strong style="font-size: 50px; color: ${statement.balance > 0 ? '#901818' : '#009900'};">
                                            <fmt:formatNumber value="${statement.balance}" type="number" pattern="#,##0.00"/>
                                        </strong>
                                    </div>
                                    <br>
                                    
                                    <!-- Status Message -->
                                    <c:choose>
                                        <c:when test="${examInfo.isPaid}">
                                            <b style="color: green;">${examInfo.currentPeriod} STATUS: PAID. Permitted to take the exams.</b>
                                        </c:when>
                                        <c:otherwise>
                                            <b style="color: red;">${examInfo.currentPeriod} STATUS: NOT PAID. Please pay before ${examInfo.currentPeriod.toLowerCase()} exams. Ignore if you're SLU Dependent or Full TOF Scholar.</b>
                                        </c:otherwise>
                                    </c:choose>
                                    <br>
                                    
                                    <!-- Payment verification note -->
                                    <small style="color: #666;">For verification on unposted payments after 'as of' date, please email <a href="mailto:sass@slu.edu.ph">sass@slu.edu.ph</a></small>
                                </div>
                                
                                <!-- Fee Breakdown Panel -->
                                <div class="mws-panel grid_12" style="box-shadow: 0px 0px 0px 0px rgba(0, 0, 0, 0); margin-bottom: 5px;">
                                    <div class="mws-panel-header">
                                        <span><i class="icon-th-list"></i> Breakdown of fees as of <strong>${currentDate}</strong></span>
                                    </div>
                                    <div class="mws-panel-body" style="padding: 0px;">
                                        <table class="mws-table">
                                            <thead>
                                                <tr>
                                                    <th>Date</th>
                                                    <th>Description</th>
                                                    <th>Amount</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                                <!-- Beginning Balance -->
                                                <tr>
                                                    <td width="70"><i></i></td>
                                                    <td>BEGINNING BALANCE</td>
                                                    <td align="right" width="70"><b>0.00</b></td>
                                                </tr>
                                                
                                                <!-- Payment History -->
                                                <c:forEach var="payment" items="${statement.paymentHistory}">
                                                    <tr>
                                                        <td width="70"><i>${payment.date}</i></td>
                                                        <td>PAYMENT RECEIVED (${payment.reference})</td>
                                                        <td align="right" width="70">
                                                            <b>(<fmt:formatNumber value="${payment.amount.replace('P ', '').replace(',', '')}" type="number" pattern="#,##0.00"/>)</b>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                                
                                                <!-- Fee Breakdown -->
                                                <c:forEach var="fee" items="${statement.feeBreakdowns}">
                                                    <tr>
                                                        <td width="70">
                                                            <i><fmt:formatDate value="${fee.datePosted}" pattern="MM/dd/yyyy"/></i>
                                                        </td>
                                                        <td>${fee.description}</td>
                                                        <td align="right" width="70">
                                                            <b>
                                                                <c:choose>
                                                                    <c:when test="${fee.amount < 0}">
                                                                        (<fmt:formatNumber value="${-fee.amount}" type="number" pattern="#,##0.00"/>)
                                                                    </c:when>
                                                                    <c:otherwise>
                                                                        <fmt:formatNumber value="${fee.amount}" type="number" pattern="#,##0.00"/>
                                                                    </c:otherwise>
                                                                </c:choose>
                                                            </b>
                                                        </td>
                                                    </tr>
                                                </c:forEach>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                                
                                <!-- Hidden elements for modal functionality -->
                                <div id="modalform-btn"></div>
                                <a id="addsubjform-btn"></a>
                            </td>
                            
                            <!-- Right Panel - Payment Channels -->
                            <td width="30%" valign="top" style="padding-left: 20px;">
                                <div class="mws-panel grid_12" style="box-shadow: 0px 0px 0px 0px rgba(0, 0, 0, 0);">
                                    <div class="mws-panel-header">
                                        <span><i class="icon-shopping-cart"></i> Online Payment Channels</span>
                                    </div>
                                    <div class="mws-panel-body">
                                        <table style="width: 100%;">
                                            <tbody>
                                                <tr>
                                                    <td style="text-align: center;">
                                                        <b style="font-size: 18px; color: #0e284f;">Tuition fees can be paid via the available online payment channels.</b>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="text-align: center;">
                                                        <hr>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="text-align: center;">
                                                        <img src="../accstatement/upay.png" style="cursor: pointer;" id="upybtn">
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="text-align: center;">
                                                        <img src="../accstatement/dragonpay.png" style="cursor: pointer;" id="dgpbtn">
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="text-align: center;">
                                                        <img src="../accstatement/bpi.png" style="cursor: pointer;" id="bpibtn">
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="text-align: center;">
                                                        <img src="../accstatement/bdo.png" style="cursor: pointer;" id="bdobtnn">
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="text-align: center;">
                                                        <img src="../accstatement/bdobills.png" style="cursor: pointer;" id="bdobtn">
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="text-align: center;">
                                                        <img src="../accstatement/bukas.png" style="cursor: pointer;" id="bksbtn">
                                                    </td>
                                                </tr>
                                            </tbody>
                                        </table>
                                    </div>
                                    <br>
                                </div>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
            
            <!-- Footer -->
            <div id="mws-footer">
                Copyright © <script>document.write(new Date().getFullYear())</script> <strong>Saint Louis University Inc.</strong> All rights reserved.
            </div>
        </div>
    </div>
    
    <!-- Modal Dialog -->
    <div id="modalform" title="Form" style="display: none;">
        <div id="modalformloader" style="width: 100%; height: 100%; background-color: #f8f8f8; position: absolute; z-index: 100000; opacity: .5; text-align: center; display: none;">
            <img id="divloaderimg" src="../lib/images/default.gif" style="position: absolute; top: 50%; left: 50%;">
        </div>
        <div id="modalform-content" class="mws-panel-body no-padding"></div>
    </div>
    
    <!-- JavaScript Libraries -->
    <script src="../lib/js/libs/jquery-1.8.3.min.js"></script>
    <script src="../lib/bootstrap/js/bootstrap.min.js"></script>
    <script src="../lib/js/core/mws.js"></script>
    <script src="../lib/plugins/jAlert/jAlert.js"></script>
    <script src="../lib/plugins/jAlert/jAlert-functions.js"></script>
    <script src="../lib/jui/js/jquery-ui-1.9.2.min.js"></script>
    <script src="../lib/jui/jquery-ui.custom.min.js"></script>
    
    <!-- Modal Initialization Script -->
    <script>
    ;(function($, window, document, undefined) {
        $(document).ready(function() {
            if($.fn.dialog) {
                $("#modalform").dialog({
                    resizable: false,
                    autoOpen: false,
                    title: 'Form',
                    modal: true,
                    width: "600",
                    closeOnEscape: false
                });
                $("#modalform-btn").bind("click", function(event) {
                    $("#modalform").dialog("option", {
                        modal: true
                    }).dialog("open");
                    event.preventDefault();
                });
            }
        });
    })(jQuery, window, document);
    </script>
    
    <script src="../lib/plugins/validate/jquery.validate-min.js"></script>
    
    <!-- Security Scripts -->
    <script>
    document.addEventListener('keydown', function(event) {
        if (event.key === "F12" || 
            (event.ctrlKey && event.shiftKey && (event.key === "I" || event.key === "J" || event.key === "C")) || 
            (event.ctrlKey && event.key === "U")) {
            event.preventDefault();
            return false;
        }
    });
    </script>
    
    <script>
    let element = new Image();
    Object.defineProperty(element, 'id', {
        get: function() {
            document.body.innerHTML = "<h1>Developer tools are not allowed!</h1>";
        }
    });
    console.log(element);
    </script>
    
    <script>
    document.addEventListener("contextmenu", function(e) {
        e.preventDefault();
    }, false);

    document.onkeydown = function(e) {
        // F12
        if(e.keyCode == 123) return false;
        // Ctrl+Shift+I
        if(e.ctrlKey && e.shiftKey && e.keyCode == 73) return false;
        // Ctrl+Shift+J
        if(e.ctrlKey && e.shiftKey && e.keyCode == 74) return false;
        // Ctrl+U
        if(e.ctrlKey && e.keyCode == 85) return false;
    }
    </script>
    
    <!-- Payment Gateway Scripts -->
    <script>
    // DragonPay Payment
    $('#dgpbtn').click(function() {
        $('html,body').animate({scrollTop: 0}, 700);
        $("#modalform").dialog({buttons: []});
        $('#modalformloader').css('display', 'block');
        $('#modalform-content').html('');
        $('#modalform-btn').click();
        $('#modalform').dialog({title: "Payment through Dragon Pay"});
        $('#modalform').dialog('widget').attr('id', 'floatingform');
        $('#floatingform').css('top', '80px');
        $('#floatingform').css('width', '500px');
        $('#floatingform').css('left', ((window.innerWidth / 2) - 250) + 'px');
        $('#modalform').css('padding', '0px');
        $.ajax({
            url: '?showdragon=1&cidno=${student.id}',
            success: function(result) {
                $('body').append('<script id="vinzscript">' + result + '<' + '/' + 'script>');
                $('#vinzscript').remove();
            }
        });
    });
    
    // UPay Payment
    $('#upybtn').click(function() {
        $('html,body').animate({scrollTop: 0}, 700);
        $("#modalform").dialog({buttons: []});
        $('#modalformloader').css('display', 'block');
        $('#modalform-content').html('');
        $('#modalform-btn').click();
        $('#modalform').dialog({title: "Payment through UPay by UnionBank"});
        $('#modalform').dialog('widget').attr('id', 'floatingform');
        $('#floatingform').css('top', '80px');
        $('#floatingform').css('width', '500px');
        $('#floatingform').css('left', ((window.innerWidth / 2) - 250) + 'px');
        $('#modalform').css('padding', '0px');
        $.ajax({
            url: '?showupay=1&cidno=${student.id}',
            success: function(result) {
                $('body').append('<script id="vinzscript">' + result + '<' + '/' + 'script>');
                $('#vinzscript').remove();
            }
        });
    });
    
    // BDO Online Payment
    $('#bdobtnn').click(function() {
        $('html,body').animate({scrollTop: 0}, 700);
        $("#modalform").dialog({buttons: []});
        $('#modalformloader').css('display', 'block');
        $('#modalform-content').html('');
        $('#modalform-btn').click();
        $('#modalform').dialog({title: "Payment through BDO Online"});
        $('#modalform').dialog('widget').attr('id', 'floatingform');
        $('#floatingform').css('top', '80px');
        $('#floatingform').css('width', '500px');
        $('#floatingform').css('left', ((window.innerWidth / 2) - 250) + 'px');
        $('#modalform').css('padding', '0px');
        $.ajax({
            url: '?showbdool=1&cidno=${student.id}',
            success: function(result) {
                $('body').append('<script id="vinzscript">' + result + '<' + '/' + 'script>');
                $('#vinzscript').remove();
            }
        });
    });
    
    // BDO Bills Payment
    $('#bdobtn').click(function() {
        $('html,body').animate({scrollTop: 0}, 700);
        $("#modalform").dialog({buttons: []});
        $('#modalformloader').css('display', 'block');
        $('#modalform-content').html('');
        $('#modalform-btn').click();
        $('#modalform').dialog({title: "Payment through BDO Bills Payment"});
        $('#modalform').dialog('widget').attr('id', 'floatingform');
        $('#floatingform').css('top', '80px');
        $('#floatingform').css('width', '600px');
        $('#floatingform').css('left', ((window.innerWidth / 2) - 300) + 'px');
        $('#modalform').css('padding', '0px');
        $.ajax({
            url: '?showbdobtn=1&cidno=${student.id}',
            success: function(result) {
                $('body').append('<script id="vinzscript">' + result + '<' + '/' + 'script>');
                $('#vinzscript').remove();
            }
        });
    });
    
    // BPI Payment
    $('#bpibtn').click(function() {
        $('html,body').animate({scrollTop: 0}, 700);
        $("#modalform").dialog({buttons: []});
        $('#modalformloader').css('display', 'block');
        $('#modalform-content').html('');
        $('#modalform-btn').click();
        $('#modalform').dialog({title: "Payment through BPI"});
        $('#modalform').dialog('widget').attr('id', 'floatingform');
        $('#floatingform').css('top', '80px');
        $('#floatingform').css('width', '500px');
        $('#floatingform').css('left', ((window.innerWidth / 2) - 250) + 'px');
        $('#modalform').css('padding', '0px');
        $.ajax({
            url: '?showbpi=1&cidno=${student.id}',
            success: function(result) {
                $('body').append('<script id="vinzscript">' + result + '<' + '/' + 'script>');
                $('#vinzscript').remove();
            }
        });
    });
    
    // Bukas Payment (External Link)
    $('#bksbtn').on('click', function(e) {
        window.open('https://bukas.ph/s/slu', '_blank');
    });
    
    // Utility functions for payment processing
    function hexStringToArrayBuffer(hexString) {
        var result = new Uint8Array(hexString.length / 2);
        for (var i = 0; i < hexString.length; i += 2) {
            result[i / 2] = parseInt(hexString.substr(i, 2), 16);
        }
        return result;
    }

    function arrayBufferToBase64(buffer) {
        var binary = '';
        var bytes = new Uint8Array(buffer);
        var len = bytes.byteLength;
        for (var i = 0; i < len; i++) {
            binary += String.fromCharCode(bytes[i]);
        }
        return window.btoa(binary);
    }

    function concatBuffers(buffer1, buffer2) {
        var tmp = new Uint8Array(buffer1.byteLength + buffer2.byteLength);
        tmp.set(new Uint8Array(buffer1), 0);
        tmp.set(new Uint8Array(buffer2), buffer1.byteLength);
        return tmp.buffer;
    }

    // Window focus/blur handling
    var blurred = false;
    window.onfocus = function() {
        blurred && (location.reload());
    };
    
    function parseJwt(token) {
        var base64Url = token.split('.')[1];
        var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        var jsonPayload = decodeURIComponent(window.atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));
        return JSON.stringify(JSON.parse(jsonPayload));
    }
    
    // Auto-refresh for payment status updates
    setInterval(function() {
        // Check for payment status updates every 30 seconds
        $.ajax({
            url: '../students/soa?action=checkPaymentStatus',
            type: 'GET',
            success: function(data) {
                if (data.updated) {
                    location.reload();
                }
            },
            error: function() {
                // Silent fail
            }
        });
    }, 30000);
    </script>
    
    <!-- Spinner styles -->
    <style>
    input::-webkit-outer-spin-button,
    input::-webkit-inner-spin-button {
        -webkit-appearance: none;
        margin: 0;
    }

    input[type=number] {
        -moz-appearance: textfield;
    }
    </style>
</body>
</html>