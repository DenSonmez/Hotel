package dk.lyngby.dto;

import dk.lyngby.model.Appointment;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Setter
@Getter
@NoArgsConstructor
public class AppointmentDTO {

    private Integer id;
    private LocalDateTime appointmentDate;
    private String reason;
    private Set<PatientDTO> patients = new HashSet<>();

    public AppointmentDTO(LocalDateTime appointmentDate, String reason) {
        this.appointmentDate = appointmentDate;
        this.reason = reason;

    }

    public AppointmentDTO(Appointment appointment) {
        this.id = appointment.getAppointmentId();
        this.appointmentDate = appointment.getAppointmentDate();
        this.reason = appointment.getReason();

        if (appointment.getPatients() != null) {
            this.patients = appointment.getPatients().stream()
                    .map(PatientDTO::new)
                    .collect(Collectors.toSet());
        }
    }


    public static List<AppointmentDTO> toHotelDTOList(List<Appointment> appointments) {
        return appointments.stream()
                .map(AppointmentDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AppointmentDTO)) return false;

        AppointmentDTO other = (AppointmentDTO) o;

        return getId().equals(other.getId()) &&
                getReason().equals(other.getReason());
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getReason().hashCode();
        return result;
    }
}
