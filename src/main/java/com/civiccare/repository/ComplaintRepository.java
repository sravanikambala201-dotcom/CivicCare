
package com.civiccare.repository;

import com.civiccare.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintRepository
        extends JpaRepository<Complaint, Long> {

    // Get complaints assigned to a particular officer
    List<Complaint> findByAssignedOfficerIgnoreCase(String assignedOfficer);

    // Dashboard statistics
    long countByStatus(String status);
}
