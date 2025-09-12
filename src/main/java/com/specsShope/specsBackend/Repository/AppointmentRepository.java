package com.specsShope.specsBackend.Repository;

import com.specsShope.specsBackend.Dtos.AppointmentReasonStatsDTO;
import com.specsShope.specsBackend.Models.Appointment;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Repository
public interface AppointmentRepository extends MongoRepository<Appointment, String> {
    List<Appointment> findByUserId(String userId);

    boolean existsByPreferredDateAndPreferredTime(String preferredDate, String preferredTime);

    List<Appointment> findByPreferredDate(String preferredDate);

    default List<Appointment> findAppointmentsByDate(LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        return this.findByPreferredDateBetween(start, end);
    }

    List<Appointment> findByPreferredDateBetween(LocalDateTime start, LocalDateTime end);

    @Query("{}")
    List<Appointment> findTopByOrderByBookedAtDesc(int pageable);

    @Aggregation(pipeline = {
            "{ $unwind: \"$eyeProblems\" }",
            "{ $group: { _id: \"$eyeProblems\", count: { $sum: 1 } } }",
            "{ $project: { reason: \"$_id\", count: 1, _id: 0 } }"
    })
    List<AppointmentReasonProjection> countAppointmentsByReason();
}
