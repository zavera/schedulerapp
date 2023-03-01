//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package edu.harvard.catalyst.scheduler.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javax.inject.Singleton;
import edu.harvard.catalyst.scheduler.dto.BooleanResultDTO;
import edu.harvard.catalyst.scheduler.dto.PasswordResetDTO;
import edu.harvard.catalyst.scheduler.dto.UserDTO;
import edu.harvard.catalyst.scheduler.service.AuthExtensionService;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Path("/appExtension")
@Singleton
@Component
public class AppExtensionResource extends BaseResource {
    private AuthExtensionService authExtensionService;

    @Autowired
    public AppExtensionResource(AuthExtensionService authExtensionService) {
        this.authExtensionService = authExtensionService;
    }

    @POST
    @Path("/registerUser")
    public String registerUser(@FormParam("data") String data) {
        Map<String, String> map = new HashMap();
        Gson gson = (new GsonBuilder()).registerTypeAdapter(UserDTO.class, new UserDTO()).create();
        UserDTO dto = (UserDTO)gson.fromJson(data, UserDTO.class);
        dto = this.authExtensionService.doRegisterUser(dto, this.getContextPath(), this.getRemoteHost(), this.getServerName(), this.getServerPort());
        if (!dto.isResult()) {
            map.put("errorMsg", dto.getErrorMsg());
        }

        return gson.toJson(dto);
    }

    @POST
    @Path("/unAuthenticatedPasswordReset")
    public String unAuthenticatedPasswordReset(@FormParam("data") String data) {
        PasswordResetDTO passwordResetDTO = (PasswordResetDTO)this.gson.fromJson(data, PasswordResetDTO.class);
        boolean result = this.authExtensionService.doPasswordReset(passwordResetDTO);
        BooleanResultDTO booleanResultDTO = new BooleanResultDTO();
        booleanResultDTO.setResult(result);
        return this.gson.toJson(booleanResultDTO);
    }
}
