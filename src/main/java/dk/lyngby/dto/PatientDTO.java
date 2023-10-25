package dk.lyngby.dto;

import dk.lyngby.model.Patient;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class PatientDTO {
    private Integer patientId; // Tilføj dette felt
    private String name;
    private String species;
    private String medicalHistory;

    public PatientDTO(Patient patient) {
        this.patientId = patient.getPatientId(); // Sæt patientId baseret på din databasekolonne
        this.name = patient.getName();
        this.species = patient.getSpecies();
        this.medicalHistory = patient.getMedicalHistory();
    }
    // denne metode tager imod en liste af Room objekter og laver dem om til RoomDto objekter
    public static List<PatientDTO> toRoomDTOList(List<Patient> patients) {
        return List.of(patients.stream()
                .map(PatientDTO::new)
                .toArray(PatientDTO[]::new));
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PatientDTO patientDTO)) return false;
        //her tjekker vi om to RoomDto'er er ens dvs om de har samme roomNumber, roomPrice og roomType
        if (getName() != null ? !getName().equals(patientDTO.getName()) : patientDTO.getName() != null)
            return false;
        if (getSpecies() != null ? !getSpecies().equals(patientDTO.getSpecies()) : patientDTO.getSpecies() != null)
            return false;
        if (getMedicalHistory() != null ? !getMedicalHistory().equals(patientDTO.getMedicalHistory()) : patientDTO.getMedicalHistory() != null)
            return false;
        return false;
    }


    @Override
    public int hashCode() {
        //her tjekker så om de forskellige værdier er null og hvis de ikke er så laver vi en hashkode ud fra dem
        //det bruger vi til at tjekke om to RoomDto'er er ens
        int result = getName() != null ? getName().hashCode() : 0;
        // 31 er et primtal, som bruges til at lave en hashkode
        result = 31 * result + (getSpecies() != null ? getSpecies().hashCode() : 0);
        result = 31 * result + (getMedicalHistory() != null ? getMedicalHistory().hashCode() : 0);
        return result;
    }
}
