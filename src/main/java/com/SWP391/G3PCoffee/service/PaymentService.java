package com.SWP391.G3PCoffee.service;

import com.SWP391.G3PCoffee.model.PaymentResponse;

import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    @Value("${vnpay.tmnCode}")
    private String vnpTmnCode;
    
    @Value("${vnpay.hashSecret}")
    private String vnpHashSecret;
    
    @Value("${vnpay.paymentUrl}")
    private String vnpPaymentUrl;
    
    @Value("${vnpay.version}")
    private String vnpVersion;
    
    @Value("${vnpay.command}")
    private String vnpCommand;
    
    @Value("${vnpay.currCode}")
    private String vnpCurrCode;
    
    @Value("${vnpay.locale}")
    private String vnpLocale;
    
    public String createVnPayPaymentUrl(
            Integer orderId, 
            BigDecimal amount, 
            String returnUrl,
            HttpSession session) throws Exception {
        
        String vnp_TxnRef = orderId.toString();
        long vnp_Amount = amount.multiply(new BigDecimal("100")).longValue(); // Convert to VND without decimal
        
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnpVersion);
        vnp_Params.put("vnp_Command", vnpCommand);
        vnp_Params.put("vnp_TmnCode", vnpTmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(vnp_Amount));
        vnp_Params.put("vnp_CurrCode", vnpCurrCode);
        
        // Generate unique order ID with format YYYYMMDDHHMMSSxxxx
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        
        // Save IP address of customer
        String vnp_IpAddr = (String) session.getAttribute("clientIpAddress");
        if (vnp_IpAddr == null || vnp_IpAddr.isEmpty()) {
            vnp_IpAddr = "127.0.0.1"; // Default if not available
        }
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        
        // Set locale
        vnp_Params.put("vnp_Locale", vnpLocale);
        
        // Add order info
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang: " + orderId);
        vnp_Params.put("vnp_OrderType", "other"); // Default order type
        vnp_Params.put("vnp_ReturnUrl", returnUrl);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        
        // Add bank code if needed
        // vnp_Params.put("vnp_BankCode", "NCB"); // Optional: specific bank code
        
        // Build query string
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                
                // Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        
        // Create secure hash
        String queryUrl = query.toString();
        String vnp_SecureHash = hmacSHA512(vnpHashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        
        return vnpPaymentUrl + "?" + queryUrl;
    }
    
    public PaymentResponse processVnPayReturn(Map<String, String> vnpParams) {
        // Remove vnp_SecureHash to validate
        String vnp_SecureHash = vnpParams.get("vnp_SecureHash");
        if (vnp_SecureHash == null) {
            return PaymentResponse.failure("Invalid signature");
        }
        
        // Remove hash and signature from params
        Map<String, String> signParams = new HashMap<>(vnpParams);
        signParams.remove("vnp_SecureHash");
        signParams.remove("vnp_SecureHashType");
        
        // Build hash data from response
        List<String> fieldNames = new ArrayList<>(signParams.keySet());
        Collections.sort(fieldNames);
        
        StringBuilder hashData = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = signParams.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                
                if (itr.hasNext()) {
                    hashData.append('&');
                }
            }
        }
        
        // Verify checksum
        String secureHash = hmacSHA512(vnpHashSecret, hashData.toString());
        if (!secureHash.equals(vnp_SecureHash)) {
            return PaymentResponse.failure("Invalid signature");
        }
        
        // Get payment result
        String vnp_ResponseCode = vnpParams.get("vnp_ResponseCode");
        if ("00".equals(vnp_ResponseCode)) {
            // Payment successful
            String orderId = vnpParams.get("vnp_TxnRef");
            String transactionInfo = "VNPAY Transaction ID: " + vnpParams.get("vnp_TransactionNo");
            
            return PaymentResponse.success(Integer.parseInt(orderId), transactionInfo);
        } else {
            // Payment failed
            return PaymentResponse.failure("Payment failed with code: " + vnp_ResponseCode);
        }
    }
    
    // HMAC-SHA512 encryption
    private String hmacSHA512(String key, String data) {
        try {
            Mac sha512Hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA512");
            sha512Hmac.init(secretKeySpec);
            byte[] hmacData = sha512Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hmacData);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate HMAC-SHA512", e);
        }
    }
    
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}