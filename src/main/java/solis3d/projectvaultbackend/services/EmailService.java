package solis3d.projectvaultbackend.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import solis3d.projectvaultbackend.entities.AppUser;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendWelcomeEmail(AppUser appUser) {
        try{
            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(appUser.getEmail());
            message.setSubject("Benvenuto in Project Vault!");
            message.setText(
                    "Ciao " + appUser.getFirstName() + ",\n\n" +
                            "Benvenuto in Project Vault!\n\n" +
                            "Il tuo account è stato creato correttamente. " +
                            "Ora potrai finalmente iniziare ad organizzare i tuoi progetti digitali e a costruire il tuo portfolio!\n\n" +
                            "Il Team di ProjectVault"
            );

            this.javaMailSender.send(message);
        } catch (Exception ex){
            logger.error("Errore nell'invio della mail a {}", appUser.getEmail(), ex);
        }
    }
}

