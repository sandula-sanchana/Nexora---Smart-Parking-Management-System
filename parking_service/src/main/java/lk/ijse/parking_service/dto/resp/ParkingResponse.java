package lk.ijse.parking_service.dto.resp;

import lk.ijse.parking_service.entity.ParkingStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingResponse {

    private Long id;
    private String parkingName;
    private String location;
    private String address;
    private Integer totalSlots;
    private Integer availableSlots;
    private Double hourlyRate;
    private ParkingStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}