import com.example.tickets.IncidentTicket;
import com.example.tickets.TicketService;

import java.util.List;

public class TryIt {

    public static void main(String[] args) {
        TicketService service = new TicketService();

        IncidentTicket t1 = service.createTicket(
                "TCK-1001",
                "reporter@example.com",
                "Payment failing on checkout"
        );

        System.out.println("Created: " + t1);

        // create NEW instances instead of mutating
        IncidentTicket t2 = service.assign(t1, "agent@example.com");
        IncidentTicket t3 = service.escalateToCritical(t2);

        System.out.println("\nOriginal remains unchanged: " + t1);
        System.out.println("After assign: " + t2);
        System.out.println("After escalation: " + t3);

        // Try external mutation
        List<String> tags = t3.getTags();
        try {
            tags.add("HACKED_FROM_OUTSIDE");
        } catch (UnsupportedOperationException e) {
            System.out.println("\nTags are immutable from outside ✔");
        }

        System.out.println("\nFinal ticket: " + t3);
    }
}