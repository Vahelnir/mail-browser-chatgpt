package org.jaaumg;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import static org.jaaumg.CalendarQuickstart.*;

public class Main {

    public static void main(String[] args) throws GeneralSecurityException, IOException, ParseException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Calendar service =
                new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                        .setApplicationName(APPLICATION_NAME)
                        .build();
        Events events = new Events();
        Scanner sc = new Scanner(System.in);
        System.out.println("Digite o nome do evento.");
        String nome = sc.nextLine();
        System.out.println("Digite a data do evento (dd/MM/yyyy HH:mm).");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        LocalDateTime localDateTime = LocalDateTime.parse(sc.nextLine(), formatter);
        String data = localDateTime.format(DateTimeFormatter.ISO_DATE_TIME);
        System.out.println();
        Event event = new Event().setSummary(nome);
        EventDateTime eventDateTimeStart = new EventDateTime().setDateTime(new DateTime(data)).setTimeZone("America/Sao_Paulo");
        event.setStart(eventDateTimeStart);
        data = localDateTime.plusHours(1).format(DateTimeFormatter.ISO_DATE_TIME);
        EventDateTime eventDateTimeEnd = new EventDateTime().setDateTime(new DateTime(data)).setTimeZone("America/Sao_Paulo");
        event.setStart(eventDateTimeEnd);
        event = service.events().insert("primary", event).execute();
        sc.close();

    }
}
