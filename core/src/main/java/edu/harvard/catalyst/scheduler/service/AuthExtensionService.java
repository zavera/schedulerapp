//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package edu.harvard.catalyst.scheduler.service;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import edu.harvard.catalyst.hccrc.core.util.ListUtils;
import edu.harvard.catalyst.scheduler.dto.PasswordResetDTO;
import edu.harvard.catalyst.scheduler.dto.UserDTO;
import edu.harvard.catalyst.scheduler.entity.BaseEntity;
import edu.harvard.catalyst.scheduler.entity.InstitutionRole;
import edu.harvard.catalyst.scheduler.entity.InstitutionRoleType;
import edu.harvard.catalyst.scheduler.entity.Role;
import edu.harvard.catalyst.scheduler.entity.User;
import edu.harvard.catalyst.scheduler.entity.UserSession;
import edu.harvard.catalyst.scheduler.persistence.AuthDAO;
import edu.harvard.catalyst.scheduler.persistence.ResourceDAO;
import edu.harvard.catalyst.scheduler.persistence.StudyDAO;
import edu.harvard.catalyst.scheduler.security.SchedulerUserDetails;
import edu.harvard.catalyst.scheduler.util.MailHandler;
import edu.harvard.catalyst.scheduler.util.MailMessageBuilder;
import edu.harvard.catalyst.scheduler.util.OneWayPasswordEncoder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.stereotype.Component;

@Component
public class AuthExtensionService implements ServiceHelpers {
    private static final Logger LOGGER = Logger.getLogger(SchedulerDbAuthenticationProvider.class);
    private final AuthService authService;
    private final MailHandler mailHandler;
    private final AuthDAO authDAO;
    private final StudyDAO studyDAO;
    private final ResourceDAO resourceDAO;
    private final AuditService auditService;

    @Autowired
    public AuthExtensionService(AuthService authService, AuthDAO authDAO, StudyDAO studyDAO, ResourceDAO resourceDAO, AuditService auditService, MailHandler mailHandler) {
        this.authService = authService;
        this.authDAO = authDAO;
        this.studyDAO = studyDAO;
        this.resourceDAO = resourceDAO;
        this.auditService = auditService;
        this.mailHandler = mailHandler;
    }

    public User findUserByEcommonsId(String ecommonsId) {
        return this.authDAO.findUserByEcommonsId(ecommonsId);
    }

    public SchedulerUserDetails authenticateUser(String username, String password, String httpSession, String ipAddress) {
        User user = this.authDAO.authenticate(username, password, httpSession);
        if (user == null) {
            throw new BadCredentialsException("Invalid Username or Password");
        } else if (!user.getActive()) {
            throw new DisabledException("User inactive");
        } else {
            user.setAuthStatus(1);
            UserSession userSession = this.authDAO.createUserSession(httpSession, ipAddress, user);
            return new SchedulerUserDetails(user, userSession);
        }
    }

    public boolean doPasswordReset(PasswordResetDTO passwordResetDTO) {
        User user = this.authDAO.findUserByEmail(passwordResetDTO.getEmail());
        if (user != null) {
            return isActiveDirectoryUsername(user.getEcommonsId()) ? false : this.authService.doPasswordReset(passwordResetDTO);
        } else {
            return user != null;
        }
    }

    public UserDTO doRegisterUser(UserDTO dto, String contextPath, String remoteHost, String serverName, int serverPort) {
        User user = new User();
        if (isActiveDirectoryUsername(dto.getEcommonsId())) {
            dto.setPassword("");
        } else if (!this.authService.testPassword(dto.getPassword())) {
            dto.setResult(false);
            dto.setErrorMsg("Insufficient Password");
            return dto;
        }

        this.setupUser(dto, user, InstitutionRoleType.ROLE_STUDY_STAFF);
        if (!dto.isResult()) {
            return dto;
        } else {
            String url = this.authService.makeUrl(contextPath, serverName, serverPort);
            List<User> superAdmins = this.authDAO.findSuperAdminByInstitutionRole();
            String superAdminsubject = "New User Registered: " + user.getFirstName() + " " + user.getLastName();
            String subject = this.authService.makeEmailUserSubject(user);
            Stream var10000 = superAdmins.stream().map(this.toMessage(superAdminsubject, this.authService.makeNewUserAdminMessage(user, url)));
            MailHandler var10001 = this.mailHandler;
            Objects.requireNonNull(var10001);
           // var10000.forEach(var10001::sendMandatoryEmails);
            SimpleMailMessage message = (SimpleMailMessage)this.toMessage(subject, this.authService.makeNewUserMessage(subject, user, url, true)).apply(user);
            this.mailHandler.sendMandatoryEmails(message);
            this.auditService.logUserActivity(remoteHost, user, user, "CREATE USER", (String)null, (String)null);
            return dto;
        }
    }

    public UserDTO createUser(UserDTO dto, User creatingUser, String contextPath, String remoteHost, String serverName, int serverPort) {
        User user = new User();
        if (isActiveDirectoryUsername(dto.getEcommonsId())) {
            dto.setPassword("");
        } else if (dto.isGenerateNewPassword()) {
            dto.setPassword(this.authService.generatePassword());
        } else if (!this.authService.testPassword(dto.getPassword())) {
            dto.setResult(false);
            dto.setErrorMsg("Insufficient Password");
            return dto;
        }

        this.setupUser(dto, user, (InstitutionRoleType)null);
        if (!dto.isResult()) {
            return dto;
        } else {
            String url = this.authService.makeUrl(contextPath, serverName, serverPort);
            String subject = this.authService.makeEmailUserSubject(user);
            String emailContent = this.authService.makeNewUserMessage(subject, user, url, false);
            this.mailHandler.sendOptionalEmails((new MailMessageBuilder()).to(user.getEmail()).subject(subject).text(emailContent).build());
            this.auditService.logUserActivity(remoteHost, user, creatingUser, "CREATE USER", (String)null, (String)null);
            return dto;
        }
    }

    public UserDTO updateUser(UserDTO dto, User creatingUser, String ipAddress) {
        User user = this.authDAO.findUserById(dto.getId());
        InstitutionRoleType institutionRoleType = dto.getInstitutionRoleType();
        if (!this.authService.isValidUser(user, dto)) {
            return dto;
        } else {
            if (isActiveDirectoryUsername(dto.getEcommonsId())) {
                String encryptedPassword = OneWayPasswordEncoder.getInstance().encode("", user.getSalt());
                if (!encryptedPassword.equals(user.getPassword())) {
                    user.setPassword(encryptedPassword);
                }
            } else if (!this.authService.processPassword(user, dto)) {
                return dto;
            }

            this.logUpdateUserData(dto, creatingUser, ipAddress, user, institutionRoleType);
            this.authService.setSomeUserProperties(dto, user, institutionRoleType);
            this.authDAO.updateEntity(user);
            dto.setResult(true);
            return dto;
        }
    }

    public UserDTO updatePassword(UserDTO dto, User usr) {
        User user = this.authDAO.findUserById(usr.getId());
        if (isActiveDirectoryUsername(user.getEcommonsId())) {
            dto.setErrorMsg("Active directory password cannot be changed in Scheduler");
            dto.setResult(false);
            return dto;
        } else {
            return this.authService.updatePassword(dto, usr);
        }
    }

    private void setupUser(UserDTO dto, User user, InstitutionRoleType institutionRoleType) {
        if (!this.authService.isBadEmailOrEcommonsId(dto)) {
            String salt = UUID.randomUUID().toString();
            user.setSalt(salt);
            String encodedPassword = OneWayPasswordEncoder.getInstance().encode(dto.getPassword(), salt);
            user.setPassword(encodedPassword);
            InstitutionRoleType roleType = null != institutionRoleType ? institutionRoleType : dto.getInstitutionRoleType();
            this.authService.setSomeUserProperties(dto, user, roleType);
            this.authDAO.createEntity(user);
            dto.setResult(true);
        }
    }

    private void logUpdateUserData(UserDTO dto, User creatingUser, String ipAddress, User user, InstitutionRoleType institutionRoleType) {
        String institutionRole = this.studyDAO.findInstitutionRoleByType(institutionRoleType).getName();
        Integer var10001 = dto.getRoleId();
        StudyDAO var10002 = this.studyDAO;
        Objects.requireNonNull(var10002);
        String role = this.lookupFieldById(var10001, var10002::findRoleById);
        var10001 = dto.getInstitutionId();
        ResourceDAO var14 = this.resourceDAO;
        Objects.requireNonNull(var14);
        String institution = this.lookupFieldById(var10001, var14::findInstitutionById);
        List<Optional<String>> previousDataComponents = Lists.newArrayList(new Optional[]{this.makeFieldString("First Name", dto.getFirstName(), user.getFirstName()), this.makeFieldString("Middle Name", dto.getMiddleName(), user.getMiddleName()), this.makeFieldString("Last Name", dto.getLastName(), user.getLastName()), this.makeFieldString("Ecommons Id", dto.getEcommonsId(), user.getEcommonsId()), this.makeFieldString("Primary Phone", dto.getPrimaryPhone(), user.getPrimaryPhone()), this.makeFieldString("Institution Role", institutionRole, user.getInstitutionRole(), institutionRoleType, InstitutionRole::getType), this.makeFieldString("Role", role, user.getRole(), dto.getRoleId(), Role::getType), this.makeFieldString("Institution", institution, user.getInstitution(), dto.getInstitutionId(), BaseEntity::getId), this.makeFieldString("Notification Email", dto.getNotificationEmail(), user.getNotificationEmail())});
        List<Optional<String>> otherPreviousDataComponents = Lists.newArrayList(new Optional[]{this.makeFieldString("Secondary Phone", dto.getSecondaryPhone(), user.getSecondaryPhone()), this.makeFieldString("Email", dto.getEmail(), user.getEmail()), this.makeFieldString("Fax", dto.getFax(), user.getFax()), this.makeFieldString("Pager", dto.getPager(), user.getPager()), this.makeFieldString("Division", dto.getDivision(), user.getDivision()), this.makeFieldString("Department", dto.getDepartment(), user.getDepartment()), this.makeFieldString("Credential", dto.getCredential(), user.getCredential()), this.makeFieldString("Faculty Rank", dto.getFacultyRank(), user.getFacultyRank())});
        Joiner joiner = Joiner.on("");
        String previousData = joiner.join(ListUtils.flatten(previousDataComponents));
        String otherPreviousData = joiner.join(ListUtils.flatten(otherPreviousDataComponents));
        this.auditService.logUserActivity(ipAddress, user, creatingUser, "UPDATE USER", previousData, otherPreviousData);
    }

    private Function<User, SimpleMailMessage> toMessage(String subject, String body) {
        return (u) -> {
            return (new MailMessageBuilder()).to(u.getEmail()).subject(subject).text(body).build();
        };
    }

    private static boolean isActiveDirectoryUsername(String username) {
        return Pattern.matches("^\\w+\\\\\\w+$", username);
    }
}
