package org.guerrero.bruno.clientg.tasks;

import lombok.RequiredArgsConstructor;
import org.guerrero.bruno.clientg.entities.Client;
import org.guerrero.bruno.clientg.repositories.ClientRepository;
import org.guerrero.bruno.clientg.services.PaymentService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentTask {
    private final ClientRepository clientRepository;
    private final PaymentService paymentService;

    //AL INICIO DE CADA DIA
    @Scheduled(cron = "0 0 0 * * ?") // Todos los días a las 12 AM
    public void executeDailyBillingCheck() {
        LocalDate todayDate = LocalDate.now();
        int todayDay = todayDate.getDayOfMonth();
        int lastDayOfTheMonth = todayDate.lengthOfMonth();

        List<Client> clientsToBill;

        if(todayDay == lastDayOfTheMonth){

            // Solo trae a los que les toca hoy Y tienen el automatico en true
            clientsToBill = clientRepository.findAllByAutomaticPaymentDayGreaterThanEqualAndAutomaticPaymentAvailableTrueAndIsActiveTrue(lastDayOfTheMonth);

        }else{
            clientsToBill = clientRepository.findAllByAutomaticPaymentDayAndAutomaticPaymentAvailableTrueAndIsActiveTrue(todayDay);

        }

        for(Client client : clientsToBill){
            paymentService.generateAutomaticPayment(client);

        }
    }
}
