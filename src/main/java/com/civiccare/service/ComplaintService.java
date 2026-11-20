
package com.civiccare.service;

import com.civiccare.model.Complaint;
import com.civiccare.repository.ComplaintRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;

    public ComplaintService(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Status is required");
        }

        String normalized = status.trim().toUpperCase(Locale.ROOT);

        if (!List.of(
                "PENDING",
                "UNDER_REVIEW",
                "IN_PROGRESS",
                "RESOLVED"
        ).contains(normalized)) {
            throw new IllegalArgumentException("Invalid complaint status");
        }

        return normalized;
    }

    // CREATE complaint
    public Complaint createComplaint(Complaint complaint) {
        if (complaint.getStatus() == null
                || complaint.getStatus().isBlank()) {
            complaint.setStatus("PENDING");
        } else {
            complaint.setStatus(normalizeStatus(complaint.getStatus()));
        }

        return complaintRepository.save(complaint);
    }

    // GET all complaints - Admin dashboard
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    // GET complaint by ID
    public Complaint getComplaintById(Long id) {
        return complaintRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Complaint not found: " + id));
    }

    // UPDATE complaint details
    public Complaint updateComplaint(
            Long id, Complaint updatedComplaint) {

        Complaint existing = getComplaintById(id);

        if (updatedComplaint.getTitle() != null) {
            existing.setTitle(updatedComplaint.getTitle());
        }

        if (updatedComplaint.getDescription() != null) {
            existing.setDescription(updatedComplaint.getDescription());
        }

        if (updatedComplaint.getCategory() != null) {
            existing.setCategory(updatedComplaint.getCategory());
        }

        if (updatedComplaint.getLocation() != null) {
            existing.setLocation(updatedComplaint.getLocation());
        }

        if (updatedComplaint.getStatus() != null
                && !updatedComplaint.getStatus().isBlank()) {
            existing.setStatus(
                    normalizeStatus(updatedComplaint.getStatus()));
        }

        if (updatedComplaint.getAssignedOfficer() != null) {
            existing.setAssignedOfficer(
                    updatedComplaint.getAssignedOfficer().trim());
        }

        if (updatedComplaint.getResolutionDetails() != null) {
            existing.setResolutionDetails(
                    updatedComplaint.getResolutionDetails());
        }

        return complaintRepository.save(existing);
    }

    // UPDATE status
    public Complaint updateStatus(Long id, String status) {
        Complaint complaint = getComplaintById(id);
        complaint.setStatus(normalizeStatus(status));

        return complaintRepository.save(complaint);
    }

    // ASSIGN officer
    public Complaint assignOfficer(Long id, String officerName) {
        if (officerName == null || officerName.isBlank()) {
            throw new IllegalArgumentException("Officer name is required");
        }

        Complaint complaint = getComplaintById(id);
        complaint.setAssignedOfficer(officerName.trim());

        return complaintRepository.save(complaint);
    }

    // GET complaints assigned to one officer
    @Transactional(readOnly = true)
    public List<Complaint> getAssignedComplaints(String officerName) {
        if (officerName == null || officerName.isBlank()) {
            throw new IllegalArgumentException("Officer name is required");
        }

        return complaintRepository
                .findByAssignedOfficerIgnoreCase(officerName.trim());
    }

    // RESOLVE complaint and save resolution details
    public Complaint resolveComplaint(Long id, String resolutionDetails) {
        if (resolutionDetails == null || resolutionDetails.isBlank()) {
            throw new IllegalArgumentException(
                    "Resolution details are required");
        }

        Complaint complaint = getComplaintById(id);
        complaint.setResolutionDetails(resolutionDetails.trim());
        complaint.setStatus("RESOLVED");

        return complaintRepository.save(complaint);
    }

    // DASHBOARD statistics
    @Transactional(readOnly = true)
    public Map<String, Long> getDashboardStats() {
        long total = complaintRepository.count();
        long pending = complaintRepository.countByStatus("PENDING");
        long underReview =
                complaintRepository.countByStatus("UNDER_REVIEW");
        long inProgress =
                complaintRepository.countByStatus("IN_PROGRESS");
        long resolved = complaintRepository.countByStatus("RESOLVED");

        return Map.of(
                "total", total,
                "pending", pending,
                "underReview", underReview,
                "inProgress", inProgress,
                "resolved", resolved
        );
    }

    // DELETE complaint - existing functionality
    public void deleteComplaint(Long id) {
        Complaint complaint = getComplaintById(id);
        complaintRepository.delete(complaint);
    }
}
