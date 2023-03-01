//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package edu.harvard.catalyst.scheduler.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.inject.Singleton;
import edu.harvard.catalyst.scheduler.dto.UserDTO;
import edu.harvard.catalyst.scheduler.entity.InstitutionRoleType;
import edu.harvard.catalyst.scheduler.security.AuthorizedRoles;
import edu.harvard.catalyst.scheduler.service.AuthExtensionService;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Path("/managementExtension")
@Singleton
@Component
public class ManagementExtensionResource extends SecuredResource {
    private AuthExtensionService service;

    @Autowired
    public ManagementExtensionResource(AuthExtensionService service) {
        this.service = service;
    }

    ManagementExtensionResource() {
    }

    @POST
    @Path("/updatePassword")
    @AuthorizedRoles({InstitutionRoleType.ROLE_SUPER_ADMIN, InstitutionRoleType.ROLE_RESOURCE_MANAGER, InstitutionRoleType.ROLE_SCHEDULER, InstitutionRoleType.ROLE_STUDY_STAFF, InstitutionRoleType.ROLE_FRONT_DESK, InstitutionRoleType.ROLE_GENERAL_VIEW})
    public String updatePassword(@FormParam("data") String data) {
        Gson gson = (new GsonBuilder()).registerTypeAdapter(UserDTO.class, new UserDTO()).create();
        UserDTO dto = (UserDTO)gson.fromJson(data, UserDTO.class);
        dto = this.service.updatePassword(dto, this.getUser());
        return gson.toJson(dto);
    }

    @POST
    @Path("/createUser")
    @AuthorizedRoles({InstitutionRoleType.ROLE_SUPER_ADMIN})
    public String createUser(@FormParam("data") String data) {
        Gson gson = (new GsonBuilder()).registerTypeAdapter(UserDTO.class, new UserDTO()).create();
        UserDTO dto = (UserDTO)gson.fromJson(data, UserDTO.class);
        dto = this.service.createUser(dto, this.getUser(), this.getContextPath(), this.getRemoteHost(), this.getServerName(), this.getServerPort());
        return gson.toJson(dto);
    }

    @POST
    @Path("/updateUser")
    @AuthorizedRoles({InstitutionRoleType.ROLE_SUPER_ADMIN})
    public String updateUser(@FormParam("data") String data) {
        Gson gson = (new GsonBuilder()).registerTypeAdapter(UserDTO.class, new UserDTO()).create();
        UserDTO dto = (UserDTO)gson.fromJson(data, UserDTO.class);
        dto = this.service.updateUser(dto, this.getUser(), this.getRemoteHost());
        return gson.toJson(dto);
    }
}
