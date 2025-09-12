package com.specsShope.specsBackend.Services;

import com.specsShope.specsBackend.Dtos.*;
import com.specsShope.specsBackend.Mappers.CallbackRequestMapper;
import com.specsShope.specsBackend.Models.CallbackRequest;
import com.specsShope.specsBackend.Models.Cart;
import com.specsShope.specsBackend.Models.CartItem;
import com.specsShope.specsBackend.Models.User;
import com.specsShope.specsBackend.Repository.CallbackRepository;
import com.specsShope.specsBackend.Repository.CartRepo;
import com.specsShope.specsBackend.Repository.UserRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service


@Slf4j
public class CallBackServiceImpl implements CallbackRequestService {

    private final JwtService jwtService;
    private final CartRepo cartRepo;
    private final CallbackRepository callbackRepository;
    private final UserRepo userRepo;
    private final CallbackRequestMapper callbackRequestMapper;
    private final EmailService emailService;
    private final CallbackRequestMapper callbackMapper;
    private final String adminEmail;

    public CallBackServiceImpl(
            JwtService jwtService,
            CartRepo cartRepo,
            CallbackRepository callbackRepository,
            UserRepo userRepo,
            CallbackRequestMapper callbackRequestMapper,
            EmailService emailService,
            CallbackRequestMapper callbackMapper,
            @Value("${admin.email}") String adminEmail
    ) {
        this.jwtService = jwtService;
        this.cartRepo = cartRepo;
        this.callbackRepository = callbackRepository;
        this.userRepo = userRepo;
        this.callbackRequestMapper = callbackRequestMapper;
        this.emailService = emailService;
        this.callbackMapper = callbackMapper;
        this.adminEmail = adminEmail;
    }


    @Override
    public CallbackResponseDTO submitRequest(String token, CallbackRequestDTO dto) {
        log.info("Starting callback request submission process");

        try {
            // ✅ Extract email from token
            String email = jwtService.extractUsername(token);


            // ✅ Fetch user
            User user = userRepo.findByEmail(email)
                    .orElseThrow(() -> {
                        log.warn("User not found with email: {}", email);
                        return new UsernameNotFoundException("User not found");
                    });

            // ✅ Fetch cart
            Cart cart = cartRepo.findByUserId(user.getId())
                    .orElseThrow(() -> {
                        log.warn("Cart not found for user ID: {}", user.getId());
                        return new RuntimeException("Cart not found for user: " + email);
                    });

            // ✅ Save callback request
            CallbackRequest callback = CallbackRequest.builder()
                    .userId(user.getId())
                    .name(dto.getName())
                    .phone(dto.getPhone())
                    .address(dto.getAddress())
                    .items(cart.getItems())
                    .totalPrice(BigDecimal.valueOf(cart.getTotalPrice()))
                    .requestedAt(LocalDateTime.now())
                    .completed(false)
                    .build();

            callbackRepository.save(callback);

            // ✅ Convert cart items for response
            List<CartItemDTO> itemDTOs = cart.getItems().stream()
                    .map(item -> CartItemDTO.builder()
                            .productId(item.getProductId())
                            .productName(item.getProductName())
                            .price(item.getPrice())
                            .quantity(item.getQuantity())
                            .imageUrl(item.getImageUrl())
                            .build())
                    .toList();

            // ✅ Prepare admin email
            StringBuilder cartItemsTable = new StringBuilder();
            cartItemsTable.append("""
    <table style="width:100%; border-collapse: collapse; margin-top:15px;">
      <tr style="background-color: #f2f2f2;">
        <th style="padding: 10px; border: 1px solid #ddd;">Product</th>
        <th style="padding: 10px; border: 1px solid #ddd;">Quantity</th>
        <th style="padding: 10px; border: 1px solid #ddd;">Price</th>
      </tr>
""");

            for (CartItem item : cart.getItems()) {
                cartItemsTable.append(String.format("""
      <tr>
        <td style="padding: 10px; border: 1px solid #ddd;">%s</td>
        <td style="padding: 10px; border: 1px solid #ddd;">%d</td>
        <td style="padding: 10px; border: 1px solid #ddd;">₹%.2f</td>
      </tr>
    """, item.getProductName(), item.getQuantity(), item.getPrice()));
            }

            cartItemsTable.append("</table>");

            String adminEmailBody = String.format("""
<!DOCTYPE html>
<html>
<head>
  <style>
    body { font-family: 'Segoe UI', sans-serif; background-color: #f9f9f9; padding: 30px; }
    .box { background-color: #fff; padding: 25px; border-radius: 10px; max-width: 650px; margin: auto; box-shadow: 0 0 10px rgba(0,0,0,0.1); }
    h2 { color: #d9534f; }
    p { line-height: 1.5; color: #333; }
    .highlight { background-color: #f7f7f7; padding: 10px; border-radius: 6px; }
  </style>
</head>
<body>
  <div class="box">
    <h2>📞 New Callback Request Received</h2>
    <p><strong>👤 Name:</strong> %s</p>
    <p><strong>📧 Email:</strong> %s</p>
    <p><strong>📞 Phone:</strong> %s</p>
    <p><strong>🏠 Address:</strong> %s</p>

    <div class="highlight">
      <h3>🛒 Cart Details</h3>
      %s
      <p style="margin-top:15px;"><strong>💰 Total Price: ₹%.2f</strong></p>
    </div>

    <p style="margin-top:20px;">Please follow up with the customer as soon as possible.</p>
    <p>Regards,<br/><strong>CareOptics System</strong></p>
  </div>
</body>
</html>
""",
                    user.getName(),
                    user.getEmail(),
                    dto.getPhone(),
                    dto.getAddress(),
                    cartItemsTable.toString(),
                    cart.getTotalPrice()
            );

            // ✅ Send admin email with HTML
            try {
                emailService.sendHtmlEmail(
                        adminEmail,
                        "📞 Callback Request - " + user.getName(),
                        adminEmailBody
                );
                log.info("Admin notification email sent successfully to: {}", "abhinavdecide@gmail.com");
            } catch (Exception e) {
                log.error("Failed to send admin notification email to: {}", "abhinavdecide@gmail.com", e);
                // Continue processing even if admin email fails
            }

            // ✅ Prepare user confirmation email
            log.debug("Preparing user confirmation email content");
            String userEmailBody = """
                <!DOCTYPE html>
                <html>
                <head>
                  <style>
                    body { font-family: 'Segoe UI', sans-serif; background-color: #f2f2f2; padding: 30px; }
                    .box { background-color: #fff; padding: 25px; border-radius: 10px; max-width: 600px; margin: auto; }
                    h2 { color: #1e90ff; }
                    p { line-height: 1.6; color: #333; }
                    .footer { margin-top: 30px; font-size: 0.9em; color: #888; }
                  </style>
                </head>
                <body>
                  <div class="box">
                    <h2>Thank You, %s! 🙏</h2>
                    <p>We've received your callback request on <strong>CareOptics</strong>.</p>
                    <p>Our team will contact you shortly based on the details you've provided.</p>
                    <p>If you need urgent assistance, reply to this email directly.</p>
                    <br/>
                    <p>Warm regards,<br/><strong>CareOptics Team</strong></p>
                    <div class="footer">📞 careoptics@example.com | 🌐 www.careoptics.in</div>
                  </div>
                </body>
                </html>
            """.formatted(user.getName());

            // ✅ Send user confirmation email
            try {
                emailService.sendHtmlEmail(
                        user.getEmail(),
                        "Thanks for requesting a callback at CareOptics",
                        userEmailBody
                );
                log.info("User confirmation email sent successfully to: {}", user.getEmail());
            } catch (Exception e) {
                log.error("Failed to send user confirmation email to: {}", user.getEmail(), e);
                // Continue processing even if user email fails
            }

            // ✅ Return response DTO
            CallbackResponseDTO responseDTO = CallbackResponseDTO.builder()
                    .id(callback.getId())
                    .userId(callback.getUserId())
                    .name(callback.getName())
                    .phone(callback.getPhone())
                    .address(callback.getAddress())
                    .items(itemDTOs)
                    .totalPrice(callback.getTotalPrice())
                    .requestedAt(callback.getRequestedAt())
                    .completed(callback.isCompleted())
                    .build();

            log.info("Callback request submission completed successfully for user: {}", user.getEmail());
            return responseDTO;

        } catch (UsernameNotFoundException e) {
            log.warn("Callback request failed - User not found");
            throw new RuntimeException("User not found", e);
        } catch (RuntimeException e) {
            log.error("Callback request failed - Runtime exception occurred", e);
            throw e;
        } catch (Exception e) {
            log.error("Callback request failed - Unexpected error occurred", e);
            throw new RuntimeException("Failed to process callback request", e);
        }
    }
    @Override
    public List<CallbackResponseDTO> getAllRequests() {
        return callbackRepository.findAll().stream()
                .map(callbackRequestMapper::toDto) // ← Clean and concise
                .collect(Collectors.toList());
    }

    @Override
    public List<CallbackResponseDTO> getRequestsByUserId(String userId) {
        return callbackRepository.findByUserId(userId).stream()  // ← Must return List<CallbackRequest>
                .map(callbackRequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CallbackResponseDTO markAsCompleted(String id) {
        // 1️⃣ Fetch the callback request
        CallbackRequest callback = callbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Callback request not found with ID: " + id));

        // 2️⃣ Update status
        callback.setCompleted(true);
        callbackRepository.save(callback);

        // 3️⃣ Send email to user
        String userEmailBody = String.format("""
            <!DOCTYPE html>
            <html>
            <head>
              <style>
                body { font-family: 'Segoe UI', sans-serif; background-color: #f2f2f2; padding: 30px; }
                .box { background-color: #fff; padding: 25px; border-radius: 10px; max-width: 600px; margin: auto; }
                h2 { color: #28a745; }
                p { line-height: 1.6; color: #333; }
                .footer { margin-top: 30px; font-size: 0.9em; color: #888; }
              </style>
            </head>
            <body>
              <div class="box">
                <h2>Hi %s! ✅</h2>
                <p>Your callback request submitted on <strong>%s</strong> has been completed.</p>
                <p>Our team has handled your request. Thank you for using CareOptics!</p>
                <br/>
                <p>Warm regards,<br/><strong>CareOptics Team</strong></p>
                <div class="footer">📞 careoptics@example.com | 🌐 www.careoptics.in</div>
              </div>
            </body>
            </html>
            """,
                callback.getName(),
                callback.getRequestedAt()
        );

        try {
            emailService.sendHtmlEmail(
                    userRepo.findById(callback.getUserId())
                            .orElseThrow(() -> new RuntimeException("User not found"))
                            .getEmail(),
                    "✅ Your CareOptics Callback Request Completed",
                    userEmailBody
            );
        } catch (Exception e) {
            // Log but don't fail the request
            log.error("Failed to send completion email to user: {}", callback.getUserId(), e);
        }

        // 4️⃣ Return updated DTO
        return callbackRequestMapper.toDto(callback);
    }


    @Override
    @Transactional
    public void deleteRequest(String id) {
        if (!callbackRepository.existsById(id)) {
            throw new RuntimeException("Callback request not found with ID: " + id);
        }
        callbackRepository.deleteById(id);
    }
    @Override
    public CallbackStatsDTO getCallbackStats() {
        long completedCount = callbackRepository.findAll()
                .stream()
                .filter(CallbackRequest::isCompleted)
                .count();

        long pendingCount = callbackRepository.findAll()
                .stream()
                .filter(c -> !c.isCompleted())
                .count();
        long totalCount = completedCount + pendingCount;

        return new CallbackStatsDTO(totalCount,completedCount, pendingCount);
    }
    @Override
    public RevenueResponseDTO getRevenuePerMonth(Integer year) {
        // Default → current year if not passed
        int targetYear = (year != null) ? year : LocalDate.now().getYear();

        // fetch only completed=true requests
        List<CallbackRequest> requests = callbackRepository.findByCompletedTrue();

        // filter by year
        requests = requests.stream()
                .filter(req -> req.getRequestedAt().getYear() == targetYear)
                .toList();

        // group by Year-Month
        Map<String, List<CallbackRequest>> groupedByMonth = requests.stream()
                .collect(Collectors.groupingBy(req ->
                        req.getRequestedAt().getYear() + "-" + req.getRequestedAt().getMonth().name()
                ));

        List<RevenueDTO> monthlyRevenues = new ArrayList<>();

        for (Map.Entry<String, List<CallbackRequest>> entry : groupedByMonth.entrySet()) {
            String month = entry.getKey();
            List<CallbackRequest> monthlyRequests = entry.getValue();

            // total revenue only (approved users)
            double totalRevenue = monthlyRequests.stream()
                    .mapToDouble(req -> req.getTotalPrice().doubleValue())
                    .sum();

            monthlyRevenues.add(new RevenueDTO(month, totalRevenue));
        }

        return new RevenueResponseDTO(monthlyRevenues);
    }
    @Override
    public List<CallbackResponseDTO> getRecentCallbacks(int limit) {
        List<CallbackRequest> requests = callbackRepository
                .findByCompletedTrueOrderByRequestedAtDesc(PageRequest.of(0, limit));

        return requests.stream()
                .map(callbackMapper::toDto)
                .toList();
    }








}
