package solis3d.projectvaultbackend.services;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import solis3d.projectvaultbackend.entities.AppUser;
import org.springframework.beans.factory.annotation.Value;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender javaMailSender;

    @Value("${frontend.url}")
    private String frontendUrl;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendWelcomeEmail(AppUser appUser) {
        try {
            MimeMessage message = this.javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(appUser.getEmail());
            helper.setSubject("Welcome to ProjectVault!");

            String htmlContent = """
                <!DOCTYPE html>
                <html>
                <body style="margin:0; padding:0; background-color:#131313; font-family:Arial, sans-serif; color:#e5e2e1;">
                    <table width="100%" cellpadding="0" cellspacing="0" style="background-color:#131313; padding:32px 16px;">
                        <tr>
                            <td align="center">
                                <table width="100%" cellpadding="0" cellspacing="0" style="max-width:640px; background-color:#1c1b1b; border:1px solid #2a2a2a;">
                                    <tr>
                                        <td style="padding:32px 32px 20px;">
                                            <h1 style="margin:0; color:#e5e2e1; font-size:28px;">
                                                Project<span style="color:#00ffc2;">Vault</span>
                                            </h1>
                                        </td>
                                    </tr>

                                    <tr>
                                        <td style="padding:0 32px 24px;">
                                            <p style="margin:0 0 12px; color:#b9cbc1; font-size:13px; text-transform:uppercase; letter-spacing:1px;">
                                                Account created
                                            </p>

                                            <h2 style="margin:0 0 18px; color:#e5e2e1; font-size:24px;">
                                                Welcome, %s!
                                            </h2>

                                            <p style="margin:0 0 16px; color:#b9cbc1; font-size:16px; line-height:1.6;">
                                                Your ProjectVault account has been created successfully.
                                            </p>

                                            <p style="margin:0 0 24px; color:#b9cbc1; font-size:16px; line-height:1.6;">
                                                You can now start building your digital art portfolio, organize your projects,
                                                document your creative workflow and publish your best work to the gallery.
                                            </p>

                                            <div style="margin:28px 0;">
                                            <a href="%s/portfolio" style="display:inline-block; background-color:#00ffc2; color:#002116; text-decoration:none; padding:14px 22px; font-weight:bold;">
                                                    Start building your portfolio
                                                </a>
                                            </div>

                                            <p style="margin:0; color:#b9cbc1; font-size:14px; line-height:1.6;">
                                                The ProjectVault Team
                                            </p>
                                        </td>
                                    </tr>

                                    <tr>
                                        <td style="padding:20px 32px; border-top:1px solid #2a2a2a;">
                                            <p style="margin:0; color:#6f7f77; font-size:12px;">
                                                This is an automatic email sent after your registration.
                                            </p>
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """.formatted(appUser.getFirstName(), this.frontendUrl);

            helper.setText(htmlContent, true);

            this.javaMailSender.send(message);

        } catch (Exception ex) {
            logger.error("Errore nell'inivio della mail a {}", appUser.getEmail(), ex);
        }
    }
}

