package lk.ijse.parking_service.dto.req;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingSaveRequest {

    private String parkingName;
    private String location;
    private String address;
    private Integer totalSlots;
    private Double hourlyRate;
}