package preparation;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CreateBookingBody {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class BookingDates {
        private String checkin;
        private String checkout;
    }

    private String firstname;
    private String lastname;
    private int totalprice;
    private boolean depositpaid;
    public BookingDates bookingdates;
    private String additionalneeds;


}
