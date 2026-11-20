
package com.civiccare.controller;

import com.civiccare.model.Complaint;
import com.civiccare.service.ComplaintService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/complaints")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    // CREATE complaint
    @PostMapping
    public ResponseEntity<Complaint> createComplaint(
            @RequestBody Complaint complaint) {

        Complaint saved = complaintService.createComplaint(complaint);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // GET all complaints
    @GetMapping
    public List<Complaint> getAllComplaints() {
        return complaintService.getAllComplaints();
    }

    // DASHBOARD statistics
    @GetMapping("/stats")
    public Map<String, Long> getDashboardStats() {
        return complaintService.getDashboardStats();
    }

    // GET complaints assigned to an officer
    @GetMapping("/assigned")
    public ResponseEntity<?> getAssignedComplaints(
            @RequestParam String officerName) {

        try {
            return ResponseEntity.ok(
                    complaintService.getAssignedComplaints(officerName));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET complaint by ID
    @GetMapping("/{id}")
    public ResponseEntity<Complaint> getComplaintById(
            @PathVariable Long id) {

        try {
            return ResponseEntity.ok(
                    complaintService.getComplaintById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // UPDATE complaint details
    @PutMapping("/{id}")
    public ResponseEntity<?> updateComplaint(
            @PathVariable Long id,
            @RequestBody Complaint complaint) {

        try {
            return ResponseEntity.ok(
                    complaintService.updateComplaint(id, complaint));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // UPDATE complaint status
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        try {
            return ResponseEntity.ok(
                    complaintService.updateStatus(id, status));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ASSIGN complaint to officer
    @PutMapping("/{id}/assign")
    public ResponseEntity<?> assignOfficer(
            @PathVariable Long id,
            @RequestParam String officerName) {

        try {
            return ResponseEntity.ok(
                    complaintService.assignOfficer(id, officerName));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // RESOLVE complaint with resolution details
    @PutMapping("/{id}/resolve")
    public ResponseEntity<?> resolveComplaint(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        try {
            String details = request.get("resolutionDetails");

            return ResponseEntity.ok(
                    complaintService.resolveComplaint(id, details));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE complaint - existing functionality
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComplaint(
            @PathVariable Long id) {

        try {
            complaintService.deleteComplaint(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
