package com.finserv.serviceImpl;

import com.finserv.configuration.CustomUserDetails;

import com.finserv.dto.ChatbotDocumentSummaryDTO;
import com.finserv.dto.ChatbotResponseDTO;
import com.finserv.dto.ChatbotUserSummaryDTO;

import com.finserv.entity.Dealer;
import com.finserv.entity.Document;
import com.finserv.entity.PersonalInfo;
import com.finserv.entity.User;
import com.finserv.enums.DocumentStatus;
import com.finserv.repository.DealerRepository;
import com.finserv.repository.DocumentRepository;
import com.finserv.repository.PersonalInfoRepository;
import com.finserv.repository.UserRepository;
import com.finserv.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;

import java.util.Comparator;
import java.util.List;

import java.util.Map;

import java.util.LinkedHashMap;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ChatbotServiceImpl implements ChatbotService {

    private final UserRepository userRepository;
    private final DealerRepository dealerRepository;
    private final DocumentRepository documentRepository;
    private final PersonalInfoRepository personalInfoRepository;


    private CustomUserDetails currentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails principal)) {
            throw new RuntimeException("Unauthorized access");
        }
        return principal;
    }


    private User currentUser() {
        String email = currentPrincipal().getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }


    private Dealer currentDealer() {
        String email = currentPrincipal().getUsername();
        return dealerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Dealer not found"));
    }


    private ChatbotResponseDTO buildResponse(
            boolean success,
            String role,
            String intent,
            String message,
            Object data,
            List<String> suggestions
    ) {
        return ChatbotResponseDTO.builder()
                .success(success)
                .role(role)
                .intent(intent)
                .message(message)
                .data(data)
                .suggestions(suggestions == null ? List.of() : suggestions)
                .build();
    }


    private ChatbotDocumentSummaryDTO buildDocumentSummary(List<Document> documents) {
        long pending = documents.stream().filter(d -> d.getStatus() == DocumentStatus.PENDING).count();
        long verified = documents.stream().filter(d -> d.getStatus() == DocumentStatus.VERIFIED).count();
        long approved = documents.stream().filter(d -> d.getStatus() == DocumentStatus.APPROVED).count();
        long rejected = documents.stream().filter(d -> d.getStatus() == DocumentStatus.REJECTED).count();
        return new ChatbotDocumentSummaryDTO(pending, verified, approved, rejected, documents.size());
    }


    private Map<String, Object> toDocumentMap(Document document) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("documentId", document.getDocumentId());
        data.put("userId", document.getUser() != null ? document.getUser().getUserId() : null);
        data.put("customerName", document.getUser() != null ? document.getUser().getFullName() : null);
        data.put("dealerCode", document.getUser() != null ? document.getUser().getDealerCode() : null);
        data.put("documentType", document.getDocumentType() != null ? document.getDocumentType().name() : null);
        data.put("status", document.getStatus() != null ? document.getStatus().name() : null);
        data.put("remarks", document.getRemarks());
        data.put("uploadedAt", document.getUploadedAt());
        return data;
    }

    private String deriveApplicationStatus(User user, PersonalInfo info, List<Document> documents) {
        if (info == null) {
            return "PROFILE_INCOMPLETE";
        }

        if (documents.stream().anyMatch(d -> d.getStatus() == DocumentStatus.REJECTED)) {
            return "DOCUMENTS_REJECTED";
        }

        if (documents.isEmpty() || documents.stream().anyMatch(d -> d.getStatus() == DocumentStatus.PENDING)) {
            return "DOCUMENTS_PENDING";
        }

        if (Boolean.FALSE.equals(user.getPaymentDone())) {
            return "PAYMENT_PENDING";
        }

        boolean allApproved = !documents.isEmpty() && documents.stream().allMatch(d -> d.getStatus() == DocumentStatus.APPROVED);
        if (allApproved) {
            return "READY_FOR_BANK";
        }

        boolean allVerifiedOrApproved = !documents.isEmpty() && documents.stream()
                .allMatch(d -> d.getStatus() == DocumentStatus.APPROVED || d.getStatus() == DocumentStatus.VERIFIED);
        if (allVerifiedOrApproved) {
            return "DOCUMENTS_VERIFIED";
        }

        return "UNDER_REVIEW";
    }


    private LocalDateTime[] resolveMonthRange(String month) {
        if (month == null || month.isBlank() || "current".equalsIgnoreCase(month)) {
            YearMonth current = YearMonth.now();
            return new LocalDateTime[]{
                    current.atDay(1).atStartOfDay(),
                    current.atEndOfMonth().atTime(23, 59, 59)
            };
        }

        if ("last".equalsIgnoreCase(month)) {
            // Previous calendar month for monthly chatbot summaries.
            YearMonth previous = YearMonth.now().minusMonths(1);
            return new LocalDateTime[]{
                    previous.atDay(1).atStartOfDay(),
                    previous.atEndOfMonth().atTime(23, 59, 59)
            };
        }

        throw new RuntimeException("Invalid month parameter");
    }

    @Override
    public ChatbotResponseDTO getUserApplicationSummary() {
        User user = currentUser();
        PersonalInfo info = personalInfoRepository.findByUser_UserId(user.getUserId()).orElse(null);
        List<Document> documents = documentRepository.findByUser_UserId(user.getUserId());
        ChatbotUserSummaryDTO dto = new ChatbotUserSummaryDTO();
        dto.setUserId(user.getUserId());
        dto.setName(user.getFullName());
        dto.setApplicationId(user.getApplicationId());
        dto.setPaymentDone(user.getPaymentDone());
        dto.setLoanAmount(info != null ? info.getLoanAmount() : null);
        dto.setApplicationStatus(deriveApplicationStatus(user, info, documents));
        dto.setDocumentSummary(buildDocumentSummary(documents));
        dto.setProfileCompletionPercent(calculateProfileCompletion(info));
        dto.setCreatedAt(user.getCreatedAt());
        return buildResponse(
                true,
                "USER",
                "APPLICATION_SUMMARY",
                "Your application summary has been fetched successfully.",
                dto,
                List.of("Show document status", "Show pending documents", "Show loan amount")
        );
    }


    @Override
    public ChatbotResponseDTO getUserDocumentStatus() {
        User user = currentUser();
        List<Document> documents = documentRepository.findByUser_UserId(user.getUserId());
        List<Map<String, Object>> data = documents.stream()
                .sorted(Comparator.comparing(Document::getUploadedAt, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(this::toDocumentMap)
                .toList();
        return buildResponse(
                true,
                "USER",
                "DOCUMENT_STATUS",
                data.isEmpty() ? "No documents found for your application." : "Your document status has been fetched successfully.",
                data,
                List.of("Show pending documents", "Show application summary")
        );
    }


    @Override
    public ChatbotResponseDTO getUserPendingDocuments() {
        User user = currentUser();
        List<Document> documents = documentRepository.findByUser_UserIdAndStatus(user.getUserId(), DocumentStatus.PENDING);
        List<Map<String, Object>> data = documents.stream()
                .map(this::toDocumentMap)
                .toList();
        return buildResponse(
                true,
                "USER",
                "PENDING_DOCUMENTS",
                data.isEmpty() ? "You have no pending documents." : "Your pending documents have been fetched successfully.",
                data,
                List.of("Show document status", "Show application summary")
        );
    }


    @Override
    public ChatbotResponseDTO getUserLoanAmount() {
        User user = currentUser();
        PersonalInfo info = personalInfoRepository.findByUser_UserId(user.getUserId()).orElse(null);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("applicationId", user.getApplicationId());
        data.put("loanAmount", info != null ? info.getLoanAmount() : null);
        return buildResponse(
                true,
                "USER",
                "LOAN_AMOUNT",
                "Your loan amount has been fetched successfully.",
                data,
                List.of("Show application summary")
        );
    }


    @Override
    public ChatbotResponseDTO getDealerMyId() {
        Dealer dealer = currentDealer();
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("dealerId", dealer.getDealerId());
        data.put("dealerCode", dealer.getDealerCode());
        data.put("dealerName", dealer.getFullName());
        data.put("email", dealer.getEmail());
        return buildResponse(
                true,
                "DEALER",
                "DEALER_ID",
                "Your dealer details have been fetched successfully.",
                data,
                List.of("Show my users", "Show pending documents")
        );
    }


    @Override
    public ChatbotResponseDTO getDealerUsers() {
        Dealer dealer = currentDealer();
        List<User> users = userRepository.findByDealerCode(dealer.getDealerCode());
        List<Map<String, Object>> data = users.stream()
                .map(user -> {
                    PersonalInfo info = personalInfoRepository.findByUser_UserId(user.getUserId()).orElse(null);
                    List<Document> documents = documentRepository.findByUser_UserId(user.getUserId());
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("userId", user.getUserId());
                    map.put("name", user.getFullName());
                    map.put("applicationId", user.getApplicationId());
                    map.put("email", user.getEmail());
                    map.put("mobileNumber", user.getMobileNumber());
                    map.put("paymentDone", user.getPaymentDone());
                    map.put("applicationStatus", deriveApplicationStatus(user, info, documents));
                    map.put("loanAmount", info != null ? info.getLoanAmount() : null);
                    map.put("documentCount", documents.size());
                    map.put("createdAt", user.getCreatedAt());
                    return map;
                })
                .toList();
        return buildResponse(
                true,
                "DEALER",
                "DEALER_USERS",
                data.isEmpty() ? "No users found for your dealer code." : "Dealer users have been fetched successfully.",
                data,
                List.of("Show pending documents", "Show document summary")
        );
    }


    @Override
    public ChatbotResponseDTO getDealerPendingDocuments() {
        Dealer dealer = currentDealer();
        List<Document> documents = documentRepository.findByUser_DealerCodeAndStatus(dealer.getDealerCode(), DocumentStatus.PENDING);
        List<Map<String, Object>> data = documents.stream()
                .map(this::toDocumentMap)
                .toList();
        return buildResponse(
                true,
                "DEALER",
                "PENDING_DOCUMENTS",
                data.isEmpty() ? "No pending documents found for your dealer users." : "Pending documents have been fetched successfully.",
                data,
                List.of("Show dealer users", "Show document summary")
        );
    }


    @Override
    public ChatbotResponseDTO getDealerDocumentSummary(String month) {
        Dealer dealer = currentDealer();
        LocalDateTime[] range = resolveMonthRange(month);
        List<Document> documents = documentRepository.findByUser_DealerCode(dealer.getDealerCode()).stream()
                .filter(document -> document.getUploadedAt() != null
                        && !document.getUploadedAt().isBefore(range[0])
                        && !document.getUploadedAt().isAfter(range[1]))
                .toList();
        ChatbotDocumentSummaryDTO summary = buildDocumentSummary(documents);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("month", month == null ? "current" : month);
        data.put("dealerCode", dealer.getDealerCode());
        data.put("summary", summary);
        return buildResponse(
                true,
                "DEALER",
                "DOCUMENT_SUMMARY",
                "Dealer document summary has been fetched successfully.",
                data,
                List.of("Show pending documents", "Show dealer users")
        );
    }


    @Override
    public ChatbotResponseDTO getAdminUsers() {
        List<User> users = userRepository.findAllUsersWithPersonalInfo();
        List<Map<String, Object>> data = users.stream()
                .map(user -> {
                    PersonalInfo info = user.getPersonalInfo();
                    List<Document> documents = documentRepository.findByUser_UserId(user.getUserId());
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("userId", user.getUserId());
                    map.put("name", user.getFullName());
                    map.put("applicationId", user.getApplicationId());
                    map.put("email", user.getEmail());
                    map.put("dealerCode", user.getDealerCode());
                    map.put("paymentDone", user.getPaymentDone());
                    map.put("applicationStatus", deriveApplicationStatus(user, info, documents));
                    map.put("documentCount", documents.size());
                    map.put("loanAmount", info != null ? info.getLoanAmount() : null);
                    map.put("createdAt", user.getCreatedAt());
                    return map;
                })
                .toList();
        return buildResponse(
                true,
                "ADMIN",
                "ADMIN_USERS",
                data.isEmpty() ? "No users found." : "Admin users have been fetched successfully.",
                data,
                List.of("Show dealer summary", "Show document summary")
        );
    }


    @Override
    public ChatbotResponseDTO getAdminDealerWiseSummary() {
        List<User> users = userRepository.findAllUsersWithPersonalInfo();
        Map<String, List<User>> byDealer = users.stream()
                .filter(user -> user.getDealerCode() != null && !user.getDealerCode().isBlank())
                .collect(Collectors.groupingBy(User::getDealerCode));

        List<Map<String, Object>> data = byDealer.entrySet().stream()
                .map(entry -> {
                    String dealerCode = entry.getKey();
                    Dealer dealer = dealerRepository.findByDealerCode(dealerCode).orElse(null);
                    List<User> dealerUsers = entry.getValue();
                    long paymentApprovedCount = dealerUsers.stream().filter(user -> Boolean.TRUE.equals(user.getPaymentDone())).count();
                    long paymentPendingCount = dealerUsers.stream().filter(user -> !Boolean.TRUE.equals(user.getPaymentDone())).count();
                    long documentCount = dealerUsers.stream()
                            .mapToLong(user -> documentRepository.findByUser_UserId(user.getUserId()).size())
                            .sum();
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("dealerId", dealer != null ? dealer.getDealerId() : null);
                    map.put("dealerCode", dealerCode);
                    map.put("dealerName", dealer != null ? dealer.getFullName() : null);
                    map.put("usersCount", dealerUsers.size());
                    map.put("paymentApprovedCount", paymentApprovedCount);
                    map.put("paymentPendingCount", paymentPendingCount);
                    map.put("documentCount", documentCount);
                    return map;
                })
                .toList();

        return buildResponse(
                true,
                "ADMIN",
                "DEALER_WISE_SUMMARY",
                data.isEmpty() ? "No dealer summary data found." : "Dealer-wise summary has been fetched successfully.",
                data,
                List.of("Show pending documents", "Show all users")
        );
    }


    @Override
    public ChatbotResponseDTO getAdminDocumentSummary(String month) {
        LocalDateTime[] range = resolveMonthRange(month);
        List<Document> documents = documentRepository.findAll().stream()
                .filter(document -> document.getUploadedAt() != null
                        && !document.getUploadedAt().isBefore(range[0])
                        && !document.getUploadedAt().isAfter(range[1]))
                .toList();
        ChatbotDocumentSummaryDTO summary = buildDocumentSummary(documents);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("month", month == null ? "current" : month);
        data.put("summary", summary);
        return buildResponse(
                true,
                "ADMIN",
                "DOCUMENT_SUMMARY",
                "Admin document summary has been fetched successfully.",
                data,
                List.of("Show pending documents", "Show dealer-wise summary")
        );
    }

    @Override
    public ChatbotResponseDTO getAdminPendingDocuments() {
        List<Document> documents = documentRepository.findByStatusAndUser_PaymentDoneTrue(DocumentStatus.PENDING);
        List<Map<String, Object>> data = documents.stream()
                .map(this::toDocumentMap)
                .toList();
        return buildResponse(
                true,
                "ADMIN",
                "PENDING_DOCUMENTS",
                data.isEmpty() ? "No pending documents found." : "Pending documents have been fetched successfully.",
                data,
                List.of("Show document summary", "Show dealer-wise summary")
        );
    }


    private Long calculateProfileCompletion(PersonalInfo info) {
        if (info == null) {
            return 0L;
        }

        long filled = 0;
        long total = 6;
        if (isFilled(info.getFullName())) filled++;
        if (isFilled(info.getEmail())) filled++;
        if (isFilled(info.getMobileNumber())) filled++;
        if (isFilled(info.getAddress())) filled++;
        if (isFilled(info.getCity())) filled++;
        if (isFilled(info.getState()) && isFilled(info.getPincode()) && info.getLoanAmount() != null) {
            filled++;
        }
        return Math.round((filled * 100.0) / total);
    }

    private boolean isFilled(String value) {
        return value != null && !value.trim().isEmpty();
    }
}


