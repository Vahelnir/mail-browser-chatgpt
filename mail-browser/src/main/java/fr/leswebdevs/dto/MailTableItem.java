package fr.leswebdevs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MailTableItem {
    private String subject;
    private String from;
    private Date receivedAt;
}
