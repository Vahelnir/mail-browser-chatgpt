package fr.leswebdevs.adapter;

import fr.leswebdevs.dto.MailTableItem;
import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;

import java.util.Arrays;
import java.util.stream.Collectors;

public class MailTableItemAdapter {
    public static MailTableItem from(Message message) throws MessagingException {
        return MailTableItem.builder()
            .subject(message.getSubject())
            .receivedAt(message.getReceivedDate())
            .from(
                Arrays.stream(message.getFrom())
                    .map(Address::toString)
                    .collect(Collectors.joining(","))
            )
            .build();
    }
}
