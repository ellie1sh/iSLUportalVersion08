import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import com.google.gson.Gson;

/**
 * Servlet for handling Statement of Accounts requests
 * Handles display, payment processing, and payment gateway integrations
 */
@WebServlet("/students/soa")
public class StatementOfAccountsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Gson gson = new Gson();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("studentID") == null) {
            response.sendRedirect("../login");
            return;
        }
        
        String studentID = (String) session.getAttribute("studentID");
        String action = request.getParameter("action");
        
        // Handle payment gateway displays
        if (action != null) {
            handlePaymentGatewayRequest(request, response, studentID, action);
            return;
        }
        
        // Handle AJAX requests for payment processing
        String showdragon = request.getParameter("showdragon");
        String showupay = request.getParameter("showupay");
        String showbdobtn = request.getParameter("showbdobtn");
        String showbdool = request.getParameter("showbdool");
        String showbpi = request.getParameter("showbpi");
        
        if (showdragon != null) {
            handleDragonPayDisplay(request, response, studentID);
            return;
        }
        
        if (showupay != null) {
            handleUPayDisplay(request, response, studentID);
            return;
        }
        
        if (showbdobtn != null) {
            handleBDOBillsDisplay(request, response, studentID);
            return;
        }
        
        if (showbdool != null) {
            handleBDOOnlineDisplay(request, response, studentID);
            return;
        }
        
        if (showbpi != null) {
            handleBPIDisplay(request, response, studentID);
            return;
        }
        
        // Default: Display Statement of Accounts
        displayStatementOfAccounts(request, response, studentID);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("studentID") == null) {
            response.sendRedirect("../login");
            return;
        }
        
        String studentID = (String) session.getAttribute("studentID");
        
        // Handle payment processing
        String paymentType = request.getParameter("paymentType");
        if (paymentType != null) {
            processPayment(request, response, studentID, paymentType);
            return;
        }
        
        // Handle Dragon Pay payment
        String checkdragonpayment = request.getParameter("checkdragonpayment");
        if (checkdragonpayment != null) {
            processDragonPayPayment(request, response, studentID);
            return;
        }
        
        // Handle BDO payment
        String checkbdopayment = request.getParameter("checkbdopayment");
        if (checkbdopayment != null) {
            processBDOPayment(request, response, studentID);
            return;
        }
        
        // Handle UPay payment
        String checkupaypayment = request.getParameter("checkupaypayment");
        if (checkupaypayment != null) {
            processUPayPayment(request, response, studentID);
            return;
        }
        
        // Handle BPI payment
        String checkbpipayment = request.getParameter("checkbpipayment");
        if (checkbpipayment != null) {
            processBPIPayment(request, response, studentID);
            return;
        }
        
        // Default: redirect to GET
        doGet(request, response);
    }
    
    private void displayStatementOfAccounts(HttpServletRequest request, HttpServletResponse response, String studentID) 
            throws ServletException, IOException {
        
        // Get student information
        StudentInfo student = DataManager.getStudentById(studentID);
        if (student == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Student not found");
            return;
        }
        
        // Get account statement
        AccountStatement statement = AccountStatementManager.getStatement(studentID);
        
        // Calculate current exam period and amounts due
        ExamPeriodInfo examInfo = calculateExamPeriodInfo(statement);
        
        // Set request attributes
        request.setAttribute("student", student);
        request.setAttribute("statement", statement);
        request.setAttribute("examInfo", examInfo);
        request.setAttribute("currentDate", LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
        request.setAttribute("semester", "FIRST SEMESTER, 2025-2026");
        
        // Forward to JSP
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/jsp/soa.jsp");
        dispatcher.forward(request, response);
    }
    
    private ExamPeriodInfo calculateExamPeriodInfo(AccountStatement statement) {
        ExamPeriodInfo info = new ExamPeriodInfo();
        LocalDate currentDate = LocalDate.now();
        
        // Determine current exam period based on date
        if (currentDate.isBefore(LocalDate.of(2025, 10, 15))) {
            info.currentPeriod = "PRELIM";
            info.amountDue = statement.getExamPeriodDue(AccountStatement.ExamPeriod.PRELIM);
            info.isPaid = statement.isPrelimPaid();
            info.statusMessage = statement.getExamEligibilityMessage(AccountStatement.ExamPeriod.PRELIM);
        } else if (currentDate.isBefore(LocalDate.of(2025, 11, 30))) {
            info.currentPeriod = "MIDTERM";
            info.amountDue = statement.getExamPeriodDue(AccountStatement.ExamPeriod.MIDTERM);
            info.isPaid = statement.isMidtermPaid();
            info.statusMessage = statement.getExamEligibilityMessage(AccountStatement.ExamPeriod.MIDTERM);
        } else {
            info.currentPeriod = "FINALS";
            info.amountDue = statement.getExamPeriodDue(AccountStatement.ExamPeriod.FINALS);
            info.isPaid = statement.isFinalsPaid();
            info.statusMessage = statement.getExamEligibilityMessage(AccountStatement.ExamPeriod.FINALS);
        }
        
        return info;
    }
    
    private void handleDragonPayDisplay(HttpServletRequest request, HttpServletResponse response, String studentID) 
            throws ServletException, IOException {
        
        AccountStatement statement = AccountStatementManager.getStatement(studentID);
        ExamPeriodInfo examInfo = calculateExamPeriodInfo(statement);
        
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        
        // Generate DragonPay form JavaScript
        StringBuilder js = new StringBuilder();
        js.append("$('#modalformloader').css('display', 'none');\n");
        js.append("$('#modalform-content').html(`\n");
        js.append("<form method='post' id='frmpay'>\n");
        js.append("<div class='mws-form'>\n");
        js.append("<input type='hidden' name='checkdragonpayment' value='vinz'>\n");
        js.append("<input type='hidden' name='txtdpidno' value='" + studentID + "'>\n");
        js.append("<input type='hidden' id='txtcpno' name='txtcpno' value='09665295444'>\n");
        js.append("<input type='hidden' id='dexam' name='dexam' value='1'>\n");
        js.append("<input type='hidden' id='dpfamt' name='dpfamt' value=''>\n");
        js.append("<input type='hidden' id='dpamt' value='" + examInfo.amountDue + "'>\n");
        js.append("<input type='hidden' id='txtidno' name='txtidno' value='" + studentID + "'>\n");
        js.append("<fieldset class='mws-form-inline'>\n");
        js.append("<legend><table width='100%'><tr><td><b>AMOUNT TO PAY</b></td></tr></table></legend>\n");
        js.append("<div style='text-align: center; padding-top: 20px; padding-bottom: 20px;'>\n");
        js.append("<input type='number' step='any' id='txtdamount' name='txtdamount' value='" + (int)examInfo.amountDue + "' ");
        js.append("style='text-align:center; font-size: 30px; width: 300px; height: 50px; color: #0e284f;'>\n");
        js.append("</div>\n");
        
        // Add payment method dropdown
        js.append("<div style='text-align: center; padding-bottom: 20px;'>\n");
        js.append("<select id='paymentMethod' name='paymentMethod' style='width: 300px; height: 40px; font-size: 16px;'>\n");
        js.append("<option value='GCash'>GCash</option>\n");
        js.append("<option value='PayMaya'>PayMaya</option>\n");
        js.append("<option value='BPI'>BPI Online</option>\n");
        js.append("<option value='BDO'>BDO Online</option>\n");
        js.append("<option value='UnionBank'>UnionBank Online</option>\n");
        js.append("<option value='Metrobank'>Metrobank Online</option>\n");
        js.append("<option value='RCBC'>RCBC Online</option>\n");
        js.append("<option value='SecurityBank'>Security Bank Online</option>\n");
        js.append("<option value='PNB'>PNB Online</option>\n");
        js.append("<option value='LandBank'>LandBank Online</option>\n");
        js.append("<option value='DBP'>DBP Online</option>\n");
        js.append("<option value='Chinabank'>China Bank Online</option>\n");
        js.append("<option value='PSBank'>PSBank Online</option>\n");
        js.append("<option value='Robinsons'>Robinsons Bank Online</option>\n");
        js.append("<option value='UCPB'>UCPB Online</option>\n");
        js.append("<option value='EastWest'>EastWest Online</option>\n");
        js.append("<option value='7Eleven'>7-Eleven</option>\n");
        js.append("<option value='Cebuana'>Cebuana Lhuillier</option>\n");
        js.append("<option value='MLhuillier'>M Lhuillier</option>\n");
        js.append("<option value='ECPay'>ECPay</option>\n");
        js.append("<option value='Bayad'>Bayad Center</option>\n");
        js.append("</select>\n");
        js.append("</div>\n");
        
        // Add service charge note
        js.append("<div id='notecontainer' style='padding-left: 20px;'>\n");
        js.append("<p style='color: red; font-size: 12px;'>\n");
        js.append("Note: There will be a twenty five pesos (P 25.00) service charge for using dragon pay.<br>\n");
        js.append("An additional fee will be charged depending on the payment channel.\n");
        js.append("</p>\n");
        js.append("</div>\n");
        
        // Calculate total with service charge
        double totalWithCharge = examInfo.amountDue + 25.00;
        js.append("<fieldset class='mws-form-inline'>\n");
        js.append("<legend><table width='100%'><tr><td><b>AMOUNT TO PAY + CHARGES</b></td></tr></table></legend>\n");
        js.append("<div style='text-align: center; padding-top: 20px; padding-bottom: 20px;'>\n");
        js.append("<input type='text' id='txttotalamount' name='txttotalamount' value='" + String.format("%.2f", totalWithCharge) + "' ");
        js.append("readonly style='text-align:center; font-size: 30px; width: 300px; height: 50px; color: #0e284f; background-color: #f0f0f0;'>\n");
        js.append("</div>\n");
        js.append("</fieldset>\n");
        
        js.append("<input type='submit' value='s' style='opacity: 0;'>\n");
        js.append("</div>\n");
        js.append("</form>\n");
        js.append("`);\n");
        
        // Add button functionality
        js.append("$('#floatingform .ui-dialog-buttonpane').html(`\n");
        js.append("<div class='ui-dialog-buttonset'>\n");
        js.append("<button type='button' id='btnproceed' class='ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only'>\n");
        js.append("<span class='ui-button-text'>Proceed</span>\n");
        js.append("</button>\n");
        js.append("</div>\n");
        js.append("`);\n");
        
        // Add event handlers
        js.append("$('#txtdamount').on('input', function() {\n");
        js.append("    var amount = parseFloat($(this).val()) || 0;\n");
        js.append("    var total = amount + 25.00;\n");
        js.append("    $('#txttotalamount').val(total.toFixed(2));\n");
        js.append("});\n");
        
        js.append("$('#btnproceed').click(function() {\n");
        js.append("    var amount = parseFloat($('#txtdamount').val());\n");
        js.append("    if (amount <= 0) {\n");
        js.append("        alert('Please enter a valid amount.');\n");
        js.append("        return;\n");
        js.append("    }\n");
        js.append("    $('#frmpay').submit();\n");
        js.append("});\n");
        
        out.print(js.toString());
    }
    
    private void handleUPayDisplay(HttpServletRequest request, HttpServletResponse response, String studentID) 
            throws ServletException, IOException {
        
        AccountStatement statement = AccountStatementManager.getStatement(studentID);
        ExamPeriodInfo examInfo = calculateExamPeriodInfo(statement);
        
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        
        StringBuilder js = new StringBuilder();
        js.append("$('#modalformloader').css('display', 'none');\n");
        js.append("$('#modalform-content').html(`\n");
        js.append("<form method='post' id='frmpay'>\n");
        js.append("<div class='mws-form'>\n");
        js.append("<input type='hidden' name='checkupaypayment' value='vinz'>\n");
        js.append("<input type='hidden' name='txtupayidno' value='" + studentID + "'>\n");
        js.append("<fieldset class='mws-form-inline'>\n");
        js.append("<legend><table width='100%'><tr><td><b>AMOUNT TO PAY</b></td></tr></table></legend>\n");
        js.append("<div style='text-align: center; padding-top: 20px; padding-bottom: 20px;'>\n");
        js.append("<input type='number' step='any' id='txtupayamount' name='txtupayamount' value='" + (int)examInfo.amountDue + "' ");
        js.append("style='text-align:center; font-size: 30px; width: 300px; height: 50px; color: #0e284f;'>\n");
        js.append("</div>\n");
        js.append("</fieldset>\n");
        js.append("<input type='submit' value='s' style='opacity: 0;'>\n");
        js.append("</div>\n");
        js.append("</form>\n");
        js.append("`);\n");
        
        js.append("$('#floatingform .ui-dialog-buttonpane').html(`\n");
        js.append("<div class='ui-dialog-buttonset'>\n");
        js.append("<button type='button' id='btnproceed' class='ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only'>\n");
        js.append("<span class='ui-button-text'>Proceed</span>\n");
        js.append("</button>\n");
        js.append("</div>\n");
        js.append("`);\n");
        
        js.append("$('#btnproceed').click(function() {\n");
        js.append("    var amount = parseFloat($('#txtupayamount').val());\n");
        js.append("    if (amount <= 0) {\n");
        js.append("        alert('Please enter a valid amount.');\n");
        js.append("        return;\n");
        js.append("    }\n");
        js.append("    $('#frmpay').submit();\n");
        js.append("});\n");
        
        out.print(js.toString());
    }
    
    private void handleBDOOnlineDisplay(HttpServletRequest request, HttpServletResponse response, String studentID) 
            throws ServletException, IOException {
        
        AccountStatement statement = AccountStatementManager.getStatement(studentID);
        ExamPeriodInfo examInfo = calculateExamPeriodInfo(statement);
        
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        
        StringBuilder js = new StringBuilder();
        js.append("$('#modalformloader').css('display', 'none');\n");
        js.append("$('#modalform-content').html(`\n");
        js.append("<form method='post' id='frmpay'>\n");
        js.append("<div class='mws-form'>\n");
        js.append("<input type='hidden' name='checkbdopayment' value='vinz'>\n");
        js.append("<input type='hidden' name='txtbdoidno' value='" + studentID + "'>\n");
        js.append("<fieldset class='mws-form-inline'>\n");
        js.append("<legend><table width='100%'><tr><td><b>AMOUNT TO PAY</b></td></tr></table></legend>\n");
        js.append("<div style='text-align: center; padding-top: 20px; padding-bottom: 20px;'>\n");
        js.append("<input type='number' step='any' id='txtbdoamount' name='txtbdoamount' value='" + (int)examInfo.amountDue + "' ");
        js.append("style='text-align:center; font-size: 30px; width: 300px; height: 50px; color: #0e284f;'>\n");
        js.append("</div>\n");
        js.append("</fieldset>\n");
        js.append("<input type='submit' value='s' style='opacity: 0;'>\n");
        js.append("</div>\n");
        js.append("</form>\n");
        js.append("`);\n");
        
        js.append("$('#floatingform .ui-dialog-buttonpane').html(`\n");
        js.append("<div class='ui-dialog-buttonset'>\n");
        js.append("<button type='button' id='btnproceed' class='ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only'>\n");
        js.append("<span class='ui-button-text'>Proceed</span>\n");
        js.append("</button>\n");
        js.append("</div>\n");
        js.append("`);\n");
        
        js.append("$('#btnproceed').click(function() {\n");
        js.append("    var amount = parseFloat($('#txtbdoamount').val());\n");
        js.append("    if (amount <= 0) {\n");
        js.append("        alert('Please enter a valid amount.');\n");
        js.append("        return;\n");
        js.append("    }\n");
        js.append("    $('#frmpay').submit();\n");
        js.append("});\n");
        
        out.print(js.toString());
    }
    
    private void handleBDOBillsDisplay(HttpServletRequest request, HttpServletResponse response, String studentID) 
            throws ServletException, IOException {
        
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        
        StringBuilder js = new StringBuilder();
        js.append("$('#modalformloader').css('display', 'none');\n");
        js.append("$('#modalform-content').html(`\n");
        js.append("<div class='mws-form' style='padding: 20px;'>\n");
        js.append("<h3 style='text-align: center; color: #0e284f;'>BDO Bills Payment Instructions</h3>\n");
        js.append("<div style='text-align: left; line-height: 1.6;'>\n");
        js.append("<p><strong>To pay via BDO Bills Payment:</strong></p>\n");
        js.append("<ol>\n");
        js.append("<li>Visit any BDO branch or use BDO Online Banking</li>\n");
        js.append("<li>Select 'Bills Payment'</li>\n");
        js.append("<li>Choose 'Educational' category</li>\n");
        js.append("<li>Select 'Saint Louis University'</li>\n");
        js.append("<li>Enter your Student ID: <strong>" + studentID + "</strong></li>\n");
        js.append("<li>Enter the amount you wish to pay</li>\n");
        js.append("<li>Complete the transaction</li>\n");
        js.append("<li>Keep your receipt for your records</li>\n");
        js.append("</ol>\n");
        js.append("<p style='color: red; font-size: 12px;'>\n");
        js.append("Note: Payment may take 1-2 business days to reflect in your account.\n");
        js.append("</p>\n");
        js.append("</div>\n");
        js.append("</div>\n");
        js.append("`);\n");
        
        js.append("$('#floatingform .ui-dialog-buttonpane').html(`\n");
        js.append("<div class='ui-dialog-buttonset'>\n");
        js.append("<button type='button' id='btnclose' class='ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only'>\n");
        js.append("<span class='ui-button-text'>Close</span>\n");
        js.append("</button>\n");
        js.append("</div>\n");
        js.append("`);\n");
        
        js.append("$('#btnclose').click(function() {\n");
        js.append("    $('#modalform').dialog('close');\n");
        js.append("});\n");
        
        out.print(js.toString());
    }
    
    private void handleBPIDisplay(HttpServletRequest request, HttpServletResponse response, String studentID) 
            throws ServletException, IOException {
        
        AccountStatement statement = AccountStatementManager.getStatement(studentID);
        ExamPeriodInfo examInfo = calculateExamPeriodInfo(statement);
        
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        
        StringBuilder js = new StringBuilder();
        js.append("$('#modalformloader').css('display', 'none');\n");
        js.append("$('#modalform-content').html(`\n");
        js.append("<form method='post' id='frmpay'>\n");
        js.append("<div class='mws-form'>\n");
        js.append("<input type='hidden' name='checkbpipayment' value='vinz'>\n");
        js.append("<input type='hidden' name='txtbpiidno' value='" + studentID + "'>\n");
        js.append("<fieldset class='mws-form-inline'>\n");
        js.append("<legend><table width='100%'><tr><td><b>AMOUNT TO PAY</b></td></tr></table></legend>\n");
        js.append("<div style='text-align: center; padding-top: 20px; padding-bottom: 20px;'>\n");
        js.append("<input type='number' step='any' id='txtbpiamount' name='txtbpiamount' value='" + (int)examInfo.amountDue + "' ");
        js.append("style='text-align:center; font-size: 30px; width: 300px; height: 50px; color: #0e284f;'>\n");
        js.append("</div>\n");
        js.append("</fieldset>\n");
        js.append("<input type='submit' value='s' style='opacity: 0;'>\n");
        js.append("</div>\n");
        js.append("</form>\n");
        js.append("`);\n");
        
        js.append("$('#floatingform .ui-dialog-buttonpane').html(`\n");
        js.append("<div class='ui-dialog-buttonset'>\n");
        js.append("<button type='button' id='btnproceed' class='ui-button ui-widget ui-state-default ui-corner-all ui-button-text-only'>\n");
        js.append("<span class='ui-button-text'>Proceed</span>\n");
        js.append("</button>\n");
        js.append("</div>\n");
        js.append("`);\n");
        
        js.append("$('#btnproceed').click(function() {\n");
        js.append("    var amount = parseFloat($('#txtbpiamount').val());\n");
        js.append("    if (amount <= 0) {\n");
        js.append("        alert('Please enter a valid amount.');\n");
        js.append("        return;\n");
        js.append("    }\n");
        js.append("    $('#frmpay').submit();\n");
        js.append("});\n");
        
        out.print(js.toString());
    }
    
    private void processDragonPayPayment(HttpServletRequest request, HttpServletResponse response, String studentID) 
            throws ServletException, IOException {
        
        try {
            double amount = Double.parseDouble(request.getParameter("txtdamount"));
            String paymentMethod = request.getParameter("paymentMethod");
            
            if (amount <= 0) {
                response.setContentType("application/json");
                response.getWriter().write("{\"success\": false, \"message\": \"Invalid amount\"}");
                return;
            }
            
            // Generate reference number
            String reference = "DP" + System.currentTimeMillis();
            
            // Process payment
            AccountStatement.PaymentResult result = AccountStatementManager.processPayment(
                studentID, amount, "DragonPay (" + paymentMethod + ")", reference);
            
            response.setContentType("application/json");
            if (result.success) {
                response.getWriter().write("{\"success\": true, \"message\": \"" + result.message + "\", \"reference\": \"" + reference + "\"}");
            } else {
                response.getWriter().write("{\"success\": false, \"message\": \"" + result.message + "\"}");
            }
            
        } catch (NumberFormatException e) {
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"message\": \"Invalid amount format\"}");
        }
    }
    
    private void processUPayPayment(HttpServletRequest request, HttpServletResponse response, String studentID) 
            throws ServletException, IOException {
        
        try {
            double amount = Double.parseDouble(request.getParameter("txtupayamount"));
            
            if (amount <= 0) {
                response.setContentType("application/json");
                response.getWriter().write("{\"success\": false, \"message\": \"Invalid amount\"}");
                return;
            }
            
            String reference = "UP" + System.currentTimeMillis();
            
            AccountStatement.PaymentResult result = AccountStatementManager.processPayment(
                studentID, amount, "UPay by UnionBank", reference);
            
            response.setContentType("application/json");
            if (result.success) {
                response.getWriter().write("{\"success\": true, \"message\": \"" + result.message + "\", \"reference\": \"" + reference + "\"}");
            } else {
                response.getWriter().write("{\"success\": false, \"message\": \"" + result.message + "\"}");
            }
            
        } catch (NumberFormatException e) {
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"message\": \"Invalid amount format\"}");
        }
    }
    
    private void processBDOPayment(HttpServletRequest request, HttpServletResponse response, String studentID) 
            throws ServletException, IOException {
        
        try {
            double amount = Double.parseDouble(request.getParameter("txtbdoamount"));
            
            if (amount <= 0) {
                response.setContentType("application/json");
                response.getWriter().write("{\"success\": false, \"message\": \"Invalid amount\"}");
                return;
            }
            
            String reference = "BDO" + System.currentTimeMillis();
            
            AccountStatement.PaymentResult result = AccountStatementManager.processPayment(
                studentID, amount, "BDO Online", reference);
            
            response.setContentType("application/json");
            if (result.success) {
                response.getWriter().write("{\"success\": true, \"message\": \"" + result.message + "\", \"reference\": \"" + reference + "\"}");
            } else {
                response.getWriter().write("{\"success\": false, \"message\": \"" + result.message + "\"}");
            }
            
        } catch (NumberFormatException e) {
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"message\": \"Invalid amount format\"}");
        }
    }
    
    private void processBPIPayment(HttpServletRequest request, HttpServletResponse response, String studentID) 
            throws ServletException, IOException {
        
        try {
            double amount = Double.parseDouble(request.getParameter("txtbpiamount"));
            
            if (amount <= 0) {
                response.setContentType("application/json");
                response.getWriter().write("{\"success\": false, \"message\": \"Invalid amount\"}");
                return;
            }
            
            String reference = "BPI" + System.currentTimeMillis();
            
            AccountStatement.PaymentResult result = AccountStatementManager.processPayment(
                studentID, amount, "BPI Online", reference);
            
            response.setContentType("application/json");
            if (result.success) {
                response.getWriter().write("{\"success\": true, \"message\": \"" + result.message + "\", \"reference\": \"" + reference + "\"}");
            } else {
                response.getWriter().write("{\"success\": false, \"message\": \"" + result.message + "\"}");
            }
            
        } catch (NumberFormatException e) {
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"message\": \"Invalid amount format\"}");
        }
    }
    
    private void processPayment(HttpServletRequest request, HttpServletResponse response, String studentID, String paymentType) 
            throws ServletException, IOException {
        
        // Generic payment processing method
        try {
            double amount = Double.parseDouble(request.getParameter("amount"));
            String channel = request.getParameter("channel");
            
            if (amount <= 0) {
                response.setContentType("application/json");
                response.getWriter().write("{\"success\": false, \"message\": \"Invalid amount\"}");
                return;
            }
            
            String reference = paymentType.toUpperCase() + System.currentTimeMillis();
            
            AccountStatement.PaymentResult result = AccountStatementManager.processPayment(
                studentID, amount, channel, reference);
            
            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(result));
            
        } catch (NumberFormatException e) {
            response.setContentType("application/json");
            response.getWriter().write("{\"success\": false, \"message\": \"Invalid amount format\"}");
        }
    }
    
    private void handlePaymentGatewayRequest(HttpServletRequest request, HttpServletResponse response, 
            String studentID, String action) throws ServletException, IOException {
        
        switch (action.toLowerCase()) {
            case "dragonpay":
                handleDragonPayDisplay(request, response, studentID);
                break;
            case "upay":
                handleUPayDisplay(request, response, studentID);
                break;
            case "bdo":
                handleBDOOnlineDisplay(request, response, studentID);
                break;
            case "bdobills":
                handleBDOBillsDisplay(request, response, studentID);
                break;
            case "bpi":
                handleBPIDisplay(request, response, studentID);
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action");
                break;
        }
    }
    
    // Inner class for exam period information
    public static class ExamPeriodInfo {
        public String currentPeriod;
        public double amountDue;
        public boolean isPaid;
        public String statusMessage;
    }
}