package lk.ijse.parking_service.dto.req;

import lk.ijse.parking_service.entity.ParkingStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingUpdateRequest {

    private String parkingName;
    private String location;
    private String address;
    private Integer totalSlots;
    private Double hourlyRate;
    private ParkingStatus status;
}