package lk.ijse.parking_service.dto.req;

import lk.ijse.parking_service.entity.ParkingStatus;
import lombok.*;

import java.time.LocalTime;

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
    private String city;
    private String zone;
    private Double latitude;
    private Double longitude;
    private Long ownerId;
    private String description;
    private String contactNumber;
    private LocalTime openingTime;
    private LocalTime closingTime;
}