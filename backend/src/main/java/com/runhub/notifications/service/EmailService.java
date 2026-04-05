package com.runhub.notifications.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${application.mail.from:noreply@runhub.com}")
    private String fromAddress;

    @Value("${application.mail.from-name:RunHub}")
    private String fromName;

    @Value("${application.frontend-url:http://localhost:4200}")
    private String frontendUrl;

    /**
     * Sends the community invite email asynchronously.
     *
     * @param toEmail       recipient email address
     * @param invitedName   display username of the invited user
     * @param inviterName   display username of the person who sent the invite
     * @param communityName name of the community
     * @param inviteToken   UUID token for the invite link
     */
    @Async
    public void sendCommunityInviteEmail(String toEmail,
                                         String invitedName,
                                         String inviterName,
                                         String communityName,
                                         String inviteToken) {
        try {
            String inviteLink = frontendUrl + "/invites?token=" + inviteToken;
            String subject = inviterName + " invited you to join " + communityName + " on RunHub";
            String html = buildInviteEmailHtml(invitedName, inviterName, communityName, inviteLink);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress, fromName);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(html, true);

            mailSender.send(message);
            log.info("Community invite email sent to {} for community {}", toEmail, communityName);
        } catch (Exception e) {
            log.warn("Failed to send community invite email to {}: {}", toEmail, e.getMessage());
        }
    }

    private String buildInviteEmailHtml(String invitedName,
                                         String inviterName,
                                         String communityName,
                                         String inviteLink) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8" />
                  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
                  <title>Community Invite</title>
                </head>
                <body style="margin:0;padding:0;background-color:#0d1117;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',Roboto,Helvetica,Arial,sans-serif;">
                  <table width="100%%" cellpadding="0" cellspacing="0" border="0" style="background-color:#0d1117;">
                    <tr>
                      <td align="center" style="padding:40px 16px;">
                        <table width="600" cellpadding="0" cellspacing="0" border="0"
                               style="max-width:600px;width:100%%;background-color:#161b2e;border-radius:12px;overflow:hidden;border:1px solid #1e2d4a;">
                          <!-- Header -->
                          <tr>
                            <td style="background:linear-gradient(135deg,#1a56db 0%%,#7c3aed 100%%);padding:32px 40px;text-align:center;">
                              <span style="font-size:28px;font-weight:800;color:#ffffff;letter-spacing:-0.5px;">RunHub</span>
                              <p style="margin:8px 0 0;color:rgba(255,255,255,0.8);font-size:14px;">Your Running Community</p>
                            </td>
                          </tr>
                          <!-- Body -->
                          <tr>
                            <td style="padding:40px;">
                              <h1 style="margin:0 0 8px;font-size:22px;font-weight:700;color:#f0f6ff;">
                                You've been invited!
                              </h1>
                              <p style="margin:0 0 24px;font-size:15px;color:#8b949e;">
                                Hey <strong style="color:#c9d1d9;">%s</strong>,
                              </p>
                              <p style="margin:0 0 24px;font-size:15px;color:#8b949e;line-height:1.6;">
                                <strong style="color:#c9d1d9;">%s</strong> has invited you to join the
                                <strong style="color:#58a6ff;">%s</strong> community on RunHub.
                                Connect with fellow runners, share your activities, and grow together!
                              </p>
                              <!-- CTA Button -->
                              <table cellpadding="0" cellspacing="0" border="0" style="margin:0 0 32px;">
                                <tr>
                                  <td style="background:linear-gradient(135deg,#1a56db 0%%,#7c3aed 100%%);border-radius:8px;">
                                    <a href="%s"
                                       style="display:inline-block;padding:14px 32px;font-size:15px;font-weight:600;
                                              color:#ffffff;text-decoration:none;letter-spacing:0.3px;">
                                      View Invite &rarr;
                                    </a>
                                  </td>
                                </tr>
                              </table>
                              <p style="margin:0;font-size:13px;color:#484f58;line-height:1.6;">
                                Or copy this link into your browser:<br/>
                                <a href="%s" style="color:#58a6ff;word-break:break-all;">%s</a>
                              </p>
                            </td>
                          </tr>
                          <!-- Footer -->
                          <tr>
                            <td style="background-color:#0d1117;padding:24px 40px;border-top:1px solid #1e2d4a;text-align:center;">
                              <p style="margin:0;font-size:12px;color:#484f58;">
                                This invite was sent by a RunHub community admin.<br/>
                                If you did not expect this, you can safely ignore this email.
                              </p>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """.formatted(invitedName, inviterName, communityName, inviteLink, inviteLink, inviteLink);
    }
}
