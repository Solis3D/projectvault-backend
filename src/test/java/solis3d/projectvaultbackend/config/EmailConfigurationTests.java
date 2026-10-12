package solis3d.projectvaultbackend.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.mail.autoconfigure.MailSenderAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import solis3d.projectvaultbackend.services.EmailService;

import java.io.IOException;
import java.io.UncheckedIOException;
import solis3d.projectvaultbackend.entities.AppUser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

class EmailConfigurationTests {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withInitializer(context -> {
                        MutablePropertySources propertySources =
                                context.getEnvironment().getPropertySources();

                        propertySources.remove(
                                StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME
                        );
                        propertySources.remove(
                                StandardEnvironment.SYSTEM_PROPERTIES_PROPERTY_SOURCE_NAME
                        );

                        try {
                            propertySources.addLast(
                                    new ResourcePropertySource(
                                            "classpath:application.properties"
                                    )
                            );
                        } catch (IOException ex) {
                            throw new UncheckedIOException(ex);
                        }
                    })
                    .withPropertyValues("frontend.url=http://localhost:5173")
                    .withConfiguration(
                            AutoConfigurations.of(MailSenderAutoConfiguration.class)
                    )
                    .withUserConfiguration(EmailService.class);

    @Test
    void shouldStartMailComponentsWithoutSmtpVariables() {
        this.contextRunner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(context).hasSingleBean(EmailService.class);
            assertThat(context).hasSingleBean(JavaMailSender.class);

            assertThat(context.getEnvironment().getProperty("MAIL_HOST"))
                    .isNull();

            assertThat(context.getEnvironment()
                    .getProperty("mail.enabled", Boolean.class))
                    .isFalse();

            assertThat(context.getEnvironment().getProperty("spring.mail.host"))
                    .isEqualTo("localhost");
        });
    }

    @Test
    void shouldSkipWelcomeEmailWhenMailIsDisabled() {
        JavaMailSender mailSender = mock(JavaMailSender.class);

        this.contextRunner
                .withBean(JavaMailSender.class, () -> mailSender)
                .run(context -> {
                    assertThat(context).hasNotFailed();

                    AppUser artist = new AppUser();
                    artist.setFirstName("Alice");
                    artist.setEmail("artist@projectvault.local");

                    context.getBean(EmailService.class)
                            .sendWelcomeEmail(artist);

                    verifyNoInteractions(mailSender);
                });
    }
}